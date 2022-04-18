package lottery.user.partner;

import javax.validation.constraints.NotEmpty;

public class PartnerCodeForm {

	@NotEmpty(message = "{RegistrationForm.name.NotEmpty}")
	private final String newPartnerCode;

	public PartnerCodeForm(String newPartnerCode) {

		this.newPartnerCode = newPartnerCode;
	}

	public String getNewPartnerCode() {
		return newPartnerCode;
	}

}
