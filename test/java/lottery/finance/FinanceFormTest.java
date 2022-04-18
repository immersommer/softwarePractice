package lottery.finance;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FinanceFormTest {

	@Test
	void testAmount() {
		FinanceForm form = new FinanceForm(100.0, "EUR");
		assertEquals(100.0, form.getAmount());
	}

	@Test
	void testNote() {
		FinanceForm form = new FinanceForm(100.0, "EUR");
		assertEquals("EUR", form.getNote());
	}
}
