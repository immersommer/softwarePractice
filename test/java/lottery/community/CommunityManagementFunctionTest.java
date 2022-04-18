package lottery.community;

import lottery.betting.BettingManagement;
import lottery.betting.bet.Bet;
import lottery.betting.bet.CommunityBet;
import lottery.betting.bet.IndividualBet;
import lottery.betting.data.Category;
import lottery.betting.data.Data;
import lottery.betting.data.football.Score;
import lottery.betting.data.number.SelectNumber;
import lottery.user.User;
import lottery.user.UserManagement;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.salespointframework.core.Currencies.EURO;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommunityManagementFunctionTest {

	@Autowired
	CommunityManagement communityManagement;

	@Autowired
	UserManagement userManagement;

	User user;

	@BeforeAll
	void before(){
		user = userManagement.findByUsername("testUser");

		communityManagement.joinCommunity(communityManagement.findCommunityByName("gruppe"),user.getUserAccount());
		System.out.println(communityManagement.findPersonalCommunities(user.getUserAccount()));

	}

//	@Test
//	void testFindPersonalCommunities(){
//		boolean result = communityManagement.findPersonalCommunities(user.getUserAccount()).contains(communityManagement.findByCommunityName("gruppe"));
//		assertThat(result).isEqualTo(true);
//		System.out.println(communityManagement.findPersonalCommunities(user.getUserAccount()));
//	}

	@Test
	void testRemoveFromCommunity(){
		communityManagement.removeFromCommunity(communityManagement.findByCommunityName("gruppe"),user.getUserAccount());
		int result = communityManagement.findByCommunityName("gruppe").getUsers().size();
		assertThat(result).isEqualTo(0);
	}

	@AfterAll
	void after(){

	}


}
