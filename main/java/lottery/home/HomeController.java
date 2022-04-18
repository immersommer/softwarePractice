package lottery.home;

import lottery.betting.BettingManagement;
import lottery.finance.FinanceManagement;
import lottery.home.message.Message;
import lottery.home.message.MessageManagement;
import lottery.user.UserManagement;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

	private final FinanceManagement financeManagement;
	private final BettingManagement bettingManagement;
	private final MessageManagement messageManagement;
	private final UserManagement userManagement;

	HomeController(FinanceManagement financeManagement, BettingManagement bettingManagement,
				   MessageManagement messageManagement, UserManagement userManagement) {
		this.financeManagement = financeManagement;
		this.bettingManagement = bettingManagement;
		this.messageManagement = messageManagement;
		this.userManagement = userManagement;
	}

	@GetMapping(path = "/home")
	@PreAuthorize("hasAnyRole('USER','BOSS')")
	public String home(@LoggedIn UserAccount user, Model model) {

		Streamable<Role> roles = user.getRoles();
		if(roles.toList().contains(Role.of("BOSS"))){
			return "redirect:/admin";
		}

		model.addAttribute("balance", financeManagement.getUserBalance(user));
		model.addAttribute("bets", bettingManagement.findBetsByUser(user.getUsername()));
		model.addAttribute("username", user.getUsername());
		model.addAttribute("firstname", user.getFirstname());
		model.addAttribute("lastname", user.getLastname());
		model.addAttribute("alert", messageManagement.newMessages(user.getUsername()));
		model.addAttribute("emailAddress", user.getEmail());
		model.addAttribute("lotteryAddress", userManagement.findByUserAccount(user).getLotteryAddress());

		return "home";
	}

	@GetMapping(path = "/message")
	@PreAuthorize("hasRole('USER')")
	public String message(@LoggedIn UserAccount user, Model model ) {

		model.addAttribute("alert", messageManagement.newMessages(user.getUsername()));
		model.addAttribute("newMessages", messageManagement.findNotReadByUser(user.getUsername()));
		model.addAttribute("oldMessages", messageManagement.findReadByUser(user.getUsername()));
		return "message";
	}

	@PostMapping(path = "/message")
	public String readMessages(@LoggedIn UserAccount user) {

		for(Message message : messageManagement.findNotReadByUser(user.getUsername())){
			message.setRead();
			messageManagement.save(message);
		}
		return "redirect:/message";
	}
}
