package com.ksubaka.entertainment.products.service;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.ksubaka.entertainment.products.utils.Utilities;

/*
 * @author: James ZHou
 * 
 * This is the service contract class contains below APIs for the implementors:
 * 1. launchUp, this is the entry point and will call newRequest API
 * 2. newRequest, this API handles the users typein. Movie names for IMDB service and album names + artist name for LastFM service
 * 3. searchProducts, this API will trigger up threads - callables to handle the http requests
 * 
 * the inner class, Guider here is a Callable implementation
 * 
 */
public abstract class ProductsSearchService<T> {

	private HttpHost proxy;
	private CredentialsProvider provider;
	private ExecutorService executor;
	
	public abstract void launchUp(Scanner scan);
	public abstract Map<String, T> newRequest(String typeIn, String artist);
	public abstract Map<String, T> searchProducts(String[] productNames, String artist) throws InterruptedException, ExecutionException, TimeoutException;

	protected class Guider implements Callable<T>{

		private String url;
		private Class<T> clazz;
		private HttpHost proxy;
		private CredentialsProvider credsProvider;
		
		HttpGet get;
		RequestConfig config;
		CloseableHttpResponse response;
		CloseableHttpClient httpClient;
				
		public Guider(String url, Class<T> clazz, HttpHost proxy, CredentialsProvider credsProvider){
			this.url = url;	
			this.clazz = clazz;
			this.proxy = proxy;
			this.credsProvider = credsProvider;
		}
		
		public T call() throws Exception {
			System.out.println("Visiting the URL: "+url);
			
			httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			try{			
				config= RequestConfig.custom().setProxy(proxy).build();
				get = new HttpGet(url);
				get.setConfig(config);
				
				response = httpClient.execute(get);			
				InputStream responseXml = response.getEntity().getContent();
				
				return Utilities.unmarshalResponseToProducts(responseXml, clazz);				
			}catch(Exception e){
				throw new RuntimeException(String.format("Error in fetching via http, %s", e.getStackTrace()));
			}
			finally{
				httpClient.close();
				response.close();
			}
		}		
	}

	public HttpHost getProxy() {
		return proxy;
	}
	
	public void setProxy(HttpHost proxy) {
		this.proxy = proxy;
	}
	
	public CredentialsProvider getProvider() {
		return provider;
	}
	
	public void setProvider(CredentialsProvider provider) {
		this.provider = provider;
	}

	public ExecutorService getExecutor() {
		return executor;
	}
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}
}
