package com.gestion_tarea.payload.response;

import java.time.LocalDateTime;
import java.util.Set;

import com.gestion_tarea.models.Task;
import com.gestion_tarea.models.User;

public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Task> tasks; 
    private Set<User> members;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String responsibleUsername;
    
	public Long getId() {
		return id;
	}
	public LocalDateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDateTime localDateTime) {
		this.startDate = localDateTime;
	}
	public LocalDateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDateTime localDateTime) {
		this.endDate = localDateTime;
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
	public Set<Task> getTasks() {
		return tasks;
	}
	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}
	public Set<User> getMembers() {
		return members;
	}
	public void setMembers(Set<User> members) {
		this.members = members;
	}
	public String getResponsibleUsername() {
		return responsibleUsername;
	}
	public void setResponsibleUsername(String responsibleUsername) {
		this.responsibleUsername = responsibleUsername;
	}
	

    // Getters y Setters
}
