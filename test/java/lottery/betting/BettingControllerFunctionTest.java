package lottery.betting;

import lottery.betting.bet.Bet;
import lottery.betting.bet.IndividualBet;
import lottery.betting.data.Category;
import lottery.betting.data.Data;
import lottery.betting.data.football.FootballMatch;
import lottery.betting.data.football.Score;
import lottery.betting.data.number.LotteryEntity;
import lottery.betting.data.number.SelectNumber;
import lottery.finance.FinanceForm;
import lottery.finance.FinanceManagement;
import lottery.user.User;
import lottery.user.UserManagement;
import lottery.user.UserRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.salespointframework.core.Currencies.EURO;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BettingControllerFunctionTest {

	@Resource
	BettingController bettingController;

	@Autowired
	BettingManagement bettingManagement;

	@Autowired
	FinanceManagement financeManagement;

	@Autowired
	UserManagement userManagement;

	User user;

	FootballMatch match1;

	@Mock
	FootballMatch match2;

	@BeforeAll
	void before(){
		user = userManagement.findByUsername("testUser");
		match1 = new FootballMatch("test1", Money.of(1, EURO), LocalDateTime.now(),
				Category.FOOTBALL,"test1","test2");
		financeManagement.deposit(new FinanceForm(25.0,""),user.getUserAccount());

		bettingManagement.saveBet(new IndividualBet(match1, new Score(2,1),
				user.getUserAccount(), Money.of(10, EURO)));
		bettingManagement.saveBet(new IndividualBet(new LotteryEntity("test2",Money.of(10, EURO),
				LocalDateTime.now(),Category.LOTTERY,"testLotto"), new SelectNumber("1,2,3,4,5,6",1),
				user.getUserAccount(), Money.of(10, EURO)));
	}

	@Test
	void testAddFootballBetNotEnoughMoney() {
		String viewHome = bettingController.addBet(user.getUserAccount(), match2, 2, 1, 40, "");
		assertThat(viewHome).isEqualTo("redirect:/message");
	}

	@Test
	void testUpdateFootballBetNotEnoughMoney() {
		long betId = bettingManagement.findBetsByUser("testUser").toList().get(0).getId();
		String viewHome = bettingController.updateFootballBet(user.getUserAccount(),betId,1,2,50);
		assertThat(viewHome).isEqualTo("redirect:/message");

	}

	@Test
	void testUpdateFootballBet() {
		long betId = bettingManagement.findBetsByUser("testUser").toList().get(0).getId();
		bettingController.updateFootballBet(user.getUserAccount(),betId,1,2,10);
		String result = bettingManagement.findBetsByUser("testUser").toList().get(0).getValue().toString();
		assertThat(result).isEqualTo(new Score(1,2).toString());
	}

	@Test
	void testUpdateLotteryBet() {
		long betId = bettingManagement.findBetsByUser("testUser").toList().get(1).getId();
		bettingController.updateLotteryBet(betId,"2,3,4,5,6,7",2);
		String result = bettingManagement.findBetsByUser("testUser").toList().get(1).getValue().toString();
		assertThat(result).isEqualTo(new SelectNumber("2,3,4,5,6,7",2).toString());
	}

	@AfterAll
	void after(){
		for(Bet bet : bettingManagement.findAllBets()){
			bettingManagement.deleteBet(bet.getId());
		}
	}
}
