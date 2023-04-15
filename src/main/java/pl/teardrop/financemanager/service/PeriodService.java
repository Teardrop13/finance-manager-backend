package pl.teardrop.financemanager.service;

import pl.teardrop.authentication.user.User;
import pl.teardrop.financemanager.model.Period;
import pl.teardrop.financemanager.repository.PeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeriodService {

	private final PeriodRepository periodRepository;

	public List<Period> getByUser(User user) {
		return periodRepository.findByUserOrderByStartsOn(user);
	}

	public Optional<Period> getById(long id) {
		return periodRepository.findById(id);
	}

	public Period save(Period period) {
		Period periodAdded = periodRepository.save(period);
		log.info("Saved period id={}, userId={}", periodAdded.getId(), periodAdded.getUser().getId());
		return periodAdded;
	}

	public void delete(long id) {
		periodRepository.deleteById(id);
		log.info("Period deleted id={}", id);
	}
}