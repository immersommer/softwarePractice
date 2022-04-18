package lottery.betting;

import lottery.betting.bet.Bet;
import lottery.betting.bet.BetRepository;
import lottery.betting.bet.CommunityBet;
import lottery.betting.bet.IndividualBet;
import lottery.betting.data.Category;
import lottery.betting.data.Data;
import lottery.betting.data.DataCatalog;
import lottery.betting.data.football.Score;
import lottery.betting.data.number.SelectNumber;
import lottery.user.User;
import lottery.user.UserManagement;
import lottery.user.UserRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Answers;
import org.mockito.Mock;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.salespointframework.core.Currencies.EURO;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BettingManagementFunctionTest {

	@Autowired
	BettingManagement management;

	@Autowired
	UserManagement userManagement;

	User user;

	@Mock
	UserAccount mockUser;

	@BeforeAll
	void before(){
		user = userManagement.findByUsername("testUser");

		management.saveBet(new IndividualBet(null, new Score(2,1),
				user.getUserAccount(), Money.of(10, EURO)));
		management.saveBet(new IndividualBet(null, new SelectNumber("1,2,3,4,5,6",1),
				mockUser, Money.of(10, EURO)));
	}

	@Test
	void testAllData() {
		Streamable<Data> result = management.findAllData();
		assertThat(result).hasSize(87);
//		assertThat(result).hasSize(645);
	}

	@Test
	void testCategoryData() {
		Streamable<Data> result = management.findDataByCategory(Category.LOTTERY);
		assertThat(result).hasSize(69);
	}

	@Test
	void testAllBets() {
		Streamable<Bet> result = management.findAllBets();
		assertThat(result).hasSize(2);
	}

	@Test
	void testUserBets() {
		Streamable<Bet> result = management.findBetsByUser("testUser");
		assertThat(result).hasSize(1);
	}

	@Test
	void testCommunityBets() {
		Streamable<CommunityBet> result = management.findBetsByCommunityAndCategory("gruppe", Category.FOOTBALL);
		assertThat(result).hasSize(0);
	}

	@Test
	void testMoneyAll() {
		double result = management.getMoneyFromAllBets().getNumber().doubleValue();
		assertThat(result).isEqualTo(20.0);
	}

	@AfterAll
	void after(){
		for(Bet bet : management.findAllBets()){
			management.deleteBet(bet.getId());
		}
	}
}
