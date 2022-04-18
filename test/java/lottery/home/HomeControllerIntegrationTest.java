package lottery.home;

import lottery.betting.BettingManagement;
import lottery.finance.FinanceManagement;
import lottery.home.message.MessageManagement;
import lottery.user.User;
import lottery.user.UserManagement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import javax.annotation.Resource;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HomeControllerIntegrationTest {
	@Resource
	HomeController homeController;

	@Autowired
	UserManagement userManagement;

	UserAccount user;

	@BeforeAll
	void before(){
		user = userManagement.findByUsername("testUser").getUserAccount();
	}

	@Test
	@WithMockUser(username="testUser")
	void testHomeAdmin() {
		Model model = new ExtendedModelMap();
		String viewHome = homeController.home(user, model);
		assertThat(viewHome).isEqualTo("home");
	}

	@Test
	@WithMockUser(username="testUser")
	void testMessage() {
		Model model = new ExtendedModelMap();
		String viewHome = homeController.message(user, model);
		assertThat(viewHome).isEqualTo("message");
	}
}
