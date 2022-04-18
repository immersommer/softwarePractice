package lottery.finance;



import lottery.user.User;
import lottery.user.UserManagement;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FinanceManagementTest {

	@Resource
	FinanceManagement financeManagement;

	@Autowired
	UserManagement userManagement;

	User user;

	@BeforeAll
	void before(){
		for(FinanceEntry financeEntry : financeManagement.findAll()){
			financeManagement.deleteEntry(financeEntry.getId());
		}
		user = userManagement.findByUsername("testUser");
		financeManagement.deposit(new FinanceForm(30.0,""), user.getUserAccount());
		financeManagement.deposit(new FinanceForm(1.0,""), user.getUserAccount());
	}


	@Test
	void testBalanceAndDeposit() {
		Money balance = financeManagement.getUserBalance(user.getUserAccount());
		assertThat(balance).isEqualTo(Money.of(31.0,"EUR"));
	}

	@Test
	void testWithdrawAmountWithNotEnoughMoney() {
		boolean isWithdraw = financeManagement.withdraw(new FinanceForm(40.0,""), user.getUserAccount());
		assertThat(isWithdraw).isFalse();
	}

	@Test
	void testWithdrawAmountWithEnoughMoney() {
		boolean isWithdraw = financeManagement.withdraw(new FinanceForm(20.0,""), user.getUserAccount());
		assertThat(isWithdraw).isTrue();
	}

	@Test
	void testAllEntries() {
		List<FinanceEntry> financeEntries = financeManagement.findAll().toList();
		assertThat(financeEntries.size()).isEqualTo(2);
	}

	@Test
	void testAllEntriesByUser() {
		List<FinanceEntry> financeEntries = financeManagement.findEntriesByUser(user.getUserAccount()).toList();
		assertThat(financeEntries.size()).isEqualTo(2);
	}

	@AfterAll
	void after(){
		for(FinanceEntry financeEntry : financeManagement.findAll()){
			financeManagement.deleteEntry(financeEntry.getId());
		}
	}
}
