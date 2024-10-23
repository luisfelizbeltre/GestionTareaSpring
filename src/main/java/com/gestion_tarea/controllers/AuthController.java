  package com.gestion_tarea.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

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
  
  
  
  
  
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
          return ResponseEntity
                  .badRequest()
                  .body(new MessageResponse("Error: Username is already taken!"));
      }

      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
          return ResponseEntity
                  .badRequest()
                  .body(new MessageResponse("Error: Email is already in use!"));
      }

      // Buscar o crear el tenant
      Tenant tenant = tenantRepository.findByName(signUpRequest.getTenantName())
              .orElseGet(() -> tenantRepository.save(new Tenant(signUpRequest.getTenantName())));

      // Crear el nuevo usuario
      User user = new User(signUpRequest.getUsername(),
              signUpRequest.getEmail(),
              encoder.encode(signUpRequest.getPassword()));
      user.setTenant(tenant); // Asocia el usuario con el tenant

      Set<String> strRoles = signUpRequest.getRole();
      Set<Role> roles = new HashSet<>();

      if (strRoles == null) {
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                  .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
      } else {
          strRoles.forEach(role -> {
              switch (role) {
                  case "admin":
                      Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                      roles.add(adminRole);
                      break;
                  case "mod":
                      Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                      roles.add(modRole);
                      break;
                  default:
                      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                      roles.add(userRole);
              }
          });
      }

      user.setRoles(roles);
      
      // Guardar el usuario antes de asociarlo a un proyecto
      userRepository.save(user);

      // Asociar el usuario a un proyecto si se proporcionó
      String projectName = signUpRequest.getProjectName();
      if (projectName != null && !projectName.isEmpty()) {
          Optional<Project> projectExist = projectRepository.findByNameAndTenantId(projectName, user.getTenant().getId());
          if (projectExist.isPresent()) {
              // Añadir el usuario al proyecto
              Project project = projectExist.get();
              project.getMembers().add(user);
              projectRepository.save(project);
          } else {
        	  ResponseEntity.status(404)
        	  .body( new MessageResponse("Project not found for the given name and tenant"));
              // Manejar el caso en que el proyecto no exista
              //throw new RuntimeException("Project not found for the given name and tenant");
          }
      }

      return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

}
