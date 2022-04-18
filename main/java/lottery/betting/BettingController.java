package lottery.betting;

import lottery.betting.bet.Bet;
import lottery.betting.bet.CommunityBet;
import lottery.betting.bet.IndividualBet;
import lottery.betting.data.Category;
import lottery.betting.data.Data;
import lottery.betting.data.Result;
import lottery.betting.data.football.*;
import lottery.betting.data.number.SelectNumber;
import lottery.community.CommunityManagement;
import lottery.finance.FinanceForm;
import lottery.finance.FinanceManagement;
import lottery.home.message.Message;
import lottery.home.message.MessageManagement;
import lottery.user.UserManagement;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.salespointframework.core.Currencies.EURO;

@Controller
class BettingController {

	private final BettingManagement management;
	private final CommunityManagement communityManagement;
	private final UserManagement userManagement;
	private final FinanceManagement financeManagement;
	private final MessageManagement messageManagement;

	BettingController(BettingManagement management, CommunityManagement communityManagement,
					  UserManagement userManagement, FinanceManagement financeManagement, MessageManagement messageManagement) {
		this.management = management;
		this.communityManagement = communityManagement;
		this.userManagement = userManagement;
		this.financeManagement = financeManagement;
		this.messageManagement = messageManagement;
	}

	@GetMapping("/betting")
	@PreAuthorize("hasRole('USER')")
	public String betting() {
		return "betting";
	}

	@GetMapping("/betting/number")
	@PreAuthorize("hasRole('USER')")
	public String number(@LoggedIn UserAccount user, Model model) {
		model.addAttribute("personalCommunities", communityManagement.findPersonalCommunities(user));
		try{
			model.addAttribute("hasFreeBet", userManagement.findByUserAccount(user).hasFreeBet());
		} catch (Exception e){
			model.addAttribute("hasFreeBet", false);
		}

		return "betting_number";
	}

	@GetMapping("/betting/football")
	@PreAuthorize("hasRole('USER')")
	public String football(@LoggedIn UserAccount user, Model model) {
		model.addAttribute("matches", management.findDataByCategory(Category.FOOTBALL));
		model.addAttribute("personalCommunities", communityManagement.findPersonalCommunities(user));

		return "betting_football";
	}

	@PostMapping("/betting/football")
	public String addBet(@LoggedIn UserAccount user, @RequestParam("match") FootballMatch match,
				  @RequestParam("home_score") int homeScore, @RequestParam("guest_score") int guestScore,
				  @RequestParam("amount") int amount, @RequestParam("dropCommunity") String community) {

		if(homeScore < 0 || guestScore < 0 || amount < 1){
			return "redirect:/betting/football?error";
		}

		FinanceForm financeForm = new FinanceForm((double)amount, "Wettplatzierung zu "+match.toString());

		if(community.isEmpty()){
			if(financeManagement.withdraw(financeForm, user)){
				management.saveBet(new IndividualBet(match, new Score(homeScore, guestScore),
						user, Money.of(amount, EURO)));
			} else {
				Message message = new Message(user, "2 Euro Bußgeld",
						"Sie haben nicht genügend Geld auf Ihrem Konto für die Wette " + match,
						LocalDateTime.now());
				messageManagement.save(message);
				FinanceForm form = new FinanceForm(2.0, "Mahnung: " + match);
				financeManagement.withdraw(form, user);
				return "redirect:/message";
			}
		} else {
			if(financeManagement.withdraw(financeForm, user)){
				management.saveBet(new CommunityBet(match, new Score(homeScore, guestScore),
						community, user, Money.of(amount, EURO)));
			} else {
				Message message = new Message(user, "2 Euro Bußgeld",
						"Sie haben nicht genügend Geld auf Ihrem Konto für die Wette " + match,
						LocalDateTime.now());
				messageManagement.save(message);
				FinanceForm form = new FinanceForm(2.0, "Mahnung: " + match);
				financeManagement.withdraw(form, user);
				return "redirect:/message";
			}
		}

		return "redirect:/home";
	}

	private String saveIndividualLotteryBet(FinanceForm financeForm, UserAccount user, int amount, Result value){
		if(financeManagement.withdraw(financeForm, user)){
			for(Data lottery : management.findDataByCategory(Category.LOTTERY)){
				if(amount > 0){
					management.saveBet(new IndividualBet(lottery, value, user, Money.of(10, EURO)));
					amount--;
				}
			}
		} else {
			Message message = new Message(user, "2 Euro Bußgeld",
					"Sie haben nicht genügend Geld auf Ihrem Konto für die Wette Lotto 6 aus 49 ("+amount+"x)",
					LocalDateTime.now());
			messageManagement.save(message);
			FinanceForm form = new FinanceForm(2.0, "Mahnung: für Lotto 6 aus 49 ("+amount+"x)");
			financeManagement.withdraw(form, user);
			return "redirect:/message";
		}
		return "redirect:/home";
	}

	private String saveCommunityLotteryBet(FinanceForm financeForm, UserAccount user, int amount,
										   Result value, String community){
		if(financeManagement.withdraw(financeForm, user)){
			for(Data lottery : management.findDataByCategory(Category.LOTTERY)){
				if(amount > 0){
					management.saveBet(new CommunityBet(lottery, value,
							community, user, Money.of(10, EURO)));
					amount--;
				}
			}
		} else {
			Message message = new Message(user, "2 Euro Bußgeld",
					"Sie haben nicht genügend Geld auf Ihrem Konto für die Wette Lotto 6 aus 49 ("+amount+"x)",
					LocalDateTime.now());
			messageManagement.save(message);
			FinanceForm form = new FinanceForm(2.0, "Mahnung: für Lotto 6 aus 49 ("+amount+"x)");
			financeManagement.withdraw(form, user);
			return "redirect:/message";
		}
		return "redirect:/home";
	}

