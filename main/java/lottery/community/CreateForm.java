package lottery.community;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class CreateForm {

	@NotEmpty(message = "{CreateForm.name.NotEmpty}")
	private final String name;

	@NotEmpty(message = "{CreateForm.password.NotEmpty}")
	private final String password;

	public CreateForm(String name, String password) {

		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
}