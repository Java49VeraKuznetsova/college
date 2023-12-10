package telran.college.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.college.entities.*;
import telran.college.dto.*;
import java.util.*;

public interface MarkRepo extends JpaRepository<Mark, Long> {
List<SubjectNameScore> findByStudentName(String studentName);
/*********************************************************/
@Query("SELECT student.name as name from Mark where subject.type=:type "
		+ "group by student.name order by avg(score) desc limit :nStudents")
List<String> findBestStudentsSubjectType(SubjectType type, int nStudents);
/*********************************/
@Query("SELECT st.name as name, st.city as city "
		+ "from Mark m "
		+ "right join m.student st "
		+ "group by st.name, city having count(m.score) < :scoresThreshold")
List<StudentCity> findStudentsScoresLess(int scoresThreshold);
}