	@PostMapping("/betting/number")
	public String addBet(@LoggedIn UserAccount user, @RequestParam("numStr") String numStr,
				  @RequestParam("superNumber") int superNumber, @RequestParam("amount") String radio,
				  @RequestParam("community") String community) {

		if(superNumber < 1 || superNumber > 9){
			return "redirect:/betting/number?error";
		}

		int amount;
		boolean bonusBet = false;
		switch(radio){
			case "single":
				amount = 1;
				break;
			case "month":
				amount = management.getLotteryAmountForDuration(LocalDateTime.now(Clock.systemUTC()).plusMonths(1));
				break;
			case "half-year":
				amount = management.getLotteryAmountForDuration(LocalDateTime.now(Clock.systemUTC()).plusMonths(6));
				break;
			case "year":
				amount = management.getLotteryAmountForDuration(LocalDateTime.now(Clock.systemUTC()).plusYears(1));
				break;
			case "bonus":
				amount = 1;
				bonusBet = true;
				userManagement.findByUserAccount(user).setFreeBet(false);
				break;
			default:
				return "redirect:/home";
		}

		int realAmount = bonusBet ? 0 : 10 * amount;

		FinanceForm financeForm = new FinanceForm((double)realAmount,
				"Wettplatzierung für Lotto 6 aus 49 ("+amount+"x)");

		String redirect;

		if(community.isEmpty()){
			redirect = saveIndividualLotteryBet(financeForm, user, amount, new SelectNumber(numStr,superNumber));
		} else {
			redirect = saveCommunityLotteryBet(financeForm, user, amount,
					new SelectNumber(numStr,superNumber), community);
		}
		return redirect;
	}

	@GetMapping("/betting/change")
	@PreAuthorize("hasRole('USER')")
	public String changeList(@LoggedIn UserAccount userAccount, Model model) {
		model.addAttribute("betList", management.findBetsByUser(userAccount.getUsername()));

		return "betting_changeList";
	}

	@GetMapping("/betting/change/{bet}")
	@PreAuthorize("hasRole('USER')")
	public String updateView(@PathVariable("bet") long betId, @RequestParam("category") Category category,
							 @RequestParam("reference") Data data, Model model) {
		model.addAttribute("betId", betId);
		if (category == Category.LOTTERY) {
			return "betting_updateLotteryView";
		} else if (category == Category.FOOTBALL) {
			model.addAttribute("betAmount", management.findBetById(betId).getTotalBettingAmount());
			model.addAttribute("match", data);
			return "betting_updateFootballView";
		}
		return "redirect:/home";
	}

	@PostMapping("/community/bet")
	public String setAmountOfCommunityBet(@LoggedIn UserAccount user, @RequestParam("bet") long betID,
										  @RequestParam("amount") int newAmount) {

		if(newAmount < 0){
			return "redirect:/home";
		}

		CommunityBet bet = management.findCommunityBetById(betID);
		double dif = newAmount - bet.getSingleAmount(user.getUsername()).getNumber().doubleValue();

		return setNewAmount(user, bet, newAmount, dif);
	}

	@PostMapping("/betting/updateFootball")
	public String updateFootballBet(@LoggedIn UserAccount user, @RequestParam("betId") long betId,
									@RequestParam("home_score") int homeScore,
									@RequestParam("guest_score") int guestScore, @RequestParam("amount") int newAmount){

		if(homeScore < 0 || guestScore < 0 || newAmount < 1){
			return "redirect:/betting/updateFootball?error";
		}

		Bet bet = management.findBetById(betId);
		bet.setValue(new Score(homeScore, guestScore));
		double dif = newAmount - bet.getTotalBettingAmount().getNumber().doubleValue();

		return setNewAmount(user, bet, newAmount, dif);
	}

	@PostMapping("/betting/updateLottery")
	public String updateLotteryBet(@RequestParam("betId") long betId, @RequestParam("numStr") String numStr,
								   @RequestParam("superNumber") int superNumber) {

		if(superNumber < 1 || superNumber > 9){
			return "redirect:/betting/updateLottery?error";
		}

		Bet bet = management.findBetById(betId);
		bet.setValue(new SelectNumber(numStr, superNumber));

		management.saveBet(bet);

		return "redirect:/home";
	}

	private String setNewAmount(UserAccount user, Bet bet, int newAmount, double difference){

		if(difference > 0){
			FinanceForm financeForm = new FinanceForm(difference,
					"Betragsänderung zu "+bet.getReference().toString());

			if(financeManagement.withdraw(financeForm, user)){
				bet.setBettingAmount(user, Money.of(newAmount, EURO));
			} else {
				Message message = new Message(user, "2 Euro Bußgeld",
						"Sie haben nicht genügend Geld auf Ihrem Konto für die Betragsänderung",
						LocalDateTime.now());
				messageManagement.save(message);
				FinanceForm form = new FinanceForm(2.0, "Mahnung: für Betragsänderung");
				financeManagement.withdraw(form, user);
				return "redirect:/message";
			}
		} else if(difference < 0){
			FinanceForm financeForm = new FinanceForm(-difference,
					"Betragsänderung zu "+bet.getReference().toString());
			financeManagement.deposit(financeForm, user);
			bet.setBettingAmount(user, Money.of(newAmount, EURO));
		}

		management.saveBet(bet);

		return "redirect:/home";
	}
}
