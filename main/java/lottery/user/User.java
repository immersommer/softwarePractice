package lottery.user;

import javax.persistence.*;

import org.salespointframework.useraccount.UserAccount;


@Entity
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToOne
	private UserAccount userAccount;

	private String lotteryAddress;

	private String partnerName;

	private String partnerCode;

	private boolean hasFreeBet;

	@SuppressWarnings("unused")
	public User(){}

	public User(UserAccount userAccount, String lotteryAddress, String partnerName, boolean hasFreeBet) {
		this.userAccount = userAccount;
		this.lotteryAddress = lotteryAddress;
		this.partnerName = partnerName;
		this.partnerCode = "";
		this.hasFreeBet = hasFreeBet;
	}

	public long getId() {
		return id;
	}

	public String getLotteryAddress(){ return lotteryAddress; }

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public boolean hasFreeBet() {
		return hasFreeBet;
	}

	public void setFreeBet(boolean freeBet) {
		hasFreeBet = freeBet;
	}

	public void setPartnerCode(String code) {
		partnerCode = code;
	}

	public void setLotteryAddress(String lottery_address){
		lotteryAddress = lottery_address;
	}
}