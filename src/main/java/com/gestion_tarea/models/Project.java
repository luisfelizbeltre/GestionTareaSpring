package com.gestion_tarea.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
@Entity
public class Project {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String name;

	    private String description;
	    // Relación con la entidad User para el responsable del proyecto
	    @ManyToOne()
	    @JoinColumn(name = "responsible_id")
	    @JsonIgnore
	    
	    private User responsible;

	    @ManyToOne
	    @JoinColumn(name = "tenant_id", nullable = false)
	    @JsonIgnore
	    private Tenant tenant;

	    @ManyToMany
	    @JoinTable(
	        name = "project_members",
	        joinColumns = @JoinColumn(name = "project_id"),
	        inverseJoinColumns = @JoinColumn(name = "user_id")
	    )
	    private Set<User> members = new HashSet<>();

	    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	    @JsonManagedReference
	    private Set<Task> tasks = new HashSet<>();

		@Column(name = "start_date")
	    private LocalDateTime startDate;

	    @Column(name = "end_date")
	    private LocalDateTime endDate;

	    
    // Getters y setters


    
    // --------------------Getters y setters-------------------
    
    
    
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<User> getMembers() {
		return members;
	}

	public void setMembers(Set<User> members) {
		this.members = members;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	
    
    public User getResponsible() {
		return responsible;
	}

	public void setResponsible(User responsible) {
		this.responsible = responsible;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}



}
