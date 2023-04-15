package pl.teardrop.financemanager.model;

import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.dto.PeriodDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "FM_PERIOD")
public class Period {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@Column(name = "STARTS_ON", nullable = false)
	private LocalDate startsOn;

	@Column(name = "ENDS_ON", nullable = false)
	private LocalDate endsOn;

	public PeriodDTO toDTO() {
		return new PeriodDTO(startsOn, endsOn);
	}

}
