package lottery.user;

import lottery.user.partner.PartnerCodeForm;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@Transactional
public class UserManagement {

	public static final Role USER_ROLE = Role.of("USER");

	private final UserRepository users;
	private final UserAccountManagement userAccounts;

	UserManagement(UserRepository users, UserAccountManagement userAccounts) {

		Assert.notNull(users, "UserRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.users = users;
		this.userAccounts = userAccounts;
	}


	//erstellt einen neuen User mit den übergebenen Daten
	public User createUser(RegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		var password = UnencryptedPassword.of(form.getPassword());
		var emailAddress = form.getEmailAddress();
		var userAccount = userAccounts.create(form.getName(), password, USER_ROLE);
		userAccount.setEmail(emailAddress);

		var lotteryAddress = form.getLotteryAddress();


		String partnerName = "";
		boolean hasFreeBet = false;
		if(!form.getPartnerCode().equals("")) {
			var partner = findByPartnerCode(form.getPartnerCode());

			if (partner != null) {
				partnerName = partner.getUserAccount().getUsername();
				hasFreeBet = true;
			}
		}

		return users.save(new User(userAccount, lotteryAddress, partnerName, hasFreeBet));
	}

	public User editUser(UserAccount user, UserEditForm form) {

		var first_name = form.getFirstName();
		var last_name = form.getLastName();
		var emailAddress = form.getEmailAddress();
		var lotteryAddress = form.getLotteryAddress();

		var userAccount = user;
		userAccount.setEmail(emailAddress);
		userAccount.setFirstname(first_name);
		userAccount.setLastname(last_name);
		findByUserAccount(user).setLotteryAddress(lotteryAddress);

		return findByUserAccount(user);
	}

	public User createPartnerCode(UserAccount user, PartnerCodeForm form){
		String code = form.getNewPartnerCode();

		findByUserAccount(user).setPartnerCode(code);

		return findByUserAccount(user);
	}


	public User findByUserId(long id){
		Optional<User> user = users.findById(id);
		return user.orElse(null);
	}

	public User findByUserAccount(UserAccount userAccount){
		Optional<User> user = users.findByUserAccount(userAccount);
		return user.orElse(null);
	}

	public User findByUsername(String name){
		Optional<User> user = users.findByUsername(name);
		return user.orElse(null);
	}

	public User findByPartnerCode(String code){
		Optional<User> user = users.findByPartnerCode(code);
		return user.orElse(null);
	}

//	public void deleteUser(User user){ users.delete(user); }

	public Streamable<User> findAll() {
		return users.findAll();
	}
}
