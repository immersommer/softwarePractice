package lottery.finance;

import lottery.home.message.MessageManagement;
import lottery.user.UserManagement;

import static org.assertj.core.api.Assertions.*;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class FinanceControllerIntegrationTest {

	@Mock
	UserAccount user;

	@Mock
	FinanceManagement finances;

	@Mock
	MessageManagement messages;

	@Test
	void testPopulatesModelForFinance() {

		Model model = new ExtendedModelMap();
		FinanceForm form = new FinanceForm(100.0, "");
		finances.deposit(form, user);
		FinanceController controller = new FinanceController(finances, messages);
		String viewName = controller.finances(user, model, new FinanceForm(100.0, null));
		assertThat(viewName).isEqualTo("finances");
		assertThat(model.getAttribute("entries")).isEqualTo(finances.findEntriesByUser(user));
		assertThat(model.asMap().get("form")).isNotNull();
	}
}

