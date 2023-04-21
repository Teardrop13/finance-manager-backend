package pl.teardrop.financemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountingPeriodDTO {
	private long id;
	private LocalDate startsOn;
	private LocalDate endsOn;
}
