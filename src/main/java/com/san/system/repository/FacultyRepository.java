package com.san.system.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.san.system.model.Faculty;



public interface FacultyRepository extends CrudRepository<Faculty,Long>{
	List<Faculty> findByNameContainingIgnoreCase(String keyword);
    Faculty findByEmail(String email);
}
