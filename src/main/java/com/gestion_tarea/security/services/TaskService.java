package com.gestion_tarea.security.services;



	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Service;
import com.gestion_tarea.models.Project;
import com.gestion_tarea.models.Task;
import com.gestion_tarea.models.TaskDTO;
import com.gestion_tarea.models.Tenant;
import com.gestion_tarea.models.User;
import com.gestion_tarea.repository.ProjectRepository;
import com.gestion_tarea.repository.TaskRepository;
import com.gestion_tarea.repository.TenantRepository;
import com.gestion_tarea.repository.UserRepository;
import java.util.List;
import java.util.Optional;

	@Service
	public class TaskService {

	    @Autowired
	    private TaskRepository taskRepository;

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private ProjectRepository projectRepository;
	    
	    @Autowired TenantRepository tenantRepository;
	    public List<Task> getAllTasks() {
	        return taskRepository.findAll();
	    }



	    // Obtener todas las tareas de un usuario filtrado por tenant y proyecto
	    public List<Task> getTasksByUsernameAndTenantId(String username, Long tenantId) {
	        return taskRepository.findByAssignedToUsernameAndTenantId(username, tenantId);
	    }
	    
	    public boolean deleteTaskByIdAndTenantId(Long id, Long tenantId) {
	    	Optional<Task> task = taskRepository.findByIdAndTenantId(id,tenantId);
	    	
	    	if(task.isPresent()) {
	    		taskRepository.delete(task.get());
	    		return true;
	    	}
	    	return false;
	    }

	    public Task saveTask(TaskDTO taskDto, Long projectId, Long tenantId) {
	        // Verificar que el tenant exista
	        Tenant tenant = tenantRepository.findById(tenantId)
	                .orElseThrow(() -> new RuntimeException("Tenant not found"));

	        System.out.println(tenantId+"--------------"+projectId);
	        // Buscar el proyecto por su ID y tenant
	        Project project = projectRepository.findByIdAndTenantId(projectId, tenantId)
	                .orElseThrow(() -> new RuntimeException("Project not found for this tenant"));

	        // Verificar que el nombre de usuario asignado no sea nulo
	        if (taskDto.getAssignedTo() == null || taskDto.getAssignedTo().isEmpty()) {
	            throw new IllegalArgumentException("Assigned user information is missing.");
	        }

	        // Obtener el nombre del usuario al que se le asigna la tarea
	        String assignedUsername = taskDto.getAssignedTo();

	        // Buscar al usuario por nombre y verificar que pertenece al mismo tenant
	        User assignedUser = userRepository.findByUsernameAndTenantId(assignedUsername, tenantId)
	                .orElseThrow(() -> new RuntimeException("Assigned user not found in this tenant"));

	        // Crear una nueva entidad Task y asignar valores
	        Task task = new Task();
	        task.setTitle(taskDto.getTitle());
	        task.setDescription(taskDto.getDescription());
	        task.setPriority(taskDto.getPriority());
	        task.setStatus(taskDto.getStatus());
	        task.setDueDate(taskDto.getDueDate());
	        task.setProject(project);
	        task.setAssignedTo(assignedUser);
	        task.setTenant(tenant);

	        // Guardar la tarea en la base de datos
	        return taskRepository.save(task);
	    }




	



	}



