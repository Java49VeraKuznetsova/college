package telran.college;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import telran.college.dto.*;
import telran.college.service.CollegeService;
import telran.exceptions.NotFoundException;
@SpringBootTest
@Sql(scripts = {"db_test.sql"})
class CollegeServiceTest {
@Autowired
CollegeService collegeService;
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
		LocalDate birthDate = LocalDate.parse("2006-08-16");
		PersonDto newStudent = new PersonDto (129l, "Shmulik", birthDate, "Jerusalem", "05801234567");
		PersonDto newStudentWrongID = new PersonDto (123l, "Shmulik", birthDate, "Jerusalem", "05801234567");
		PersonDto student = collegeService.addStudent(newStudent);
		assertEquals("Shmulik", student.name());
		assertEquals(129l, student.id());
		assertEquals("Jerusalem", student.city());
		assertEquals("05801234567", student.phone());
		assertEquals(birthDate, student.birthDate());
		assertThrows(IllegalStateException.class, ()->  collegeService.addStudent(newStudentWrongID));
		assertEquals(newStudent, student);
		
	
	}
	@Test
	void addLecturerTest() {
		LocalDate birthDate = LocalDate.parse("1996-09-16");
		PersonDto newLecturerWrongID = new PersonDto(1230l,"Shmulik", birthDate, "Jerusalem", "05801234567");
		PersonDto newLecturer = new PersonDto(1233l,"Shmulik", birthDate, "Jerusalem", "05801234567");
		PersonDto lecturer = collegeService.addLecturer(newLecturer);
				assertThrows(IllegalStateException.class, ()-> collegeService.addLecturer(newLecturerWrongID));
				assertEquals(1233l, lecturer.id());
				assertEquals("Shmulik", lecturer.name());
				assertEquals(birthDate, lecturer.birthDate());
				assertEquals("Jerusalem", lecturer.city());
				assertEquals("05801234567", lecturer.phone());
				assertEquals(newLecturer, lecturer);
	}
	@Test
	void addSubjectTest() {
		SubjectDto newSubject = new SubjectDto(326l, "Tecnology", 100, 1230l, SubjectType.BACK_END);
		//??
		SubjectDto newSubjectWrongID = new SubjectDto(322l, "Tecnology", 100, 1230L, SubjectType.BACK_END);
		SubjectDto newSubjectWrongLecturer = new SubjectDto(326l, "Tecnology", 100, 1235L, SubjectType.BACK_END);
		
		SubjectDto subject = collegeService.addSubject(newSubject);
		assertEquals("Tecnology", subject.name());
		assertEquals(newSubject, subject);
		assertThrows(NotFoundException.class, () -> collegeService.addSubject(newSubjectWrongLecturer));
		//??
		//assertThrows(IllegalStateException.class, () -> collegeService.addSubject(newSubjectWrongID));
	}
	@Test
	void addMarkTest() {
		MarkDto newMark = new MarkDto(128l, 321l, 95);
		MarkDto newMarkWrongStudentID = new MarkDto(129l, 321l, 95);
		MarkDto newMarkWrongSubjectID = new MarkDto(128l, 330l, 95);
		
		MarkDto mark = collegeService.addMark(newMark);
		assertEquals(newMark, mark);
		assertThrows(NotFoundException.class, () -> collegeService.addMark(newMarkWrongSubjectID));
		assertThrows(NotFoundException.class, () -> collegeService.addMark(newMarkWrongStudentID));
	}
	@Test
	void updateStudentTest() {
		LocalDate birthDate = LocalDate.parse("1990-11-12");
		PersonDto updateStudentSara = new PersonDto (124l, "Sara", birthDate, "Jerusalem", "05801234567");
		PersonDto updateStudentWrongID = new PersonDto (134l, "Sara", birthDate, "Jerusalem", "05801234567");
		PersonDto studentNewSara = collegeService.updateStudent(updateStudentSara);
		
		assertThrows(NotFoundException.class,() -> collegeService.updateStudent(updateStudentWrongID));
		assertEquals("Jerusalem", studentNewSara.city());
		assertEquals("05801234567", studentNewSara.phone());
		
	}
	@Test 
	void updateLecurerTest() {
		PersonDto updateLectorMozes = new PersonDto(1231l, "Mozes", LocalDate.parse("1963-10-20"), "Lod", "05801234567");
		PersonDto updateLectorWrongID = 
				new PersonDto(1331l, "Mozes", LocalDate.parse("1963-10-20"), "Lod", "05801234567");
		
		
		assertThrows(NotFoundException.class, () -> collegeService.updateLecturer(updateLectorWrongID));
		assertEquals(updateLectorMozes, collegeService.updateLecturer(updateLectorMozes));
	}

}