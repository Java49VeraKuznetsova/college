package telran.college.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import telran.college.dto.MarkDto;
@Entity
@NoArgsConstructor
@Table(name="marks")
@Getter
public class Mark {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	long id;
	@ManyToOne
	@JoinColumn(name="stid", nullable = false)
	Student student;
	@ManyToOne
	@JoinColumn(name="suid", nullable = false)
	Subject subject;
	@Column(nullable = false)
	int score;
	public Mark (MarkDto markDto) {
		score = markDto.score();
		
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public MarkDto build() {
		return new MarkDto(student.id, subject.id, score);
	}

}