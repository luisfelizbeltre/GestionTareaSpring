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
  
  
  
  
  // 1. Registro de una nueva empresa y usuario inicial (Admin)
  @PostMapping("/registerCompany")
  public ResponseEntity<?> registerCompany(@Valid @RequestBody SignupRequest signUpRequest) {
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
      }

      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
      }

      // Crear el tenant si no existe
      Tenant tenant = tenantRepository.findByName(signUpRequest.getTenantName())
              .orElseGet(() -> tenantRepository.save(new Tenant(signUpRequest.getTenantName())));

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

      // Asociar el usuario inicial a un proyecto si se proporciona
    //  String projectName = signUpRequest.getProjectName();
      //if (projectName != null && !projectName.isEmpty()) {
        //  Project project = projectRepository.findByNameAndTenantId(projectName, tenant.getId())
          //        .orElseGet(() -> projectRepository.save(new Project(projectName, tenant)));
          //project.getMembers().add(user);
          //projectRepository.save(project);
      //}

      return ResponseEntity.ok(new MessageResponse("Company and Admin registered successfully!"));
  }

  // 2. Registro de un usuario en una empresa existente
  @PostMapping("/registerUserToCompany")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerUserToCompany(@Valid @RequestBody SignupRequest signUpRequest) {
      if (!tenantRepository.existsByName(signUpRequest.getTenantName())) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Tenant not found"));
      }

      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
      }

      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
          return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
      }

      // Asignar el usuario al tenant existente
      Tenant tenant = tenantRepository.findByName(signUpRequest.getTenantName())
              .orElseThrow(() -> new RuntimeException("Error: Tenant is not found."));
      User user = new User(signUpRequest.getUsername(),
              signUpRequest.getEmail(),
              encoder.encode(signUpRequest.getPassword()));
      user.setTenant(tenant);

      // Asignar roles en base a la solicitud
      Set<String> strRoles = signUpRequest.getRole();
      System.out.println(strRoles.toString());
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

      // Guardar el nuevo usuario en el tenant
      userRepository.save(user);

      // Asociar al proyecto si se proporciona
      String projectName = signUpRequest.getProjectName();
      if (projectName != null && !projectName.isEmpty()) {
          Project project = projectRepository.findByNameAndTenantId(projectName, tenant.getId())
                  .orElseThrow(() -> new RuntimeException("Project not found for the given name and tenant"));
          project.getMembers().add(user);
          projectRepository.save(project);
      }

      return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

}
