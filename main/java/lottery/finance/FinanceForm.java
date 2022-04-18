package lottery.finance;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;


public class FinanceForm {

	@NotNull(message = "amount cannot be null.")
	@Min(value = 0)
	@Max(value = 100000000)
	public Double amount;

	private String note;

	private LocalDateTime date;

	@SuppressWarnings("unused")
	FinanceForm(){}

	public FinanceForm(Double amount, String note){
		this.amount = amount;
		this.date = LocalDateTime.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(1)));
		this.note = note;
	}

	public Double getAmount(){
		return amount;
	}

	public LocalDateTime getDate() { return date; }

	public String getNote(){
		return note;
	}


}