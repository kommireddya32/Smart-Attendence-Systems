package com.san.system.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.san.system.model.Student;

public interface StudentRepository extends CrudRepository<Student,Long>{
	List<Student> findByNameContainingIgnoreCase(String keyword);
	 
    Student findByEmail(String email);
    List<Student> findByDepartment(String department);
}
