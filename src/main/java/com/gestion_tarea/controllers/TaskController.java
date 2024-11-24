package com.gestion_tarea.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.gestion_tarea.models.Project;
import com.gestion_tarea.models.Task;
import com.gestion_tarea.payload.response.TaskDTO;
import com.gestion_tarea.repository.UserRepository;
import com.gestion_tarea.security.services.TaskService;
import com.gestion_tarea.security.services.UserDetailsImpl;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }
  
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody TaskDTO task) {
        try {
            Task existingTask = taskService.getTaskById(id);
            if (existingTask != null) {
                existingTask.setStatus(task.getStatus());  // Actualiza el estado de la tarea
                taskService.saveTask(existingTask);  // Guarda la tarea actualizada
                return ResponseEntity.ok(existingTask);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    
    
    @GetMapping("")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<TaskDTO> listTasks() {
        // Obtener el nombre de usuario y el tenantId desde el token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        Long tenantId = null;
        Long idUsername;
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            idUsername=userDetails.getId();
            tenantId = userDetails.getTenantId(); // Obtener el tenantId del usuario autenticado
            
            username = userRepository.findById(idUsername).get().getUsername();
            System.out.println(username);
        }
        System.out.println(tenantId+"das------------a---------a");

        // Filtrar tareas por username y tenantId
        return taskService.getTasksByUsernameAndTenantId(username, tenantId);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable("id") Long id){
    	try {
			Long tenantId = getAuthenticatedTenantId();
			
			boolean deleted = taskService.deleteTaskByIdAndTenantId(id, tenantId);
			if(deleted) {
				return ResponseEntity.ok("Tarea eliminada correctamente");
				
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada o no pertenece al tenant");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la tarea");
		}
    	
    	
    }
 
    
    
 // Endpoint para agregar una nueva tarea a un proyecto
    @PostMapping("/add")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Task> addTaskToProject(@RequestBody TaskDTO taskDto) {

        // Obtener el tenantId del usuario autenticado
        Long tenantId = getAuthenticatedTenantId();

        // Guardar la tarea, asignándola al usuario especificado en el cuerpo de la solicitud
        Task savedTask = taskService.saveTask(taskDto, taskDto.getProject(), tenantId);

        return ResponseEntity.ok(savedTask);
    }

    // Método privado para obtener el tenantId del usuario autenticado
    private Long getAuthenticatedTenantId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getTenantId();
        } else {
            throw new RuntimeException("Tenant ID not found for the authenticated user.");
        }
    }


    
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        // Implementación para crear el proyecto
        return ResponseEntity.ok("Proyecto creado exitosamente");
    }

}
