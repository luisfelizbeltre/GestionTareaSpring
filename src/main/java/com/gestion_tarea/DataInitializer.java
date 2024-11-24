package com.gestion_tarea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.gestion_tarea.models.ERole;
import com.gestion_tarea.models.Role;
import com.gestion_tarea.models.User;
import com.gestion_tarea.repository.RoleRepository;
import com.gestion_tarea.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired 
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_USER));
        }

        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
        }

        if (roleRepository.findByName(ERole.ROLE_MODERATOR).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_MODERATOR));
        }
        if (roleRepository.findByName(ERole.ROLE_SUPER).isEmpty()) {
            Role superRole = roleRepository.save(new Role(ERole.ROLE_SUPER));

            //roleRepository.save(new Role(ERole.ROLE_SUPER));
            //
            if (userRepository.findByUsername("superluis").isEmpty()) {
            	 User superUser = new User();
            	 superUser.setUsername("superluis");
            	 superUser.setEmail("superluis@gmail.com");
            	 superUser.setTenant(null);
            	 superUser.setPassword(passwordEncoder.encode("superpassword123456"));
            	 
            	 superUser.getRoles().add(superRole);
            	 userRepository.save(superUser);
				
			}
        }
    }
}
