package com.gestion_tarea.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.gestion_tarea.models.Project;
import com.gestion_tarea.models.Task;
import com.gestion_tarea.models.TaskDTO;
import com.gestion_tarea.security.services.TaskService;
import com.gestion_tarea.security.services.UserDetailsImpl;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/all")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Task> listTasks() {
        // Obtener el nombre de usuario y el tenantId desde el token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        Long tenantId = null;
        @SuppressWarnings("unused")
		Long idUsername=null;
        
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            idUsername=userDetails.getId();
            tenantId = userDetails.getTenantId(); // Obtener el tenantId del usuario autenticado
        }
        System.out.println(tenantId+"das------------a---------a");

        // Filtrar tareas por username y tenantId
        return taskService.getTasksByUsernameAndTenantId(username, tenantId);
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
