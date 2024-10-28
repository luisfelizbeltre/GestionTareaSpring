package com.gestion_tarea.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion_tarea.models.Task;
import com.gestion_tarea.models.User;



@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByAssignedTo(User user);

	List<Task> findByAssignedTo(Optional<User> user);
	 List<Task> findByAssignedToUsernameAndTenantId(String username, Long tenantId);
//	List<Task> findByAssignedToEmail(String email);

	Optional<Task> findByIdAndTenantId(Long id, Long tenantId);
}
