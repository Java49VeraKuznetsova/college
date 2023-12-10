package telran.college.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.college.dto.LecturerHour;
import telran.college.dto.LecturerPhone;
import telran.college.dto.StudentCity;
import telran.college.dto.StudentMark;
import telran.college.dto.StudentSubjectMark;
import telran.college.repo.*;
@Service
@RequiredArgsConstructor
public class CollegeServiceImpl implements CollegeService {
	final StudentRepo studentRepo;
	final LecturerRepo lecturerRepo;
	final SubjectRepo subjectRepo;
	final MarkRepo markRepo;
	@Override
	public List<String> bestStudentsSubjectType(String type, int nStudents) {
		
		return studentRepo.findBestStudentsSubjectType(type, nStudents);
	}
	@Override
	public List<StudentMark> studentsAvgMarks() {
		
		return studentRepo.studentsMarks();
	}
	/*
	@Override
	public List<LecturerHour> lecturerSumHours(int nLecturers) {
		
		return lecturerRepo.lecturerHours(nLecturers);
	}
	*/
	@Override
	public List<LecturerPhone> lecturersCityPhone(String type, String city) {
		
		return lecturerRepo.lecturerPhones(type, city);
	}
	@Override
	public List<StudentCity> studentCityScores(String type, String city, int nScore) {
		
		return studentRepo.studentsCityScores(type, city, nScore);
	}
	@Override
	public List<StudentSubjectMark> studentSubjectMark(String name) {
		
		return studentRepo.studentNameGetSubjects(name);
	}
	

}
