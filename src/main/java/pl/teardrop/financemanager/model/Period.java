package pl.teardrop.financemanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.dto.PeriodDTO;

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
