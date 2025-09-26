package com.san.system.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.san.system.model.AttendanceRecord;
import com.san.system.model.Faculty;
import com.san.system.model.Student;


public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    /**
     * Finds all attendance records for a specific student.
     * Spring Data JPA automatically creates the query from the method name.
     * @param studentId The ID of the student.
     * @return A list of attendance records.
     */
    List<AttendanceRecord> findByStudentId(Long studentId);

    /**
     * Finds all attendance records for a specific faculty member.
     * @param facultyId The ID of the faculty member.
     * @return A list of attendance records.
     */
    List<AttendanceRecord> findByFacultyId(Long facultyId);
    boolean existsByStudentAndFacultyAndAttendanceDateBetween(Student student, Faculty faculty, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(a) > 0 FROM AttendanceRecord a WHERE a.student = ?1 AND a.faculty = ?2 AND CAST(a.attendanceDate AS date) = CURRENT_DATE")
    boolean hasAttendedToday(Student student, Faculty faculty);
    List<AttendanceRecord> findByFacultyIdAndAttendanceDateBetween(Long facultyId, LocalDateTime start, LocalDateTime end);

}
