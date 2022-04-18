package lottery.betting.bet;

import lottery.betting.data.Data;
import lottery.betting.data.Result;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

import static org.salespointframework.core.Currencies.EURO;

@Entity
public class CommunityBet extends Bet {

	@ElementCollection
	private final Map<String, Double> amountPerUser = new HashMap<>();

	@SuppressWarnings("unused")
	protected CommunityBet() {}

	public CommunityBet(Data reference, Result value, String community, UserAccount user, Money bettingAmount){

		super(reference, value, Type.COMMUNITY, community);

		setBettingAmount(user, bettingAmount);

	}

	public Map<String, Double> getMap() {
		return amountPerUser;
	}

	public Money getSingleAmount(String user) {
		if (amountPerUser.containsKey(user)){
			return Money.of(amountPerUser.get(user), EURO);
		} else {
			return Money.of(0, EURO);
		}
	}

	public Money getTotalBettingAmount() {
		double total = 0;
		for(Double amount : amountPerUser.values()){
			total += amount;
		}
		return Money.of(total, EURO);
	}

	@Override
	public Money setBettingAmount(UserAccount user, Money amount){
		String username = user.getUsername();
		Double difference = amount.getNumber().doubleValue();
		if(amountPerUser.containsKey(username)){
			difference -= amountPerUser.get(username) ;
			amountPerUser.replace(username, amount.getNumber().doubleValue());
		} else {
			amountPerUser.put(user.getUsername(), amount.getNumber().doubleValue());
		}
		return Money.of(difference, EURO);
	}

	public Map<String, Double> payOut(){
		Data reference = getReference();
		Map<String, Double> payOut = new HashMap<>();
		if(reference.isSet()){
			evaluate();
			int factor = getValue().compareTo(reference.getResult());
			for(Map.Entry<String, Double> entry: amountPerUser.entrySet()){
				payOut.put(entry.getKey(), factor * entry.getValue());
			}
			return payOut;
		} else {
			return null;
		}
	}
}
