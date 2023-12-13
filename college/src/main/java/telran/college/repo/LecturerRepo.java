package telran.college.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.college.dto.*;
import telran.college.entities.*;

public interface LecturerRepo extends JpaRepository<Lecturer, Long> {
	
	

/******************************************************************/

List<NamePhone> findByCity(String city);

/*
@Query(value="select name, phone from students_lecturers where city=:city and dtype='Lecturer'", nativeQuery = true)
List<NamePhone> findLecturersCity(String city);
*/
}
