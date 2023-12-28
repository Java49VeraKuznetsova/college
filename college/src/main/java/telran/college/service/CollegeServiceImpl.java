package telran.college.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import telran.college.dto.*;
import telran.college.entities.*;
import telran.college.repo.*;
import telran.exceptions.NotFoundException;
@Service
@RequiredArgsConstructor

public class CollegeServiceImpl implements CollegeService {
	final StudentRepo studentRepo;
	final LecturerRepo lecturerRepo;
	final SubjectRepo subjectRepo;
	final MarkRepo markRepo;
	final EntityManager em;
	@Override
	@Transactional(readOnly=true)
	public List<String> bestStudentsSubjectType(SubjectType type, int nStudents) {
		
		return studentRepo.findBestStudentsSubjectType( type, nStudents);
	}
	@Override
	@Transactional(readOnly=true)
	public List<NameScore> studentsAvgMarks() {
		
		return studentRepo.studentsMarks();
	}
	@Override
	@Transactional(readOnly=true)
	public List<LecturerHours> lecturersMostHours(int nLecturers) {
		
		return lecturerRepo.findLecturersMostHours(nLecturers);
	}
	@Override
	@Transactional(readOnly=true)
	public List<StudentCity> studentsScoresLess(int nThreshold) {
		
		return studentRepo.findStudentCitiesScoresLess(nThreshold);
	}
	@Override
	@Transactional(readOnly=true)
	public List<NamePhone> studentsBurnMonth(int month) {
		
		return studentRepo.findStudentsBurnMonth(month);
	}
	@Override
	@Transactional(readOnly=true)
	public List<NamePhone> lecturersCity(String city) {
		
		return lecturerRepo.findByCity(city);
	}
	@Override
	@Transactional(readOnly=true)
	public List<SubjectNameScore> subjectsScores(String studentName) {
		
		return markRepo.findByStudentName(studentName);
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto addStudent(PersonDto personDto) {
		if(studentRepo.existsById(personDto.id())) {
			throw new IllegalStateException("student " + personDto.id() + " already exists");
		}
		studentRepo.save(new Student(personDto));
		return personDto;
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto addLecturer(PersonDto personDto) {
		if(lecturerRepo.existsById(personDto.id())) {
			throw new IllegalStateException("lecturer " + personDto.id() + " already exists");
		}
		lecturerRepo.save(new Lecturer(personDto));
		return personDto;
	}
	@Override
	@Transactional(readOnly = false)
	public SubjectDto addSubject(SubjectDto subjectDto) {
		if(subjectRepo.existsById(subjectDto.id())) {
			throw new IllegalStateException("subject " + subjectDto.id() + " already exists");
		}
		Lecturer lecturer = null;
		if(subjectDto.lecturerId() != null) {
			lecturer = findLecturer(subjectDto.lecturerId());
		}
		Subject subject = new Subject(subjectDto);
		subject.setLecturer(lecturer);
		subjectRepo.save(subject);
		return subjectDto;
	}
	private Lecturer findLecturer(long id) {
		return lecturerRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("lecturer " + id + "not exists"));
	}
	private Student findStudent(long id) {
		return studentRepo.findById(id)
				.orElseThrow(() -> new NotFoundException("student " + id + "not exists"));
	}
	@Override
	@Transactional(readOnly = false)
	public MarkDto addMark(MarkDto markDto) {
		Student student = findStudent(markDto.studentId());
		Subject subject = findSubject(markDto.subjectId());
		Mark mark = new Mark(student, subject, markDto.score());
		markRepo.save(mark);
		return markDto;
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto updateStudent(PersonDto personDto) {
		return updatePerson(personDto, studentRepo, "student");
	}
	private  PersonDto updatePerson(PersonDto personDto, JpaRepository<? extends Person, Long> repo,
			String personRole) {
		Person person = repo.findById(personDto.id())
				.orElseThrow(() ->
				new NotFoundException(String.format("%s with id %d doesn't exist",
						personRole, personDto.id())));
		person.setCity(personDto.city());
		person.setPhone(personDto.phone());
		return personDto;
	}
	@Override
	@Transactional
	public PersonDto updateLecturer(PersonDto personDto) {
		
		return updatePerson(personDto, lecturerRepo, "lecturer");
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto deleteLecturer(long id) {
		Lecturer lecturer = findLecturer(id);
		lecturerRepo.delete(lecturer);
		return lecturer.build();
	}
	@Override
	@Transactional(readOnly = false)
	public SubjectDto deleteSubject(long id) {
		Subject subject = findSubject(id);
		subjectRepo.delete(subject);
		return subject.build();
	}
	private Subject findSubject(long id) {
		return subjectRepo.findById(id).
				orElseThrow(() -> new NotFoundException(String.format("subject %d not found",
						id)));
	}
	@Override
	@Transactional(readOnly = false)
	public List<PersonDto> deleteStudentsHavingScoresLess(int nScores) {
		List<Student> students = getStudentsHavingScoresLess(nScores);
		students.forEach(this::deleteStudent);
		return students.stream().map(Student::build).toList();
	}
	private List<Student> getStudentsHavingScoresLess(int nScores) {
		
		return studentRepo.findStudentsScoresLess(nScores);
	}
	void deleteStudent(Student student) {
		
		studentRepo.delete(student);
	}
	@Override
	
	public List<String> anyQuery(QueryDto queryDto) {
		String queryStr = queryDto.query();
		List<String> res = null;
		Query query;
		try {
			query = queryDto.queryType() == QueryType.SQL ?
					em.createNativeQuery(queryStr) : em.createQuery(queryStr);
			res = getResult(query);
		} catch (Throwable e) {
			res = List.of(e.getMessage());
		}
		return res;
	}
	@SuppressWarnings("unchecked")
	private List<String> getResult(Query query) {
		List<String> res = Collections.emptyList();
		List<?> resultList = Collections.emptyList();
		try {
			resultList = query.getResultList();
		} catch (Exception e) {
			res = List.of(e.getMessage());
		}
		
		if (!resultList.isEmpty()) {
			res = resultList.get(0).getClass().isArray() ?
					listObjectArraysProcessing((List<Object[]>)resultList) : 
						listObjectsProcessing(resultList);
		}
		return res;
	}
	private List<String> listObjectsProcessing(List<?> resultList) {
		
		try {
			return resultList.stream().map(Object::toString).toList();
		} catch (Exception e) {
			return List.of(e.getMessage());
		}
	}
	private List<String> listObjectArraysProcessing(List<Object[]> resultList) {
		
		return resultList.stream().map(Arrays::deepToString).toList();
	}

}