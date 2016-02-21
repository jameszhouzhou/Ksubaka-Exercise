package com.ksubaka.entertainment.products.utils;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class Utilities {

	public static final String IMDB_SEARCH_TITLE = "&s=";
	public static final String LASTFM_PARAM_ARTIST = "&artist=";
	public static final String LASTFM_PARAM_ALBUM = "&album=";
	public static final String LASTFM_API_KEY_AND_VALUE = "&api_key=e1cc2851d03e61990fb125228950dfd3";
	
	public static final String COMMA = ",";
	public static final String EXIT = "exit";
	
	public static final String API_IMDB = "imdb";
	public static final String API_LASTFM = "lastfm";
	
	public static final String IMDB_RESPONSE_FALSE = "False";
	public static final String IMDB_RESPONSE_OK = "True";
	public static final String LASTFM_RESPONSE_FALSE = "failed";
	public static final String LASTFM_RESPONSE_OK = "ok";
	
	public static String generateURLforIMDB(String base, String movieName){
		return base + IMDB_SEARCH_TITLE + movieName;
	}
	
	public static String generateURLForLastFM(String base, String artist, String album){
		return base + LASTFM_PARAM_ARTIST + artist + LASTFM_PARAM_ALBUM + album + LASTFM_API_KEY_AND_VALUE;
	}
	
	public static <T> T unmarshalResponseToProducts(InputStream responseXml, Class<T> clazz) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(responseXml);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			return (T)jaxbUnmarshaller.unmarshal(doc);
	}
	
	public static String formateReuqestString(String str){
		str = ","+str+",";
		String regex = "\\s*,\\s*";
		str = str.replaceAll(regex, ",");
		str = str.replaceAll("\\s+", "+");
		
		return str.substring(1, str.length()-1);
	}
}
