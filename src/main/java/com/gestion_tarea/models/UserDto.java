package com.gestion_tarea.models;

import java.util.List;

public class UserDto {
    
    public UserDto() {
    	
    }
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public UserDto(Long id, String username, String email) {
		
		this.id = id;
		this.username = username;
		this.email = email;
		
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private String username;
    private String password;
    private String email;
    private List<String> roles;

    // Getters y setters
}
