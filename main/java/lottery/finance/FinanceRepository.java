package lottery.finance;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

@EntityScan("bean.FinanceRepository")
public interface FinanceRepository extends CrudRepository<FinanceEntry, Long> {

	Sort DEFAULT_SORT = Sort.by("date").descending();

	@Override
	Optional<FinanceEntry> findById(Long aLong);

	@Override
	Streamable<FinanceEntry> findAll();

	@Query("SELECT u FROM FinanceEntry u WHERE u.user = ?1")
	Streamable<FinanceEntry> findByUser(String user, Sort sort);

	@Query("SELECT u FROM FinanceEntry u WHERE u.user = ?1")
	default Streamable<FinanceEntry> findByUser(String user) {
		return findByUser(user, DEFAULT_SORT);
	}

}
