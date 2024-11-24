package com.gestion_tarea.security.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion_tarea.repository.TenantRepository;
import com.gestion_tarea.models.Tenant;


@Service
public class TenantService {
	
	

		 @Autowired
	    private TenantRepository tenantRepository;

	    // Obtener todas las empresas
	    public List<Tenant> getAllCompanies() {
	        return tenantRepository.findAll();
	    }

	    // Eliminar una empresa
	    public void deleteCompany(Long id) {
	    	tenantRepository.deleteById(id);
	    }
	


}
