package lottery.community;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends CrudRepository<Community, Long> {

	@Query("SELECT u FROM Community u WHERE u.name = ?1")
	Community findByName(String name);

	@Query("SELECT u FROM Community u ORDER BY u.name")
	Streamable<Community> findAll();
}