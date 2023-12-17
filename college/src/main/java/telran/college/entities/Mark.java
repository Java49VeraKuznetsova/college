package telran.college.entities;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@NoArgsConstructor

@Table(name="marks", indexes = {@Index(columnList = "stid"),
@Index(columnList = "suid")})
public class Mark {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	long id;
	@ManyToOne
	@JoinColumn(name="stid", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	Student student;
	@ManyToOne
	@JoinColumn(name="suid", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	Subject subject;
	@Column(nullable = false)
	int score;
	public Mark(Student student, Subject subject, int score) {
		super();
		this.student = student;
		this.subject = subject;
		this.score = score;
	}
	

}