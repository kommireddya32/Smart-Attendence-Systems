package com.san.system.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.san.system.model.Admin;
import com.san.system.repository.AdminRepository;
@Service
public class AdminService {
	@Autowired
	AdminRepository repo;
	public boolean isAdmin(String emailId, String password) {
		Admin admin = repo.findById(emailId).orElse(null);
		if(admin!=null) {
			if(admin.getPassword().equals(password)) return true;
		}
		
		return false;
	}
}

