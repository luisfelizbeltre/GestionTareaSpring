package com.gestion_tarea.payload.response;

import java.util.List;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String email;
  private List<String> roles;
  private String tenantName;
  private Long tenantId;
  public JwtResponse(String token, Long id, String username, String email, List<String> roles, String tenantName, Long tenantId) {
      this.token = token;
      this.id = id;
      this.username = username;
      this.email = email;
      this.roles = roles;
      this.tenantName = tenantName;
      this.tenantId=tenantId;
  }

  
  
  public String getToken() {
	return token;
}



public void setToken(String token) {
	this.token = token;
}



public String getType() {
	return type;
}



public void setType(String type) {
	this.type = type;
}



public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  
  
  public String getTenantName() {
	return tenantName;
}

public void setTenantName(String tenantName) {
	this.tenantName = tenantName;
}

public Long getTenantId() {
	return tenantId;
}

public void setTenantId(Long tenantId) {
	this.tenantId = tenantId;
}

public void setRoles(List<String> roles) {
	this.roles = roles;
}

public List<String> getRoles() {
    return roles;
  }
}
