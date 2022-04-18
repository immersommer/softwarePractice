package lottery.community;

import com.mysema.commons.lang.Assert;
import lottery.betting.BettingManagement;
import lottery.betting.bet.CommunityBet;
import lottery.betting.data.Category;
import lottery.finance.FinanceForm;
import lottery.home.message.MessageManagement;
import lottery.user.User;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import static org.salespointframework.core.Currencies.EURO;

@Controller
public class CommunityController {

	private final CommunityManagement management;
	private final BettingManagement bettingManagement;
	private final MessageManagement messageManagement;

	public CommunityController(CommunityManagement management, BettingManagement bettingManagement, MessageManagement messageManagement){

		Assert.notNull(management, "CommunityManagement must not be null!");
		this.management = management;
		this.bettingManagement = bettingManagement;
		this.messageManagement = messageManagement;
	}

	@GetMapping("/community")
	@PreAuthorize("hasRole('USER')")
	public String community(@LoggedIn UserAccount user, Model model) {

		model.addAttribute("personalCommunities", management.findPersonalCommunities(user));
		model.addAttribute("joinableCommunities", management.findJoinableCommunities(user));
		model.addAttribute("alert", messageManagement.newMessages(user.getUsername()));

		return "community";
	}

	@GetMapping("/community/{communityName}")
	@PreAuthorize("hasRole('USER')")
	public String info(@PathVariable("communityName") String communityName, @LoggedIn UserAccount user, Model model) {

		Community community = management.findCommunityByName(communityName);

		model.addAttribute("communityName", communityName);
		model.addAttribute("userName", user.getUsername());
		model.addAttribute("members", community.getUsers());
		Streamable<CommunityBet> lotteryBets = bettingManagement.findBetsByCommunityAndCategory(communityName,
				Category.LOTTERY);
		Streamable<CommunityBet> footballBets = bettingManagement.findBetsByCommunityAndCategory(communityName,
				Category.FOOTBALL);
		model.addAttribute("lotteryBets", lotteryBets);
		model.addAttribute("footballBets", footballBets);

		return "community_info";
	}

	@GetMapping("/community/create")
	@PreAuthorize("hasRole('USER')")
	public String create(Model model, CreateForm form) {
		return "community_create";
	}

	@PostMapping("/community/create")
	public String createNew(@Valid CreateForm form, @LoggedIn UserAccount user, Errors result) {

		//Übergebene Daten werden auf Richtigkeit überprüft
		if(form.getName().contains(" ") || form.getPassword().contains(" ") || result.hasErrors()){
			return "redirect:/community/create?error";
		}

		//Falls der angegebene Community-Name schon vorhanden ist, keine Community erstellen
		String communityName = form.getName();

		Streamable<Community> communities = management.findAll();

		for(Community community : communities){
			if(community.getName().equals(communityName)){
				return "redirect:/community/create?error";
			}
		}

		management.createCommunity(form);
		Community community = management.findCommunityByForm(form);
		management.joinCommunity(community, user);

		return "redirect:/community";
	}

	@GetMapping("/community/join")
	@PreAuthorize("hasRole('USER')")
	public String join(Model model, CreateForm form, String communityName) {
		model.addAttribute("communityName", communityName);
		return "community_join";
	}

	@PostMapping("/community/join")
	public String join(@Valid CreateForm form, @LoggedIn UserAccount user, BindingResult res,Errors result){

		if(result.hasErrors()){
			return "community_join?error";
		}

		Community community= management.findCommunityByForm(form);
		if(community==null){
			ObjectError error = new ObjectError("globalError", "Fehlerhafte Communitydaten.");
			res.addError(error);
			return "community_join";
		}

		management.joinCommunity(community, user);

		return "redirect:/community";
	}
}
