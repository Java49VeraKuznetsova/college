package telran.college.repo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.college.dto.LecturerHour;
import telran.college.dto.LecturerPhone;
import telran.college.entities.*;

public interface LecturerRepo extends JpaRepository<Lecturer, Long> {
	/*
	String JOIN_LECTURERS_HOURS = "FROM students_lecturers st  join subjects sb on lecturer_id = st.id";

	@Query(value = "SELECT st.name as name, sum (hours) as hours" +
	JOIN_LECTURERS_HOURS + 
	"group by st.name order by sum(hours) desc limit "
	+ ":nLecturer", nativeQuery=true)
	
	
	List<LecturerHour> lecturerHours(int nLecturers);
	*/
	
	@Query(value = "SELECT name, phone  FROM students_lecturers"
			+ "	where dtype=:type AND city=:city",nativeQuery=true )
	List<LecturerPhone> lecturerPhones(String type, String city);

}







