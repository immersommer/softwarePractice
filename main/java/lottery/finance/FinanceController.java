package lottery.finance;

import javax.validation.Valid;

import lottery.home.message.MessageManagement;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FinanceController {

	private final FinanceManagement management;
	private final MessageManagement messageManagement;

	FinanceController(FinanceManagement management, MessageManagement messageManagement) {
		this.management = management;
		this.messageManagement = messageManagement;
	}

	@GetMapping(path = "/finances")
	@PreAuthorize("hasRole('USER')")
	String finances(@LoggedIn UserAccount user, Model model, FinanceForm form) {

		model.addAttribute("entries", management.findEntriesByUser(user));
		model.addAttribute("balance", management.getUserBalance(user));
		model.addAttribute("alert", messageManagement.newMessages(user.getUsername()));
		model.addAttribute("form", form);

		return "finances";
	}

	@PostMapping(path = "/finances")
	String depositOrWithdraw(@Valid @ModelAttribute("form") FinanceForm form, Errors errors, Model model,
							  @RequestParam(value = "action") String action, @LoggedIn UserAccount user) {

		if (errors.hasErrors() ) {
			return finances(user, model, form);
		}

		if (action.equals("deposit")) {
			management.deposit(form, user);
		} else {
			if (action.equals("withdraw") && !management.withdraw(form, user)) {
				return "redirect:/finances?error";
			}
		}

		return "redirect:/finances";
	}
}

