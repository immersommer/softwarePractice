package lottery.finance;

import lottery.user.User;
import lottery.user.UserManagement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FinanceControllerFunctionTest {
	@Resource
	FinanceController financeController;

	@Autowired
	FinanceManagement financeManagement;

	@Autowired
	UserManagement userManagement;

	User user;

	@Mock
	Errors errors;

	@BeforeAll
	void before(){
		Model model = new ExtendedModelMap();
		user = userManagement.findByUsername("testUser");
		financeController.depositOrWithdraw(new FinanceForm(30.0,""), errors, model,
				"deposit", user.getUserAccount());
	}

	@Test
	void testWithdrawAmountWithNotEnoughMoney() {
		Model model = new ExtendedModelMap();
		String viewHome = financeController.depositOrWithdraw(new FinanceForm(100.0,""), errors, model,
				"withdraw", user.getUserAccount());
		assertThat(viewHome).isEqualTo("redirect:/finances?error");
	}

	@Test
	void testWithdrawAmountWithEnoughMoney() {
		Model model = new ExtendedModelMap();
		String viewHome = financeController.depositOrWithdraw(new FinanceForm(10.0,""), errors, model,
				"withdraw", user.getUserAccount());
		assertThat(viewHome).isEqualTo("redirect:/finances");
	}

	@Test
	void testDeposit() {
		Model model = new ExtendedModelMap();
		String viewHome = financeController.depositOrWithdraw(new FinanceForm(10.0,""), errors, model,
				"deposit", user.getUserAccount());
		assertThat(viewHome).isEqualTo("redirect:/finances");
	}

	@AfterAll
	void after() {
		for (FinanceEntry financeEntry : financeManagement.findAll()) {
			financeManagement.deleteEntry(financeEntry.getId());
		}
	}
}
