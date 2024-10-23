package com.gestion_tarea.models;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Tenant {
    
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, unique = true)
	    private String name;

	    @OneToMany(mappedBy = "tenant")
	    @JsonIgnore // Ignorar la serialización de la lista de proyectos
	    private Set<Project> projects = new HashSet<>();
	    

	    @OneToMany(mappedBy = "tenant")
	    @JsonIgnore // Ignorar la serialización de la lista de proyectos
	    private Set<Task> task = new HashSet<>();
	    

	    @OneToMany(mappedBy = "tenant")
	    @JsonManagedReference
	    private Set<User> users = new HashSet<>();
    
	 // Constructor por defecto (requerido por JPA)
    public Tenant() {}

    // Constructor con parámetros
    public Tenant(String name) {
        this.name = name;
    }
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
    // Getters y setters
	
	
}
