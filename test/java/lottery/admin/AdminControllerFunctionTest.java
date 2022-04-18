package lottery.admin;

import lottery.community.Community;
import lottery.community.CommunityManagement;
import lottery.user.User;
import lottery.user.UserManagement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ComponentScan(basePackages = {"lottery.community.CommunityManagement", "lottery.user.UserManagement"})
public class AdminControllerFunctionTest {

	@Resource
	private AdminController adminController;

	@Autowired
	UserManagement userManagement;

	@Autowired
	CommunityManagement communityManagement;


	User user;
	Community community;

	@BeforeAll
	void before(){
		user = userManagement.findByUserId(8);
		communityManagement.joinCommunity(communityManagement.findCommunityByName("gruppe"), user.getUserAccount());
		community = communityManagement.findByCommunityName("gruppe");
	}

    @Test
	@WithMockUser(username="boss",roles={"BOSS"})
	void testSearchNotExistedUserId(){
		Model model = new ExtendedModelMap();
		long id = 30;
		String viewHome = adminController.info(model, id);
		assertThat(viewHome).isEqualTo("redirect:/admin/users?error");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testSearchExistedUserId(){
		Model model = new ExtendedModelMap();
		long id = 8;
		String viewHome = adminController.info(model, id);
		assertThat(viewHome).isEqualTo("admin_userDetails");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testSearchNotExistedCommunityName(){
		Model model = new ExtendedModelMap();
		String community1 = "testCommunity";
		String viewHome = adminController.communityInfo(model, community1);
		assertThat(viewHome).isEqualTo("redirect:/admin/communities?error");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testSearchExistedCommunityName(){
		Model model = new ExtendedModelMap();
		String community2 = "gruppe";
		String viewHome = adminController.communityInfo(model, community2);
		assertThat(viewHome).isEqualTo("admin_communityDetails");
	}

	@AfterAll
	void after(){
		communityManagement.removeFromCommunity(community, user.getUserAccount());
	}
}
