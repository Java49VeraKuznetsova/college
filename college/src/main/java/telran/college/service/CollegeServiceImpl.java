package telran.college.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
		
		return studentRepo.findStudentCitiesScoresLess(nThreshold);
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
	@Transactional(readOnly = false)
	public PersonDto updateLecturer(PersonDto personDto) {
		
		return updatePerson(personDto, lecturerRepo, "lecturer");
	}
	@Override
	@Transactional(readOnly = false)
	public PersonDto deleteLecturer(long id) {
		Lecturer lecturer = findLecturer(id);
		List<Subject> subjects = subjectRepo.findByLecturerId(id);
		subjects.forEach(s -> s.setLecturer(null));
		lecturerRepo.delete(lecturer);
		return lecturer.build();
	}
	@Override
	@Transactional(readOnly = false)
	public SubjectDto deleteSubject(long id) {
		Subject subject = findSubject(id);
		List<Mark> marks = markRepo.findBySubjectId(id);
		marks.forEach(markRepo::delete);
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
		List<Mark> marks = markRepo.findByStudentId(student.getId());
		marks.forEach(markRepo::delete);
		studentRepo.delete(student);
	}

}