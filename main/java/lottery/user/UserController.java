package lottery.user;

import  javax.validation.Valid;

import lottery.user.partner.PartnerCodeForm;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

	private final UserManagement userManagement;
	private boolean userExists = false;

	UserController(UserManagement userManagement) {

		Assert.notNull(userManagement, "UserManagement must not be null!");

		this.userManagement = userManagement;
	}

	@GetMapping("/")
	public String index() {
		return "landing_page";
	}

	@GetMapping("/register")
	String register(Model model, RegistrationForm form) {
		return "register";
	}

	@PostMapping("/register")
	String registerNew(@Valid RegistrationForm form, BindingResult res, Errors result, Model model) {

		//Übergebene Daten werden auf Richtigkeit überprüft
		if(form.getName().contains(" ") || form.getPartnerCode().contains(" ") || result.hasErrors()){
			ObjectError error = new ObjectError("globalError", "Einige Angaben sind falsch.");
			res.addError(error);
			return "register";
		}

		//Falls der angegebene Nutzername schon vorhanden ist, keinen User erstellen
		String username = form.getName();
		String email = form.getEmailAddress();

		Streamable<User> users = userManagement.findAll();

		if(username.equals("boss")){
			ObjectError error = new ObjectError("globalError", "Sie können sich nicht als Boss registrieren.");
			res.addError(error);
			return "register";
		}

		boolean validPartnerCode = false;
		for(User user : users){
			if(user.getUserAccount().getUsername().equals(username)){
				ObjectError error = new ObjectError("globalError", "Dieser Nutzername ist schon vergeben.");
				res.addError(error);
				return "register";
			}

			if(user.getUserAccount().getEmail().equals(email)){
				ObjectError error = new ObjectError("globalError", "Diese Emailadresse ist schon vergeben.");
				res.addError(error);
				return "register";
			}

			if(user.getPartnerCode().equals(form.getPartnerCode())){
				validPartnerCode = true;
			}
		}

		if(!validPartnerCode){
			FieldError error = new FieldError("partnerCode","partnerCode",
					"Der Partner-Code ist nicht gültig. Bitte korrigieren oder das Feld leerlassen.");
			res.addError(error);
			return "register";
		}

		//Falls keine Fehler bei der Validierung aufgetreten sind, wird ein User mit den angegebenen Daten erstellt
		userManagement.createUser(form);
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/edit-user")
	@PreAuthorize("hasRole('USER')")
	public String editUser(@LoggedIn UserAccount user, Model model, UserEditForm form) {

		model.addAttribute("firstName", user.getFirstname());
		model.addAttribute("lastName", user.getLastname());
		model.addAttribute("emailAddress", user.getEmail());
		model.addAttribute("lotteryAddress", userManagement.findByUserAccount(user).getLotteryAddress());
		model.addAttribute("form", form);
		return "edit_user";
	}

	@PostMapping("/edit_user")
	String editCurrentUser(@Valid @ModelAttribute("form") UserEditForm form, Errors errors,BindingResult res, Model model,
						   @RequestParam(value = "action") String action, @LoggedIn UserAccount user) {


		if(form.getFirstName().contains(" ") || form.getLastName().contains(" ")){
			return editUser(user, model, form);
		}

		String email = form.getEmailAddress();
		boolean changedEmail = !user.getEmail().equals(email);

		Streamable<User> users = userManagement.findAll();

		if(changedEmail){
			for(User otherUser : users) {
				if (otherUser.getUserAccount().getEmail().equals(email) ) {
					ObjectError error = new ObjectError("globalError", "Diese Emailadresse ist schon vergeben.");
					res.addError(error);
					return editUser(user, model, form);
				}
			}
		}

		//Falls das Formular Fehler enthält, keinen User bearbeiten
		if (errors.hasErrors()) {
			return editUser(user, model, form);
		}

		userManagement.editUser(user, form);
		return "redirect:/home";
	}

	@GetMapping(path = "/partner")
	@PreAuthorize("hasRole('USER')")
	public String partner(@LoggedIn UserAccount user, Model model, PartnerCodeForm form) {

		model.addAttribute("partnerCode", userManagement.findByUserAccount(user).getPartnerCode());
		//model.addAttribute("partners", userManagement.findByUserAccount(user).getPartners());
		User partner = userManagement.findByUsername(userManagement.findByUserAccount(user).getPartnerName());
		String partnerName = "";
		if(partner != null) {
			partnerName = partner.getUserAccount().getUsername();
		}

		model.addAttribute("partner", partnerName);
		model.addAttribute("hasPartnerCode", !userManagement.findByUserAccount(user).getPartnerCode().equals(""));
		model.addAttribute("newPartnerCode", "");
		model.addAttribute("form", form);

		return "partner";
	}

	@PostMapping("/partner")
	String createPartnerCode(@Valid @ModelAttribute("form") PartnerCodeForm form, Errors errors, Model model,
							 @RequestParam(value = "action") String action, BindingResult res, @LoggedIn UserAccount user) {

		if(form.getNewPartnerCode().contains(" ") || errors.hasErrors()){
			return "redirect:/partner?error";
		}

		String code = form.getNewPartnerCode();

		Streamable<User> users = userManagement.findAll();

		for(User u : users){
			if(u.getPartnerCode().equals(code)){
				ObjectError error = new ObjectError("globalError", "Dieser Code ist schon vergeben.");
				res.addError(error);
				model.addAttribute("hasPartnerCode", false);
				return "partner";
			}
		}

		userManagement.createPartnerCode(user, form);
		return "redirect:/home";
	}

}
