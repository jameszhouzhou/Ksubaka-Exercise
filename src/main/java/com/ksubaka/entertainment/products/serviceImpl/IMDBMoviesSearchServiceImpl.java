package com.ksubaka.entertainment.products.serviceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.ksubaka.entertainment.products.dao.Movie;
import com.ksubaka.entertainment.products.dao.Movies;
import com.ksubaka.entertainment.products.service.ProductsSearchService;
import com.ksubaka.entertainment.products.utils.Utilities;

public class IMDBMoviesSearchServiceImpl extends ProductsSearchService<Movies> {

	private String imdbUrl;
	
	public IMDBMoviesSearchServiceImpl(String imdbUrl){
		this.imdbUrl = imdbUrl;
	}
	
	@Override
	public void launchUp(Scanner scan){
		System.out.println("IMDB APIs helps you to find movies. Please enter movie names, for example,   Mission Impossible   or   Mission impossible,Indiana jones. \n");		
		System.out.println("Or enter   exit    to exist program. \n");
		
		String typeIn = scan.nextLine();
		if(typeIn==null || typeIn.length()==0){
			System.err.println("I got nothing, please try again. \n");
			launchUp(scan);
		}else if(Utilities.EXIT.equalsIgnoreCase(typeIn)){
			System.out.println("Terminating the program ...");
			System.exit(3);
		}
		
		Map<String, Movies> result = newRequest(typeIn, null);
		for(String key:result.keySet()){
			System.out.println("-------------------------------------------------------------------");
			Movies movies = result.get(key);
			
			if(Utilities.IMDB_RESPONSE_FALSE.equalsIgnoreCase(movies.getResponse()))
				System.err.println(key+": "+movies.getError()+"\n");
			if(Utilities.IMDB_RESPONSE_OK.equalsIgnoreCase(movies.getResponse())){
				for(Movie movie:movies.getMovies())
					System.out.println(movie.getTitle()+" : "+movie.getYear()+"\n");					
			}
		}
		
		launchUp(scan);
	}
	
	@Override
	public Map<String, Movies> newRequest(String typeIn, String artist) {
		typeIn = Utilities.formateReuqestString(typeIn);
		Map<String, Movies> searchResults = null;
		try {
			searchResults = searchProducts(typeIn.split(Utilities.COMMA), null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return searchResults;
	}

	@Override
	public Map<String, Movies> searchProducts(String[] productNames, String artist) throws InterruptedException, ExecutionException, TimeoutException{
		ExecutorCompletionService<Movies> completionService = new ExecutorCompletionService<Movies>(getExecutor());
		Map<String, Movies> results = new HashMap<String, Movies>();
		
		for(String name:productNames){
			String imdbProductsUrl = Utilities.generateURLforIMDB(imdbUrl, name);
			Callable<Movies> task = new Guider(imdbProductsUrl, Movies.class, getProxy(), getProvider());
			Movies movies = completionService.submit(task).get(1000*10, TimeUnit.SECONDS);
			
			results.put(name, movies);
		}
		
		return results;
	}
}
