package lottery.user;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

	@Override
	Optional<User> findById(Long id);

	@Query("SELECT u FROM User u WHERE u.userAccount = ?1")
	Optional<User> findByUserAccount(UserAccount userAccount);

	@Query("SELECT u FROM User u WHERE u.userAccount.userAccountIdentifier.id = ?1")
	Optional<User> findByUsername(String userName);

	@Query("SELECT u FROM User u WHERE u.partnerCode = ?1")
	Optional<User> findByPartnerCode(String partnerCode);

	@Override
	Streamable<User> findAll();
}
