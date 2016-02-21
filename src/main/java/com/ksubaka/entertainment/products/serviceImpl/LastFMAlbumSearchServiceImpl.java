package com.ksubaka.entertainment.products.serviceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.ksubaka.entertainment.products.dao.Album;
import com.ksubaka.entertainment.products.dao.Albums;
import com.ksubaka.entertainment.products.service.ProductsSearchService;
import com.ksubaka.entertainment.products.utils.Utilities;

public class LastFMAlbumSearchServiceImpl extends ProductsSearchService<Albums> {

	private String lastFMUrl;
		
	public LastFMAlbumSearchServiceImpl(String lastFMUrl) {
		this.lastFMUrl = lastFMUrl;
	}

	@Override
	public void launchUp(Scanner scan) {
		System.out.println("LastFM APIs helps you to find albums. Please enter the artist name first (ONE name Only), for example,   justin bieber. \n");		
		System.out.println("Or enter   exit    to exist program. \n");
		
		String typeIn = scan.nextLine();
		if(typeIn==null || typeIn.length()==0){
			System.err.println("I got nothing, please try again. \n");
			launchUp(scan);
		}else if(Utilities.EXIT.equalsIgnoreCase(typeIn)){
			System.out.println("Terminating the program ...");
			System.exit(3);
		}
		
		System.out.println("Please enter albums of "+typeIn);
		String albumNames = scan.nextLine();
		
		if(albumNames==null || albumNames.length()==0){
			System.err.println("I got nothing, please try again. \n");
			launchUp(scan);
		}else if(Utilities.EXIT.equalsIgnoreCase(albumNames)){
			System.out.println("Terminating the program ...");
			System.exit(3);
		}
		
		Map<String, Albums> result = newRequest(albumNames,typeIn);
		for(String key:result.keySet()){
			System.out.println("-------------------------------------------------------------------");
			Albums albums = result.get(key);
			
			if(Utilities.LASTFM_RESPONSE_FALSE.equalsIgnoreCase(albums.getStatus()))
				System.err.println(key+": "+albums.getError()+"\n");
			if(Utilities.LASTFM_RESPONSE_OK.equalsIgnoreCase(albums.getStatus())){
				for(Album album:albums.getAlbums())
					System.out.println(album.getArtist()+" : "+album.getName()+" : "+album.getUrl()+"\n");					
			}
		}		
		launchUp(scan);
	}
	
	@Override
	public Map<String, Albums> newRequest(String typeIn, String artist) {		
		artist = Utilities.formateReuqestString(artist);
		typeIn = Utilities.formateReuqestString(typeIn);
		
		Map<String, Albums> searchResults = null;
		try {
			searchResults = searchProducts(typeIn.split(Utilities.COMMA), artist);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return searchResults;
	}

	@Override
	public Map<String, Albums> searchProducts(String[] productNames, String artist) throws InterruptedException, ExecutionException, TimeoutException{
		ExecutorCompletionService<Albums> completionService = new ExecutorCompletionService<Albums>(getExecutor());
		Map<String, Albums> results = new HashMap<String, Albums>();
		
		for(String name:productNames){
			String albumProductsUrl = Utilities.generateURLForLastFM(lastFMUrl, artist, name);
			Callable<Albums> task = new Guider(albumProductsUrl, Albums.class, getProxy(), getProvider());
			Albums Albums = completionService.submit(task).get(1000*10, TimeUnit.SECONDS);
			
			results.put(name, Albums);
		}
		
		return results;
	}
}
