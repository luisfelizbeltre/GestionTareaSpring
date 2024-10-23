package com.gestion_tarea.controllers;


import java.util.List;

import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.gestion_tarea.models.Project;
import com.gestion_tarea.models.ProjectDTO;
import com.gestion_tarea.models.Tenant;
import com.gestion_tarea.models.User;
import com.gestion_tarea.repository.TenantRepository;
import com.gestion_tarea.repository.UserRepository;
import com.gestion_tarea.security.services.ProjectService;
import com.gestion_tarea.security.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private TenantRepository tenantRepository ;
    
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> createProject(@RequestBody ProjectDTO projectDTO) {
        // Obtener la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = null;
        Long tenantId = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            username = userDetails.getUsername();
            tenantId = userDetails.getTenantId(); // Obtener el tenantId
        }

        // Validar que se encontró un tenantId
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tenant ID found for the authenticated user: " + username);
        }

        // Buscar el tenant por ID, lanzar excepción si no se encuentra
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found for user: " ));

        // Buscar al usuario responsable si se proporciona en el DTO
        User responsibleUser = null;
        if (projectDTO.getResponsibleUsername() != null) {
            responsibleUser = userRepository.findByUsernameAndTenantId(projectDTO.getResponsibleUsername(), tenantId)
                .orElse(null); // Retorna null si no se encuentra el usuario
            if (responsibleUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Responsible user not found in tenant.");
            }
        }

        // Crear un nuevo proyecto usando los datos del DTO
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setTenant(tenant);
        project.setResponsible(responsibleUser); // Asignar el responsable (si se ha proporcionado)
        project.setEndDate(projectDTO.getEndDate());
        // Guardar el proyecto
        try {
            Project createdProject = projectService.createProject(project, tenant);
            return ResponseEntity.ok(createdProject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el proyecto: " + e.getMessage());
        }
    }

    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> getAllProjects() {
        // Obtener el nombre de usuario y el tenantId desde el token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            tenantId = userDetails.getTenantId(); // Obtener el tenantId del usuario autenticado
        }

        if (tenantId == null) {
            throw new RuntimeException("Tenant ID not found for the authenticated user.");
        }

        List<Project> projects = projectService.getAllProjects(tenantId);
        
        // Mapear los proyectos a ProjectDTO
           List<ProjectDTO> projectDTOs = projects.stream().map(project -> {
               ProjectDTO dto = new ProjectDTO();
               dto.setId(project.getId());
               dto.setName(project.getName());
               dto.setDescription(project.getDescription());
               dto.setStartDate(project.getStartDate());
               dto.setEndDate(project.getEndDate());
               dto.setTasks(project.getTasks());
           
             
               // Mapear el responsable
               if (project.getResponsible() != null) {
                   dto.setResponsibleUsername(project.getResponsible().getUsername());  // Evitar serializar el objeto completo
               } else {
                   dto.setResponsibleUsername(null);
               }

               return dto;
           }).collect(Collectors.toList());

           return ResponseEntity.ok(projectDTOs);
    }
    

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable("id") Long id) {
        // Obtener el nombre de usuario y el tenantId desde el token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = null;
        System.out.println("aaaaaaaaaaa");
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            tenantId = userDetails.getTenantId(); // Obtener el tenantId del usuario autenticado
        }

        if (tenantId == null) {
            throw new RuntimeException("Tenant ID not found for the authenticated user.");
        }

        Project projects = projectService.getProjectById(id,tenantId);
       
        ProjectDTO dto = new ProjectDTO();
        dto.setDescription(projects.getDescription());
        dto.setId(projects.getId());
        dto.setMembers(projects.getMembers());
        dto.setTasks(projects.getTasks());
        dto.setName(projects.getName());
        dto.setStartDate(projects.getStartDate());
        dto.setEndDate(projects.getEndDate());
     // Asignar responsable si existe
        if (projects.getResponsible() != null) {
            dto.setResponsibleUsername(projects.getResponsible().getUsername());
        } else {
            dto.setResponsibleUsername(null);
        }
        
        return ResponseEntity.ok(dto);
    }


    

    @PostMapping("/{projectId}/add-member")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> addMemberToProject(@PathVariable("projectId") Long projectId, @RequestParam("username") String username) {
        try {
            Project updatedProject = projectService.addMemberToProject(projectId, username);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
