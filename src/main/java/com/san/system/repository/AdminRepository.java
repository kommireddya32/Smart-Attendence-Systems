package com.san.system.repository;

import org.springframework.data.repository.CrudRepository;

import com.san.system.model.Admin;
public interface AdminRepository extends CrudRepository<Admin,String>{
	
}
