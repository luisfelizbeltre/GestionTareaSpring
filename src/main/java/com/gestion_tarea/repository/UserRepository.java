package com.gestion_tarea.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gestion_tarea.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);
Optional<User> findByIdAndTenantId(Long id,Long tenantID);
  Boolean existsByEmail(String email);

Optional<User> findByUsernameAndTenantId(String assignedUsername, Long tenantId);
List<User> findByTenantId(Long tenantId);

}
