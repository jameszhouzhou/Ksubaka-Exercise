package com.ksubaka.entertainment.products.dao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Movie {

	@XmlAttribute(name="imdbID")
	private String imdbID;
	
	@XmlAttribute(name="poster")
	private String poster;
	
	@XmlAttribute(name="title")
	private String title;
	
	@XmlAttribute(name="type")
	private String type;
	
	@XmlAttribute(name="year")
	private String year;

	public String getImdbID() {
		return imdbID;
	}

	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
