package lottery.finance;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.salespointframework.core.Currencies.EURO;

@Service
@Transactional
public class FinanceManagement {

	private final FinanceRepository entries;

	FinanceManagement(FinanceRepository financeRepository) {

		Assert.notNull(financeRepository, "financeRepository must not be null!");

		this.entries = financeRepository;
	}

	public void deposit(FinanceForm form, UserAccount user){
		FinanceEntry entry = new FinanceEntry(user, form.getAmount(), form.getNote(), form.getDate());
		entries.save(entry);
	}

	public boolean withdraw(FinanceForm form, UserAccount user){
		Money balance = getUserBalance(user);
		Money withdraw = Money.of(form.getAmount(), EURO);
		if (withdraw.isLessThanOrEqualTo(balance)){
			FinanceEntry entry = new FinanceEntry(user, -(form.getAmount()),form.getNote(), form.getDate());
			entries.save(entry);
			return true;
		} else{
			//message here
			return false;
		}
	}

	public Money getUserBalance(UserAccount user){
		Streamable<FinanceEntry> userEntries = entries.findByUser(user.getUsername());
		double balance = 0;
		for(FinanceEntry entry : userEntries){
			balance += entry.getAmount().getNumber().doubleValue();
		}
		return Money.of(balance, EURO);
	}

	public Streamable<FinanceEntry> findEntriesByUser(UserAccount user){
		return entries.findByUser(user.getUsername());
	}

	public Streamable<FinanceEntry> findAll() { return entries.findAll(); }

	public void deleteEntry(Long entryID){ entries.deleteById(entryID); }
}
