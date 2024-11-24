package com.gestion_tarea.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_tarea.models.Tenant;
import com.gestion_tarea.security.services.TenantService;

@RestController
@RequestMapping("/api/companies")

public class TenantController {
	

	    @Autowired
	    private TenantService tenantService;

	    // Obtener todas las empresas
	    @GetMapping
	    @PreAuthorize("hasRole('SUPER')" )

	    public List<Tenant> getAllCompanies() {
	        return tenantService.getAllCompanies();
	    }

	    // Eliminar una empresa por ID
	    @DeleteMapping("/{id}")
	    @PreAuthorize("hasRole('SUPER')" )

	    public ResponseEntity<Void> deleteCompany(@PathVariable("id") Long id) {
	    	tenantService.deleteCompany(id);
	        return ResponseEntity.noContent().build();
	    }
	

}
