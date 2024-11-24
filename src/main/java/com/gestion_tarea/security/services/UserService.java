package com.gestion_tarea.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gestion_tarea.models.Project;
import com.gestion_tarea.models.Task;
import com.gestion_tarea.models.User;
import com.gestion_tarea.payload.response.UserDto;
import com.gestion_tarea.repository.ProjectRepository;
import com.gestion_tarea.repository.TaskRepository;
import com.gestion_tarea.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService  {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository  projectRepository ;

   public List<?> getAllUser(){
	   Set<String> roles = null;
	   
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	   if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			 roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
						.collect(Collectors.toSet());
	   }
	  
		if (roles.contains("ROLE_SUPER")) {
			 List<UserDto> userDtos = userstoDTO(userRepository.findAll());
			return  userDtos ;
			
		}
	   List<User> users = userRepository.findByTenantId(getAuthenticatedTenantId());
	   
	   List<UserDto> userDtos = userstoDTO(users);

		return userDtos;

	   
	   
   }
   
   public List<UserDto> userstoDTO(List<User> users) {
	   List<UserDto> userDtos = users.stream()
		        .map(user -> {
		            UserDto dto = new UserDto();
		            dto.setEmail(user.getEmail());
		            dto.setUsername(user.getUsername());
		            dto.setId(user.getId());
		             // Inicializar la lista de roles en el DTO
		            List<String> roleNames = new ArrayList<>();

		            // Iterar sobre los roles del usuario y añadir cada nombre a la lista
		            user.getRoles().forEach(role -> roleNames.add(role.getName()+""));

		            // Asignar la lista de nombres de roles al DTO
		            dto.setRoles(roleNames);
		            return dto;
		        })
		        .collect(Collectors.toList());

		return userDtos;
	   
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

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
    	Optional<User> userOptional = userRepository.findById(id);
    	
    	if (userOptional.isPresent()) {
    		User user = userOptional.get();
            // Limpia las relaciones de proyectos donde es responsable
            List<Project> projects = projectRepository.findByResponsible(user);
            projects.forEach(project -> project.setResponsible(null));
            projectRepository.saveAll(projects); 
    		
    		 
    		  // Limpia las relaciones entre el usuario y los proyectos
            user.getProjects().forEach(project -> project.getMembers().remove(user));
            user.getProjects().clear();
            userRepository.save(user);

    		
    		 List<Task> task =	taskRepository.findByAssignedTo(user);
    		    taskRepository.deleteAll(task);
    		    
    		        userRepository.deleteById(id);
			
		}
    	
   
    }

    public Optional<User> getUserByName(String name) {
        return  userRepository.findByUsername(name);
    }
//    public List<User> getUserByEmail(String email) {
//        return (List<User>) userRepository.findByEmail(email);
//    }
//    
//    
//    @Transactional
//    public User addUser(User user, Set<String> roleNames) {
//        // Codificar la contraseña antes de guardarla en la base de datos
//        user.setPassword(user.getPassword());
//
//        Set<Role> roles = new HashSet<>();
//        for (String roleName : roleNames) {
//            Role role = roleRepository.findByName(roleName);
//            if (role != null) {
//                roles.add(role);
//            } else {
//                throw new IllegalArgumentException("Role not found: " + roleName);
//            }
//        }
//        
//        // Asignar roles al usuario
//        user.setRoles(roles);
//
//        // Guardar el usuario en la base de datos
//        return userRepository.save(user);
//    }
//
//   
 

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    
}
