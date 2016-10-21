package io.smartcat.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;


@Document(collection = "users")
public class User {
	
	private String username;
	private String firstname;
	private String lastname;
	private Date birthDate;
	
	private long numberOfCards;
	
	private final List<String> favoriteMovies = Lists.newArrayList();
	
	private Address address;
	
	// getters and setters
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public long getNumberOfCards() {
		return numberOfCards;
	}
	public void setNumberOfCards(long numberOfCards) {
		this.numberOfCards = numberOfCards;
	}
	public List<String> getFavoriteMovies() {
		return favoriteMovies;
	}
	public void setFavoriteMovies(List<String> favoriteMovies) {
		this.favoriteMovies.clear();
		if (favoriteMovies != null) {
			this.favoriteMovies.addAll(favoriteMovies);
		}
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	

}
