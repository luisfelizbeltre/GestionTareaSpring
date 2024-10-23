package com.gestion_tarea.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.gestion_tarea.models.User;
import com.gestion_tarea.models.UserDto;
import com.gestion_tarea.security.services.UserService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
//    @Autowired
//    private RoleRepository roleRepository;

    @GetMapping("/all")
    public List<UserDto> getAllUsers() {
    	
    	
    	
    	
    	
        return userService.getAllUser();
    }

    
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

////    @PostMapping("/add")
////    public User registerUser(@RequestBody User user) {
////        return userService.registerUser(user);
////    }
//    
////    @PostMapping("/add")
////    public ResponseEntity<?> addUser(@RequestBody UserDto userDto) {
////        try {
////            User user = new User();
////            user.setUsername(userDto.getUsername());
////            user.setPassword(userDto.getPassword());
////            user.setEmail(userDto.getEmail());
////
////            User newUser = userService.addUser(user, new HashSet<>(userDto.getRoles()));
////            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
////        } catch (IllegalArgumentException e) {
////            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
////        }
//    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userService.updateUser(id, userDetails);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted successfully with id " + id;
    }

    @GetMapping("/search")
    public Optional<User> getUserByName(@RequestParam("name") String name) {
        return userService.getUserByName(name);
    }
}
