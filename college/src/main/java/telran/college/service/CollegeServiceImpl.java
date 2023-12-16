package telran.college.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.college.dto.*;
import telran.college.entities.*;
import telran.college.repo.*;
import telran.exceptions.NotFoundException;
@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class CollegeServiceImpl implements CollegeService {
	final StudentRepo studentRepo;
	final LecturerRepo lecturerRepo;
	final SubjectRepo subjectRepo;
	final MarkRepo markRepo;
	@Override
	public List<String> bestStudentsSubjectType(SubjectType type, int nStudents) {
		
		return studentRepo.findBestStudentsSubjectType( type, nStudents);
	}
	@Override
	public List<NameScore> studentsAvgMarks() {
		
		return studentRepo.studentsMarks();
	}
	@Override
	public List<LecturerHours> lecturersMostHours(int nLecturers) {
		
		return lecturerRepo.findLecturersMostHours(nLecturers);
	}
	@Override
	public List<StudentCity> studentsScoresLess(int nThreshold) {
		
		return studentRepo.findStudentsScoresLess(nThreshold);
	}
	@Override
	public List<NamePhone> studentsBurnMonth(int month) {
		
		return studentRepo.findStudentsBurnMonth(month);
	}
	@Override
	public List<NamePhone> lecturersCity(String city) {
		
		return lecturerRepo.findByCity(city);
	}
	@Override
	public List<SubjectNameScore> subjectsScores(String studentName) {
		
		return markRepo.findByStudentName(studentName);
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto addStudent(PersonDto personDto) {
		if(studentRepo.existsById(personDto.id())) {
			throw new IllegalStateException(personDto.id() + " already exists");
		}
		studentRepo.save(new Student(personDto));
		return personDto;
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto addLecturer(PersonDto personDto) {
		// Auto-generated method stub
		if(lecturerRepo.existsById(personDto.id())) {
			throw new IllegalStateException(personDto.id() + " already exists");
		}
		lecturerRepo.save(new Lecturer(personDto));
		return personDto;
	}
	@Override
	@Transactional(readOnly = false)
	public SubjectDto addSubject(SubjectDto subjectDto) {
		//ADD by me
		/*
		if(subjectRepo.existsById(subjectDto.id())) {
			throw new IllegalStateException(subjectDto.id() + " already exists");
		}
		*/
		Lecturer lecturer = null;
		if(subjectDto.lecturerId() != null) {
			lecturer = lecturerRepo.findById(subjectDto.lecturerId())
					.orElseThrow(() -> new NotFoundException(subjectDto.lecturerId() + "not exists"));
		}
		Subject subject = new Subject(subjectDto);
		subject.setLecturer(lecturer);
		subjectRepo.save(subject);
		return subjectDto;
	}
	@Override
	@Transactional(readOnly = false)
	public MarkDto addMark(MarkDto markDto) {
		// Auto-generated method stub
		
		Student	student = studentRepo.findById(markDto.studentId())
					.orElseThrow(() -> new NotFoundException(markDto.studentId() + " not exists"));
		Subject	subject = subjectRepo.findById(markDto.subjectId())
					.orElseThrow(() -> new NotFoundException(markDto.subjectId() + " not exists"));
		Mark mark = new Mark(markDto);
		mark.setStudent(student);
		mark.setSubject(subject);
			markRepo.save(mark);
		
		return markDto;
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto updateStudent(PersonDto personDto) {
		Student student = studentRepo.findById(personDto.id())
				.orElseThrow(() -> new NotFoundException(personDto.id() + " not exists"));
		student.setCity(personDto.city());
		student.setPhone(personDto.phone());
		
		return personDto;
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto updateLecturer(PersonDto personDto) {
		Lecturer lecturer = lecturerRepo.findById(personDto.id())
				.orElseThrow(() -> new NotFoundException(personDto.id() + " not exists"));
		lecturer.setCity(personDto.city());
		lecturer.setPhone(personDto.phone());
		
		return personDto;
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto deleteLecturer(long id) {
		
		// TODO Auto-generated method stub
		/*
		Lecturer lecturer = lecturerRepo.findById(id)
				.orElseThrow(() -> new NotFoundException(id +" no lecturer with such id"));
	List<Subject> subjects = subjectRepo.findBySubjectId(id);
	subjects.forEach(s->s.setLecturer(null));
	lecturerRepo.delete(lecturer);
	*/
		//find Lecturer by id (with possible NotFoundException)
		//find all subjects with a given lecturer
		// update all subjects with being deleted lecturer by setting null in field Lecturer
		//lecturerRepo.delete(lecturer)
		//returns lecturer.build();
		
		//return lecturer.build();
		return null;
	}
	@Override
	@Transactional(readOnly = false)
	public SubjectDto deleteSubject(long id) {
		//  Auto-generated method stub
		/*
		Subject subject = subjectRepo.findById(id)
				.orElseThrow(() -> new NotFoundException(id +" no lecturer with such id"));
		List<Mark> marks = markRepo.findBySubjectId(id);
		marks.forEach(m -> markRepo.delete(m));
		subjectRepo.deleteById(id);
		return subject.build();
		*/
		return null;
	}
	@Override
	@Transactional(readOnly = false)
	public List<PersonDto> deleteStudentsHavingScoresLess(int nScores) {
		// TODO Auto-generated method stub
		/*
		List<Student> students = 
				studentRepo.deleteStudentsHavingScoresLess(nScores);
		students.forEach(s -> {
			long id = s.getId();
			markRepo.deleteByStudentId(id);
			studentRepo.deleteById(id);
		});
	*/
		
		return null;
	}

}