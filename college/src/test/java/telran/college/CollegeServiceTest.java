package telran.college;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.UnexpectedRollbackException;

import telran.college.dto.*;
import telran.college.entities.*;
import telran.college.repo.*;
import telran.college.service.CollegeService;
import telran.exceptions.NotFoundException;
@SpringBootTest
@Sql(scripts = {"db_test.sql"})
class CollegeServiceTest {
private static final long NEW_STUDENT_ID = 200l;
private static final long NEW_LECTURER_ID = 2000l;
private static final long NEW_SUBJECT_ID = 3210l;
@Autowired
CollegeService collegeService;
@Autowired
StudentRepo studentRepo;
@Autowired
LecturerRepo lecturerRepo;
@Autowired
SubjectRepo subjectRepo;
@Autowired
MarkRepo markRepo;

	@Test
	void bestStudentsTypeTest() {
		List<String> students = collegeService.bestStudentsSubjectType(SubjectType.BACK_END, 2);
		String[] expected = {
				"David", "Yosef"
		};
		assertArrayEquals(expected, students.toArray(String[]::new));
	}
	@Test
	void studentsAvgScoreTest() {
		List<NameScore> studentMarks = collegeService.studentsAvgMarks();
		String [] students = {
			"David", "Rivka", "Vasya", "Sara", "Yosef"	
		};
		int [] scores = {
				96, 95, 83, 80, 78
		};
		NameScore[] studentMarksArr = studentMarks.toArray(NameScore[]::new);
		
		IntStream.range(0, students.length)
		.forEach(i -> {
			assertEquals(students[i], studentMarksArr[i].getStudentName());
			assertEquals(scores[i], studentMarksArr[i].getScore());
		});
	}
	@Test
	void lecturersMostHoursTest() {
		List<LecturerHours> lecturersHours = collegeService.lecturersMostHours(2);
		LecturerHours[] lecturersHoursArr = lecturersHours.toArray(LecturerHours[]::new);
		String[] lecturers = {
				"Abraham", "Mozes"
		};
		int [] hours = {
			225, 130	
		};
		IntStream.range(0, hours.length).forEach(i -> {
			assertEquals(lecturers[i], lecturersHoursArr[i].getName());
			assertEquals(hours[i], lecturersHoursArr[i].getHours());
		});
	}
	@Test
	void studentsScoresLessTest() {
		List<StudentCity> studentCityList = collegeService.studentsScoresLess(1);
		assertEquals(1, studentCityList.size());
		StudentCity studentCity = studentCityList.get(0);
		assertEquals("Rehovot",studentCity.getStudentCity());
		assertEquals("Yakob",studentCity.getStudentName());
	}
	@Test
	void studentsBurnMonthTest() {
		String [] namesExpected = {
				"Vasya", "Yakob"
		};
		String [] phonesExpected = {
			"054-1234567", "051-6677889"	
		};
		NamePhone[] studentPhonesArr = collegeService.studentsBurnMonth(10)
				.toArray(NamePhone[]::new);
		assertEquals(phonesExpected.length, studentPhonesArr.length);
		IntStream.range(0,  phonesExpected.length).forEach(i -> {
			assertEquals(namesExpected[i], studentPhonesArr[i].getName());
			assertEquals(phonesExpected[i], studentPhonesArr[i].getPhone());
		});
	}
	@Test
	void lecturesCityTest() {
		String[]expectedNames = {
				"Abraham", "Mozes"
		};
		String[] expectedPhones = {
			"050-1111122", "054-3334567"	
		};
		NamePhone[] namePhones = collegeService.lecturersCity("Jerusalem")
				.toArray(NamePhone[]::new);
		assertEquals(expectedNames.length, namePhones.length);
		IntStream.range(0, namePhones.length).forEach(i -> {
			assertEquals(expectedNames[i], namePhones[i].getName());
			assertEquals(expectedPhones[i], namePhones[i].getPhone());
		});
	}
	@Test
	void subjectsScoresTest() {
		String[] subjects = {
				"Java Core", "Java Technologies", "HTML/CSS", "JavaScript", "React"
		};
		int[] scores = {
				75, 60, 95, 85, 100
		};
		SubjectNameScore[] subjectScores = collegeService.subjectsScores("Vasya")
				.toArray(SubjectNameScore[]::new);
		assertEquals(scores.length, subjectScores.length);
		IntStream.range(0, scores.length).forEach(i -> {
			assertEquals(subjects[i], subjectScores[i].getSubjectName());
			assertEquals(scores[i], subjectScores[i].getScore());
		});
		
	}
	@Test
	void addStudentTest() {
		PersonDto existingStudent = new PersonDto(123, "Crista",
				LocalDate.now(), "city", "phone");
		PersonDto newStudent = new PersonDto(NEW_STUDENT_ID, "Crista",
				LocalDate.now(), "city", "phone");
		assertThrowsExactly(IllegalStateException.class, () -> collegeService.addStudent(existingStudent));
		assertEquals(newStudent, collegeService.addStudent(newStudent));
		assertEquals(newStudent, studentRepo.findById(NEW_STUDENT_ID).get().build());
	}
	@Test
	void addLecturerTest() {
		PersonDto existingLecturer = new PersonDto(1230, "Crista",
				LocalDate.now(), "city", "phone");
		PersonDto newLecturer = new PersonDto(NEW_LECTURER_ID, "Crista",
				LocalDate.now(), "city", "phone");
		assertThrowsExactly(IllegalStateException.class, () -> collegeService.addLecturer(existingLecturer));
		assertEquals(newLecturer, collegeService.addLecturer(newLecturer));
		assertEquals(newLecturer, lecturerRepo.findById(NEW_LECTURER_ID).get().build());
	}
	@Test
	void addSubjectTest() {
		SubjectDto existingSubject = new SubjectDto(321, "Java", 0, null, null);
		SubjectDto newSubject = new SubjectDto(NEW_SUBJECT_ID, "Nodejs", 100, 1230l,
				SubjectType.BACK_END);
		SubjectDto subjectNoLecturer = new SubjectDto(NEW_SUBJECT_ID + 10, "Nodejs", 100, 123l,
				SubjectType.BACK_END);
		assertThrowsExactly(IllegalStateException.class,
				()->collegeService.addSubject(existingSubject));
		assertThrowsExactly(NotFoundException.class,
				()->collegeService.addSubject(subjectNoLecturer));
		assertEquals(newSubject, collegeService.addSubject(newSubject));
		assertEquals(newSubject, subjectRepo.findById(NEW_SUBJECT_ID).get().build());
		
	}
	@Test
	void addMarkTest() {
		MarkDto markNoStudent = new MarkDto(NEW_STUDENT_ID, 321, 100);
		MarkDto markNoSubject = new MarkDto(123, NEW_SUBJECT_ID, 100);
		MarkDto markDto = new MarkDto(128, 321, 100);
		assertThrowsExactly(NotFoundException.class,
				()->collegeService.addMark(markNoStudent));
		assertThrowsExactly(NotFoundException.class,
				()->collegeService.addMark(markNoSubject));
		assertEquals(markDto, collegeService.addMark(markDto));
		List<Mark> marks = markRepo.findByStudentId(128);
		assertEquals(1, marks.size());
	}
	@Test
	void updateStudentTest() {
		//123, 'Vasya', 'Rehovot', '054-1234567', '2000-10-10', 'Student'
		PersonDto noExistingStudent = new PersonDto(NEW_STUDENT_ID, "Crista",
				LocalDate.now(), "city", "phone");
		PersonDto studentUpdated = new PersonDto(123, "Vasya", LocalDate.parse("2000-10-10"), "Lod", "11111111");
		assertThrowsExactly(NotFoundException.class,
				()->collegeService.updateStudent(noExistingStudent));
		assertEquals(studentUpdated, collegeService.updateStudent(studentUpdated));
		assertEquals(studentUpdated, studentRepo.findById(123l).get().build());
		
	}
	@Test
	void updateLecturerTest() {
		//(1230, 'Abraham',  'Jerusalem','050-1111122', '1957-01-23', 'Lecturer')
		PersonDto noExistingLecturer = new PersonDto(NEW_LECTURER_ID, "Abraham",
				LocalDate.parse("1957-01-23"), "Rehovot", "111111111");
		PersonDto lecturerUpdated = new PersonDto(1230l, "Abraham",
				LocalDate.parse("1957-01-23"), "Rehovot", "111111111");
		assertThrowsExactly(NotFoundException.class,
				()->collegeService.updateLecturer(noExistingLecturer));
		assertEquals(lecturerUpdated, collegeService.updateLecturer(lecturerUpdated));
		assertEquals(lecturerUpdated, lecturerRepo.findById(1230l).get().build());
	}
	@Test
	void deleteSubjectTest() {
		//322, 'Java Technologies', 75, 1230, 'BACK_END'
		SubjectDto subjectDto = new SubjectDto(322l, "Java Technologies", 75, 1230l,
				SubjectType.BACK_END);
		assertThrowsExactly(NotFoundException.class,
				()->collegeService.deleteSubject(NEW_SUBJECT_ID));
		assertEquals(subjectDto, collegeService.deleteSubject(322));
		assertNull(subjectRepo.findById(322l).orElse(null));
		
	}
	@Test
	void deleteLecturerTest() {
		//1230, 'Abraham',  'Jerusalem','050-1111122', '1957-01-23', 'Lecturer'
		PersonDto personDto = new PersonDto(1230, "Abraham", LocalDate.parse("1957-01-23"),
				"Jerusalem", "050-1111122");
		assertThrowsExactly(NotFoundException.class,
				()->collegeService.deleteLecturer(NEW_LECTURER_ID));
		assertEquals(personDto, collegeService.deleteLecturer(1230));
		assertNull(lecturerRepo.findById(1230l).orElse(null));
		Subject subject = subjectRepo.findById(322l).get();
		assertNull(subject.getLecturer());
		
	}
	@Test
	void deleteStudentsScoresLess() {
		//128, 'Yakob', 'Rehovot', '051-6677889', '2000-10-10','Student'
		PersonDto [] expected = {
			new PersonDto(128, "Yakob", LocalDate.parse("2000-10-10"), "Rehovot",
					"051-6677889")	
		};
		PersonDto [] actual  = collegeService.deleteStudentsHavingScoresLess(2)
				.toArray(PersonDto[]::new);
		assertArrayEquals(expected, actual);
		assertNull(studentRepo.findById(128l).orElse(null));
	}
	@Test
	void sqlRequestTest() {
		QueryDto queryDto = new QueryDto("select * from students_lecturers", QueryType.SQL);
		System.out.println(collegeService.anyQuery(queryDto));
		
		
	}
	@Test
	void sqlRequestWrongRest() {
		QueryDto queryDto = new QueryDto("select * from students", QueryType.SQL);
		System.out.println(collegeService.anyQuery(queryDto));
	}
	@Test
	void jpqlRequestTest() {
		QueryDto queryDto = new QueryDto("select student from Student student", QueryType.JPQL);
		System.out.println(collegeService.anyQuery(queryDto));
		
		
	}
	@Test
	void jpqlRequestWrongRest() {
		try {
			QueryDto queryDto = new QueryDto("select student from Student", QueryType.JPQL);
			System.out.println(collegeService.anyQuery(queryDto));
		} catch (UnexpectedRollbackException e) {
			System.out.println(e.getMessage());
		}
	}

}