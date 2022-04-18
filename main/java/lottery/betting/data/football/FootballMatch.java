package lottery.betting.data.football;

import lottery.betting.data.Category;
import lottery.betting.data.Data;
import org.javamoney.moneta.Money;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class FootballMatch extends Data {

	private String home, guest, shortHome, shortGuest, homeIcon, guestIcon, league;

	protected FootballMatch() { super(); }

	public FootballMatch(String name, Money price, LocalDateTime date, Category category, String home, String guest) {

		super(name, price, date, category);

		this.home = home;
		this.guest = guest;
	}

	public FootballMatch(String name, Money price, LocalDateTime date, Category category, String home, String guest,
						 String shortHome, String shortGuest, String homeIcon, String guestIcon, String league) {

		super(name, price, date, category);

		this.home = home;
		this.guest = guest;
		this.shortHome = shortHome;
		this.shortGuest = shortGuest;
		this.homeIcon = homeIcon;
		this.guestIcon = guestIcon;
		this.league = league;
	}

	public String getHome() {
		return home;
	}

	public String getGuest() {
		return guest;
	}

	public String getShortHome() {
		return shortHome;
	}

	public String getShortGuest() {
		return shortGuest;
	}

	public String getHomeIcon() {
		return homeIcon;
	}

	public String getGuestIcon() {
		return guestIcon;
	}

	public String getLeague() {
		return league;
	}

	@Override
	public String toString(){
		return home +" vs. "+ guest +" am "+ getDate();
	}

	public String toShortString(){
		return home +" vs. "+ guest;
	}
}
