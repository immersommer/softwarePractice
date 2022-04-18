package lottery.home.message;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface MessageRepository extends CrudRepository<Message, Long> {
	Sort DEFAULT_SORT = Sort.by("date").descending();

	@Override
	Streamable<Message> findAll();

	@Query("SELECT u FROM Message u WHERE u.user = ?1")
	Streamable<Message> findByUser(String user, Sort sort);

	@Query("SELECT u FROM Message u WHERE u.user = ?1 AND u.read = true ORDER BY u.date DESC")
	Streamable<Message> findByUserRead(String user);

	@Query("SELECT u FROM Message u WHERE u.user = ?1 AND u.read = false ORDER BY u.date DESC")
	Streamable<Message> findByUserNotRead(String user);

	@Query("SELECT u FROM Message u WHERE u.user = ?1")
	default Streamable<Message> findByUser(String user) {
		return findByUser(user, DEFAULT_SORT);
	}
}
