package lottery.admin;

import lottery.betting.bet.Bet;
import lottery.betting.BettingManagement;
import lottery.betting.data.Category;
import lottery.betting.data.football.FootballMatch;
import lottery.betting.data.football.Score;
import lottery.betting.data.number.LotteryEntity;
import lottery.betting.data.number.SelectNumber;
import lottery.community.Community;
import lottery.community.CommunityManagement;
import lottery.finance.FinanceForm;
import lottery.finance.FinanceManagement;
import lottery.home.message.Message;
import lottery.home.message.MessageManagement;
import lottery.user.User;
import lottery.user.UserManagement;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class AdminController {

	private final UserManagement userManagement;
	private final FinanceManagement financeManagement;
	private final CommunityManagement communityManagement;
	private final BettingManagement bettingManagement;
	private final MessageManagement messageManagement;


	AdminController(UserManagement userManagement, FinanceManagement financeManagement,
					CommunityManagement communityManagement, BettingManagement bettingManagement,
					MessageManagement messageManagement) {

		this.bettingManagement = bettingManagement;
		this.communityManagement = communityManagement;
		this.financeManagement = financeManagement;
		this.userManagement = userManagement;
		this.messageManagement = messageManagement;
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('BOSS')")
	String admin() {
		return "admin";
	}

	@GetMapping("/admin/users")
	@PreAuthorize("hasRole('BOSS')")
	String allUsers(Model model) {

		model.addAttribute("users", userManagement.findAll());

		return "admin_allUsers";
	}

	@GetMapping("/admin/users/getInfo")
	@PreAuthorize("hasRole('BOSS')")
	String info(Model model, @RequestParam("id") Long id) {
		User user = userManagement.findByUserId(id);
		if (user != null) {
			model.addAttribute("user", user);
			model.addAttribute("communities",
					communityManagement.findPersonalCommunities(user.getUserAccount()));
			model.addAttribute("bets",
					bettingManagement.findBetsByUser(user.getUserAccount().getUsername()));
			return "admin_userDetails";
		}
		return "redirect:/admin/users?error";
	}

	@GetMapping("/admin/communities")
	@PreAuthorize("hasRole('BOSS')")
	String allCommunities(Model model) {

		model.addAttribute("communities", communityManagement.findAll());

		return "admin_allCommunities";
	}

	@GetMapping("/admin/communities/getCommunityInfo")
	@PreAuthorize("hasRole('BOSS')")
	String communityInfo(Model model, @RequestParam("name") String name) {
		Community community = communityManagement.findByCommunityName(name);
		if (community != null) {
			model.addAttribute("communityName", community.getName());
			model.addAttribute("usersInCommunity", community.getUsers());
			return "admin_communityDetails";
		}
		return "redirect:/admin/communities?error";
	}

	@GetMapping("/admin/bets")
	@PreAuthorize("hasRole('BOSS')")
	String allBets(Model model) {
		model.addAttribute("money", bettingManagement.getMoneyFromAllBets());
		model.addAttribute("bets", bettingManagement.findNotEvaluatedBets());

		return "admin_allBets";
	}

	@GetMapping("/admin/evaluateBets")
	@PreAuthorize("hasRole('BOSS')")
	String allData(Model model) {
		model.addAttribute("matches", bettingManagement.findDataByCategory(Category.FOOTBALL));
		model.addAttribute("lotteries", bettingManagement.findDataByCategory(Category.LOTTERY));

		return "admin_evaluateBet";
	}

	@PostMapping(path = "/admin/evaluateBets/football")
	@PreAuthorize("hasRole('BOSS')")
	String setFootball(@RequestParam("match") FootballMatch match,
					   @RequestParam("home_score") int homeScore, @RequestParam("guest_score") int guestScore) {

		if(errorFootballInput(homeScore, guestScore)){
			return "redirect:/admin/evaluateBets?error";
		}

		match.setResult(new Score(homeScore, guestScore));
		return evaluateBet(bettingManagement.findBetsByDataAndEvaluate(match, false), "Wettausschüttung zu " + match);
	}

	@PostMapping(path = "/admin/evaluateBets/lottery")
	@PreAuthorize("hasRole('BOSS')")
	String setLottery(@RequestParam("lottery") LotteryEntity lottery,
					  @RequestParam("numberStr") String numStr, @RequestParam("superNumber") int superNumber) {

		if(errorLotteryInput(numStr, superNumber)){
			return "redirect:/admin/evaluateBets?error";
		}

		lottery.setResult(new SelectNumber(numStr, superNumber));
		return evaluateBet(bettingManagement.findBetsByDataAndEvaluate(lottery, false), "Wettausschüttung zu " + lottery);
	}

	@GetMapping("/admin/finances")
	@PreAuthorize("hasRole('BOSS')")
	String financeSituation(Model model) {
		List<BettingManagement.Tuple> list = bettingManagement.getMoneyForSetData();
		model.addAttribute("finance", bettingManagement.getFinancialSituation(list));
		model.addAttribute("moneySetData", list);

		return "admin_finance";
	}

	private String evaluateBet(Streamable<Bet> betsByData, String description) {
		for (Bet bet : betsByData) {
			Set<Map.Entry<String, Double>> payOut = bet.payOut().entrySet();
			for (Map.Entry<String, Double> entry : payOut) {
				financeManagement.deposit(new FinanceForm(entry.getValue(), description),
						userManagement.findByUsername(entry.getKey()).getUserAccount());
				Message message = new Message(userManagement.findByUsername(entry.getKey()).getUserAccount(),
						"Ihre Wette auf "+bet.getReference().toString()+" ist beendet.",
						"Ihr Gewinn ist: "+ Money.of(entry.getValue(), EURO),
						LocalDateTime.now());
				messageManagement.save(message);
				evaluatePartnerProgram(entry.getKey(), entry.getValue() * 0.01);
			}
		}
		return "redirect:/admin";
	}

	private void evaluatePartnerProgram(String username, Double payOut){
		User user = userManagement.findByUsername(username);
		String partner;
		if(user.getPartnerName().equals("")){
			return;
		} else {
			partner = user.getPartnerName();
		}

		financeManagement.deposit(new FinanceForm(payOut, "Partnerprogrammbonus durch "+username),
				userManagement.findByUsername(partner).getUserAccount());
		Message message = new Message(userManagement.findByUsername(partner).getUserAccount(),
				"Eine Partnerwette ist abgeschlossen",
				"Ihr Gewinn ist: "+ Money.of(payOut, EURO),
				LocalDateTime.now());
		messageManagement.save(message);
	}

	private static boolean errorLotteryInput(String numStr, int superNumber){
		boolean error = false;
		String[] numArr = numStr.split(",");
		Set<Integer> numbers = new HashSet<>();
		for(String s : numArr){
			int number;
			try {
				number = Integer.parseInt(s);
			} catch (NumberFormatException nfe) {
				error = true;
				continue;
			}
			if(number < 1 || number > 49){
				error = true;
				continue;
			}
			numbers.add(number);
		}
		if(numbers.size() != 6){
			error = true;
		}
		if(superNumber < 1 || superNumber > 9){
			error = true;
		}
		return error;
	}

	private static boolean errorFootballInput(int homeScore, int guestScore){
		if(homeScore < 0 || guestScore < 0){
			return true;
		}
		return homeScore > 30 || guestScore > 30;
	}
}