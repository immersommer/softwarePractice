/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lottery.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;


class UserManagementUnitTest {

	@Test // #93
	void createsUserAccountWhenCreatingAUser() {

		// Given
		// … a CustomerRepository returning customers handed into save(…),
		UserRepository repository = mock(UserRepository.class);
		when(repository.save(any())).then(i -> i.getArgument(0));

		// … a UserAccountManager
		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccountManager.create(any(), any(), any())).thenReturn(userAccount);

		// … and the CustomerManagement using both of them,
		UserManagement userManagement = new UserManagement(repository, userAccountManager);

		// When
		// … a registration form is submitted
		RegistrationForm form = new RegistrationForm("name", "email-address", "lottery-address", "password", "00000");
		User user = userManagement.createUser(form);

		// Then
		// … a user account creation has been triggered with the proper data and role
		verify(userAccountManager, times(1)) //
				.create(eq(form.getName()), //
						eq(UnencryptedPassword.of(form.getPassword())), //
						eq(UserManagement.USER_ROLE));

		// … the customer has a user account attached
		assertThat(user.getUserAccount()).isNotNull();
	}
}