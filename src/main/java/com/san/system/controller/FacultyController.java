package com.san.system.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.zxing.WriterException;
import com.san.system.dto.StudentAttendanceStatusDto;
import com.san.system.model.AttendanceRecord;
import com.san.system.model.Faculty;
import com.san.system.service.AttendanceService;
import com.san.system.service.FacultyService;
import com.san.system.util.QRCodeGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/faculty")  // ✅ Added for consistent routing
public class FacultyController {

    private final FacultyService facultyService;
    private final AttendanceService attendanceService;

    @Autowired
    public FacultyController(FacultyService facultyService,
                             AttendanceService attendanceService) {
        this.facultyService = facultyService;
        this.attendanceService = attendanceService;
    }

    /* ---------- LOGIN / DASHBOARD ---------- */

    @RequestMapping("/login-page")
    public String facultyLogin() {
        return "redirect:/faculty_login.html";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Faculty authenticated = facultyService.authenticate(email, password);

        if (authenticated != null) {
            session.setAttribute("facultyId", authenticated.getId());  // ✅ Ensures session is set
            return "redirect:/faculty/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/faculty/login-page";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        Long facultyId = (Long) session.getAttribute("facultyId");
        if (facultyId == null) {
            return "redirect:/faculty/login-page";
        }

        List<AttendanceRecord> previousSessions =
                attendanceService.getHistoryForFaculty(facultyId);
        model.addAttribute("previousSessions", previousSessions);

        return "faculty-dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/faculty/login-page";
    }

    /* ---------- PROFILE ---------- */

    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpSession session) {
        Long facultyId = (Long) session.getAttribute("facultyId");
        if (facultyId == null) {
            return "redirect:/faculty/login-page";
        }

        Faculty faculty = facultyService.getFacultyById(facultyId);
        model.addAttribute("faculty", faculty);

        return "faculty-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String department,
                                @RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Long facultyId = (Long) session.getAttribute("facultyId");
        if (facultyId == null) {
            return "redirect:/faculty/login-page";
        }

        try {
            boolean success = facultyService
                    .updateProfile(facultyId, name, department, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", success);
            redirectAttributes.addFlashAttribute("message",
                    success ? "Profile updated successfully!" : "Update failed");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("success", false);
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/faculty/profile";
    }

    /* ---------- QR CODE API ---------- */

    @GetMapping(value = "/api/qrcode/generate", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> generateQrCode(@RequestParam String department, HttpSession session) {
        Long facultyId = (Long) session.getAttribute("facultyId");
        
        if (facultyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faculty session not found");
        }

        try {
            String qrData = facultyService.generateAndSaveQrData(facultyId, department);
            System.out.println("Generated QR: " + qrData); // ✅ Debug log

            byte[] qrImage = QRCodeGenerator.generateQRCodeImage(qrData, 250, 250);

            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl(CacheControl.noStore());
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return new ResponseEntity<>(qrImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "QR generation failed", e);
        }
    }
    /* ---------- ATTENDANCE HISTORY ---------- */

    @GetMapping("/history-page")
    public String showHistoryPage(HttpSession session) {
        if (session.getAttribute("facultyId") == null) {
            return "redirect:/faculty/login-page";
        }
        return "faculty-history.html";
    }

    @GetMapping("/history")
    @ResponseBody
    public List<StudentAttendanceStatusDto> getAttendanceHistory(
            @RequestParam String department,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            HttpSession session) {

        Long facultyId = (Long) session.getAttribute("facultyId");
        if (facultyId == null) {
            return List.of();
        }
        return attendanceService.getAttendanceStatusForClass(facultyId, department, date);
    }
}