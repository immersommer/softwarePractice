package lottery.betting.bet;

import lottery.betting.data.Data;
import lottery.betting.data.Result;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.util.Map;

@Entity
public abstract class Bet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String origin;

	@OneToOne(cascade = CascadeType.ALL)
	private Data reference;

	private Result value;

	private Type type;

	private boolean evaluate = false;

	@SuppressWarnings("unused")
	protected Bet() {}

	public Bet(Data reference, Result value, Type type, UserAccount user){
		this.reference = reference;
		this.value = value;
		this.type = type;
		this.origin = user.getUsername();
	}

	public Bet(Data reference, Result value, Type type, String community){
		this.reference = reference;
		this.value = value;
		this.type = type;
		this.origin = community;
	}

	public long getId() {
		return id;
	}

	public String getOrigin() { return origin; }

	public Data getReference() { return reference; }

	public Result getValue() { return value; }

	public void setValue(Result value) {
		this.value = value;
	}

	public abstract Money getTotalBettingAmount();

	public abstract Money setBettingAmount(UserAccount user, Money amount);

	public boolean isEvaluated() {
		return evaluate;
	}

	public void evaluate() {
		evaluate = true;
	}

	public abstract Map<String, Double> payOut();
}

