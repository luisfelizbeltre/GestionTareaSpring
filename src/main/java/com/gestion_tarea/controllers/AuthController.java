  package com.gestion_tarea.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_tarea.models.ERole;
import com.gestion_tarea.models.Project;
import com.gestion_tarea.models.Role;
import com.gestion_tarea.models.Tenant;
import com.gestion_tarea.models.User;
import com.gestion_tarea.payload.request.LoginRequest;
import com.gestion_tarea.payload.request.SignupRequest;
import com.gestion_tarea.payload.response.JwtResponse;
import com.gestion_tarea.payload.response.MessageResponse;
import com.gestion_tarea.repository.ProjectRepository;
import com.gestion_tarea.repository.RoleRepository;
import com.gestion_tarea.repository.TenantRepository;
import com.gestion_tarea.repository.UserRepository;
import com.gestion_tarea.security.jwt.JwtUtils;
import com.gestion_tarea.security.services.ProjectService;
import com.gestion_tarea.security.services.UserDetailsImpl;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  TenantRepository tenantRepository; // Nuevo: Repositorio para manejar tenants
  @Autowired
  ProjectRepository projectRepository;
	@Autowired
	private ProjectService projectService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    //agregar el tenantIdal jwt
    

    return ResponseEntity.ok(new JwtResponse(
    		jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles,
                         userDetails.getTenantName(), 
                         userDetails.getTenantId()  // Pasar tenantId
                         )); // Incluye tenant en la respuesta
  }
  
  
  
  @PostMapping("/registerCompany")
  public ResponseEntity<?> registerCompany(@Valid @RequestBody SignupRequest signUpRequest) {
      // Verificar si el nombre de usuario ya está en uso
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
      }

      // Verificar si el correo electrónico ya está en uso
      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
      }

      // Verificar si el tenant ya existe
      Optional<Tenant> existingTenant = tenantRepository.findByName(signUpRequest.getTenantName());
      if (existingTenant.isPresent()) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Tenant already exists!"));
      }

      // Crear un nuevo tenant si no existe
      Tenant tenant = new Tenant(signUpRequest.getTenantName());
      tenantRepository.save(tenant);

      // Crear el usuario con rol de ADMIN por defecto
      User user = new User(signUpRequest.getUsername(),
              signUpRequest.getEmail(),
              encoder.encode(signUpRequest.getPassword()));
      user.setTenant(tenant);

      // Asignar el rol de administrador al usuario inicial
      Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      user.setRoles(Set.of(adminRole));

      // Guardar el usuario inicial
      userRepository.save(user);


      return ResponseEntity.ok(new MessageResponse("Company and Admin registered successfully!"));
  }

  // 2. Registro de un usuario en una empresa existente
  @PostMapping("/registerUserToCompany")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerUserToCompany(@Valid @RequestBody SignupRequest signUpRequest) {
      // Validación de tenant
      Tenant tenant = tenantRepository.findByName(signUpRequest.getTenantName())
              .orElseThrow(() -> new RuntimeException("Error: Tenant is not found."));
      
      // Validación de usuario
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
      }

      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
      }

      // Crear nuevo usuario
      User user = new User(signUpRequest.getUsername(),
                           signUpRequest.getEmail(),
                           encoder.encode(signUpRequest.getPassword()));
      user.setTenant(tenant);

      // Asignación de roles
      Set<String> strRoles = signUpRequest.getRole();
      Set<Role> roles = new HashSet<>();
      if (strRoles == null || strRoles.isEmpty()) {
          roles.add(roleRepository.findByName(ERole.ROLE_USER)
                  .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
      } else {
          strRoles.forEach(role -> {
              switch (role) {
                  case "admin":
                      roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                              .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                      break;
                  case "mod":
                      roles.add(roleRepository.findByName(ERole.ROLE_MODERATOR)
                              .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                      break;
                  default:
                      roles.add(roleRepository.findByName(ERole.ROLE_USER)
                              .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
                      break;
              }
          });
      }
      user.setRoles(roles);

      // Guardar el nuevo usuario
      userRepository.save(user);

      // Asociar el usuario al proyecto, si es necesario
      String projectName = signUpRequest.getProjectName();
      if (projectName != null && !projectName.isEmpty()) {
          Optional<Project> project = projectRepository.findByNameAndTenantId(projectName, tenant.getId());
          if (project.isPresent()) {
              try {
                  Project updatedProject = projectService.addMemberToProject(project.get().getId(), user.getUsername());
                  return ResponseEntity.ok(updatedProject);
              } catch (RuntimeException e) {
                  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
              }
          } else {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Project not found for the given name and tenant"));
          }
      }

      return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }


}
