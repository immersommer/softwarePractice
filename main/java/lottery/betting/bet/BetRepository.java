package lottery.betting.bet;

import lottery.betting.data.Category;
import lottery.betting.data.Data;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface BetRepository extends CrudRepository<Bet, Long> {

	@Query("SELECT u FROM Bet u WHERE u.id = ?1 AND u.evaluate = false")
	Bet findBetById(long id);

	@Query("SELECT u FROM Bet u WHERE u.type = lottery.betting.bet.Type.COMMUNITY AND u.id = ?1 AND u.evaluate = false")
	CommunityBet findCommunityBetById(long id);

	@Query("SELECT u FROM Bet u WHERE u.origin = ?1 AND u.evaluate = false")
	Streamable<Bet> findBetsByOrigin(String user);

	@Query("SELECT u FROM Bet u WHERE u.type = lottery.betting.bet.Type.COMMUNITY " +
			"AND u.origin = ?1 AND u.reference.category = ?2 AND u.evaluate = false")
	Streamable<CommunityBet> findCommunityBetsByCommunityAndCategory(String community, Category category);

	@Query("SELECT u FROM Bet u WHERE u.evaluate = false")
	Streamable<Bet> findBetsNotEvaluated();

	@Query("SELECT u FROM Bet u WHERE u.reference = ?1 AND u.evaluate = ?2")
	Streamable<Bet> findBetsByDataAndEvaluate(Data data, boolean evaluate);

	@Override
	Streamable<Bet> findAll();

	@Override
	void deleteById(Long aLong);
}
