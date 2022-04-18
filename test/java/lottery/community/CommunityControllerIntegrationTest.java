package lottery.community;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommunityControllerIntegrationTest {

	@Resource
	private CommunityController communityController;

	@Mock
	UserAccount user;

	@Test
	@WithMockUser(username="user")
	void testToCommunity() {
		Model model = new ExtendedModelMap();
		String viewHome = communityController.community(user, model);
		assertThat(viewHome).isEqualTo("community");
	}

	@Test
	@WithMockUser(username="user")
	void testToCommunityInfo() {
		Model model = new ExtendedModelMap();
		String viewHome = communityController.info("gruppe", user, model);
		assertThat(viewHome).isEqualTo("community_info");
	}

	@Test
	@WithMockUser(username="user")
	void testToCommunityCreate() {
		Model model = new ExtendedModelMap();
		String viewHome = communityController.create(model,new CreateForm("",""));
		assertThat(viewHome).isEqualTo("community_create");
	}

	@Test
	@WithMockUser(username="user")
	void testToCommunityJoin() {
		Model model = new ExtendedModelMap();
		String viewHome = communityController.join(model,new CreateForm("",""),"gruppe");
		assertThat(viewHome).isEqualTo("community_join");
	}
}
