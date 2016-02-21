package com.ksubaka.entertainment.products.main;

import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ksubaka.entertainment.products.service.ProductsSearchService;
import com.ksubaka.entertainment.products.serviceImpl.IMDBMoviesSearchServiceImpl;
import com.ksubaka.entertainment.products.serviceImpl.LastFMAlbumSearchServiceImpl;
import com.ksubaka.entertainment.products.utils.Utilities;

public class StartMe {
	
	public static void main(String[] args){
		StartMe startMe = new StartMe();
		
		ApplicationContext context = new ClassPathXmlApplicationContext("modules.spring-context.xml");
		Scanner scan = new Scanner(System.in);
		
		Properties properties = System.getProperties();
		
		HttpHost proxy = null;
		CredentialsProvider provider=null;
		ProductsSearchService searcher = null;
		
		boolean needProxyAndAuthentication = properties.containsKey("proxyHost") && 
											 properties.containsKey("proxyPort") &&
											 properties.containsKey("authId")    &&
											 properties.containsKey("authPassword");
		
		if(needProxyAndAuthentication)
			startMe.initProxy(proxy, provider, properties);
		
		try{
			System.out.println("Thanks for using this tool to search movies or alblums! \n");			
			Thread.sleep(1000);
			
			System.out.println("Please enter   imdb   or   lastfm   to find moves or albums. Enter   exit   to exit the program.");			
			String api = scan.nextLine();
			
			if(Utilities.API_IMDB.equalsIgnoreCase(api))
				searcher = context.getBean("IMDBMoviesSearchService", IMDBMoviesSearchServiceImpl.class);
			else if(Utilities.API_LASTFM.equalsIgnoreCase(api))
				searcher = context.getBean("LastFMAlbumSearchService", LastFMAlbumSearchServiceImpl.class);
			else if(api == null || api.length()==0){
				System.err.println("I got nothing. Terminating the program ...");
				System.exit(3);
			}else if(Utilities.EXIT.equalsIgnoreCase(api)){
				System.out.println("Terminating the program ...");
				System.exit(3);
			}else{
				System.err.println("You entered something I dont recognize. Terminating the program ...");
				System.exit(3);
			}
			
			ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(System.getProperty("thread.count", "2")));
			
			//if users entered a valid api, then try to invoke IMDB or LastFM service
			startMe.invokeService(searcher, executor, proxy, provider, scan);			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void initProxy(HttpHost proxy, CredentialsProvider provider, Properties properties){
		proxy = new HttpHost(properties.getProperty("proxyHost"), Integer.parseInt(properties.getProperty("proxyPort")));
		provider = new BasicCredentialsProvider();
		AuthScope scope = new AuthScope(properties.getProperty("proxyHost"), Integer.parseInt(properties.getProperty("proxyPort")));
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(properties.getProperty("authId"), properties.getProperty("authPassword"));
		provider.setCredentials(scope, credentials);
	}
	
	public void invokeService(ProductsSearchService searcher, ExecutorService executor, HttpHost proxy, CredentialsProvider provider, Scanner scan){
		searcher.setExecutor(executor);
		searcher.setProxy(proxy);
		searcher.setProvider(provider);
		
		searcher.launchUp(scan);
	}
}
