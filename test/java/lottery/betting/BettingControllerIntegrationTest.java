package lottery.betting;

import lottery.betting.bet.Bet;
import lottery.betting.bet.BetRepository;
import lottery.betting.data.Category;
import lottery.betting.data.football.FootballMatch;
import lottery.betting.data.number.LotteryEntity;
import lottery.user.UserManagement;
import lottery.user.UserRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;


import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.AssertEquals.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.salespointframework.core.Currencies.EURO;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BettingControllerIntegrationTest {

	@Resource
	private BettingController bettingController;

	@Autowired
	UserManagement userManagement;

	@Autowired
	UserRepository userRepository;

	@Mock
	UserAccount user;

	@Test
	@WithMockUser(username="user")
	void testToBetting() {
		String viewHome = bettingController.betting();
		assertThat(viewHome).isEqualTo("betting");
	}

	@Test
	@WithMockUser(username="user")
	void testToNumber() {
		Model model = new ExtendedModelMap();
		String viewHome = bettingController.number(user, model);
		assertThat(viewHome).isEqualTo("betting_number");
	}

	@Test
	@WithMockUser(username="user")
	void testToFootball() {
		Model model = new ExtendedModelMap();
		String viewHome = bettingController.football(user, model);
		assertThat(viewHome).isEqualTo("betting_football");
	}

	@Test
	@WithMockUser(username="user")
	void testToChangeView() {
		Model model = new ExtendedModelMap();
		String viewHome = bettingController.changeList(user, model);
		assertThat(viewHome).isEqualTo("betting_changeList");
	}
}
