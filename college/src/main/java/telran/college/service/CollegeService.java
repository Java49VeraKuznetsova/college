package telran.college.service;

import java.util.List;

import telran.college.dto.LecturerHour;
import telran.college.dto.LecturerPhone;
import telran.college.dto.StudentCity;
import telran.college.dto.StudentMark;
import telran.college.dto.StudentSubjectMark;

public interface CollegeService {
	List<String> bestStudentsSubjectType(String type, int nStudents);

	List<StudentMark> studentsAvgMarks();
	
	//List<LecturerHour> lecturerSumHours(int nLecturers);
	
	List<LecturerPhone> lecturersCityPhone (String type, String city);
	
	List <StudentCity> studentCityScores(String type, String city, int nScore);
	
	List<StudentSubjectMark> studentSubjectMark(String name);
	
}