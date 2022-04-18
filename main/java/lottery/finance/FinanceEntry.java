package lottery.finance;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.salespointframework.core.Currencies.EURO;

@Entity
public class FinanceEntry {

	private String user;

	@Id
	@GeneratedValue
	private long id;

	private Money amount;
	private String note;
	private LocalDateTime date;
	private static DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

	@SuppressWarnings("unused")
	protected FinanceEntry() {}

	public FinanceEntry(UserAccount user, Number amount, String note, LocalDateTime date){
		Assert.notNull(user, "user must not be null!");
		Assert.notNull(amount, "amount must not be null!");
		this.user = user.getUsername();
		this.amount = Money.of(amount, EURO);
		this.note = note;
		this.date = date;
	}

	public long getId(){
		return  id;
	}

	public Money getAmount(){
		return amount;
	}

	public String getUser(){
		return user;
	}

	public String getNote(){
		return note;
	}

	public String getDate() { return date.format(formatDateTime); }

	@Override
	public String toString(){
		return note;
	}

}
