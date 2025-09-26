package com.san.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.san.system.model.Faculty;
import com.san.system.model.Student;
import com.san.system.service.AdminService;
import com.san.system.service.FacultyService;
import com.san.system.service.StudentService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	@Autowired
	private FacultyService facultyService;
	@Autowired
    private StudentService studentService;
	@Autowired
    private AdminService adminService;

	@RequestMapping("/admin")
	String admin() {
		return "admin_login";
	}
	@PostMapping("/admin/login")
	public String adminLogin(@RequestParam String email,
	                         @RequestParam String password,
	                         HttpSession session,
	                         RedirectAttributes redirectAttributes) {

	    if (adminService.isAdmin(email, password)) {
	        session.setAttribute("adminUser", email);
	        return "redirect:/admin/dashboard"; // ✅ Redirect to a GET-safe endpoint
	    } else {
	        redirectAttributes.addFlashAttribute("error",
	                "Incorrect Email or Password, please try again");
	        return "redirect:/admin"; // Assuming this shows the login form
	    }
	}
	@GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        if (session.getAttribute("adminUser") == null) {
            return "redirect:/admin";
        }
        
            model.addAttribute("totalStudents", studentService.getStudentCount());
            model.addAttribute("totalFaculty", facultyService.getFacultyCount());
        
        return "admin_dashboard"; // -> /WEB-INF/jsp/admin_dashboard.jsp
    }
	
	 @GetMapping("/admin/students")
	    public String manageStudents(@RequestParam(required = false) String keyword,
	                                 Model model, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";

	        if (keyword != null && !keyword.isEmpty()) {
	            model.addAttribute("students", studentService.searchStudents(keyword));
	        } else {
	            model.addAttribute("students", studentService.getAllStudents());
	        }
	        return "manage-students";
	    }
	 @GetMapping("/admin/students/add")
	    public String addStudentForm(HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        return "add-student";
	    }

	    @PostMapping("/admin/students/add")
	    public String saveStudent(Student student, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        studentService.createStudent(student);
	        return "redirect:/admin/students";
	    }

	    @PostMapping("/admin/students/delete")
	    public String deleteStudent(@RequestParam Long id, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        studentService.deleteStudent(id);
	        return "redirect:/admin/students";
	    }

	    @GetMapping("/admin/students/edit")
	    public String editStudentForm(@RequestParam Long id, Model model, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        model.addAttribute("student", studentService.getStudentById(id));
	        return "edit-student";
	    }

	    @PostMapping("/admin/students/update")
	    public String updateStudent(Student student, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        studentService.updateStudent(student);
	        return "redirect:/admin/students";
	    }
	    /* ---------------------  FACULTY MANAGEMENT (JSP) --------------------- */

	    @GetMapping("/admin/faculty")
	    public String manageFaculty(@RequestParam(required = false) String keyword,
	                                Model model, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";

	        if (keyword != null && !keyword.isEmpty()) {
	            model.addAttribute("facultyList", facultyService.searchFaculty(keyword));
	        } else {
	            model.addAttribute("facultyList", facultyService.getAllFaculty());
	        }
	        return "manage-faculty";
	    }

	    @GetMapping("/admin/faculty/add")
	    public String addFacultyForm(HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        return "add-faculty";
	    }

	    @PostMapping("/admin/faculty/add")
	    public String saveFaculty(Faculty faculty, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        facultyService.createFaculty(faculty);
	        return "redirect:/admin/faculty";
	    }

	    @PostMapping("/admin/faculty/delete")
	    public String deleteFaculty(@RequestParam Long id, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        facultyService.deleteFaculty(id);
	        return "redirect:/admin/faculty";
	    }

	    @GetMapping("/admin/faculty/edit")
	    public String editFacultyForm(@RequestParam Long id, Model model, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        model.addAttribute("faculty", facultyService.getFacultyById(id));
	        return "edit-faculty";
	    }

	    @PostMapping("/admin/faculty/update")
	    public String updateFaculty(Faculty faculty, HttpSession session) {
	        if (session.getAttribute("adminUser") == null) return "redirect:/admin";
	        facultyService.updateFaculty(faculty);
	        return "redirect:/admin/faculty";
	    }

	    /* ---------------------  LOGOUT --------------------- */

	    @GetMapping("/logout")
	    public String logout(HttpSession session) {
	        session.invalidate();
	        return "redirect:/admin";
	    }



}
