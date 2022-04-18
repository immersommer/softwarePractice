package lottery.finance;

import lottery.user.User;
import lottery.user.UserManagement;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FinanceEntryTest {

	@Autowired
	UserManagement userManagement;

	User user;
	private static final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

	@BeforeAll
	void before(){
		user = userManagement.findByUsername("testUser");
	}
	@Test
	void testAmount() {
		FinanceEntry financeEntry = new FinanceEntry(user.getUserAccount(), 100.0 ,"Test", LocalDateTime.now());
		assertEquals(Money.of(100.0, "EUR"), financeEntry.getAmount());
	}

	@Test
	void testNote() {
		FinanceEntry financeEntry = new FinanceEntry(user.getUserAccount(), 100.0 ,"Test", LocalDateTime.now());
		assertEquals("Test", financeEntry.getNote());
	}

	@Test
	void testUser() {
		FinanceEntry financeEntry = new FinanceEntry(user.getUserAccount(), 100.0 ,"Test", LocalDateTime.now());
		assertEquals(user.getUserAccount().getUsername(), financeEntry.getUser());
	}

	@Test
	void testDate() {
		FinanceEntry financeEntry = new FinanceEntry(user.getUserAccount(), 100.0 ,"Test", LocalDateTime.now());
		assertEquals(LocalDateTime.now().format(formatDateTime), financeEntry.getDate());
	}

	@Test
	void testRejectsEmptyUser() {

		assertThatExceptionOfType(IllegalArgumentException.class)//
			.isThrownBy(() -> new FinanceEntry(null, 100.0 ,"Test", LocalDateTime.now()));
	}

	@Test
	void testRejectsEmptyAmount() {

		assertThatExceptionOfType(IllegalArgumentException.class)//
				.isThrownBy(() -> new FinanceEntry(user.getUserAccount(), null ,"Test", LocalDateTime.now()));
	}


}