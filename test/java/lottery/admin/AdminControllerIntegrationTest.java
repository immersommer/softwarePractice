package lottery.admin;

import lottery.user.UserManagement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import lottery.user.User;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminControllerIntegrationTest {

	@Resource
	private AdminController adminController;

	@Autowired
	UserManagement userManagement;

	User user;

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testToAdmin() {
		String viewHome = adminController.admin();
		assertThat(viewHome).isEqualTo("admin");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testToInfo() {
		Model model = new ExtendedModelMap();
		user = userManagement.findByUserId(8);
		String viewHome = adminController.info(model, user.getId());
		assertThat(viewHome).isEqualTo("admin_userDetails");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testToAllUsers(){
		Model model = new ExtendedModelMap();
		String viewHome = adminController.allUsers(model);
		assertThat(viewHome).isEqualTo("admin_allUsers");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testToAllCommunities(){
		Model model = new ExtendedModelMap();
		String viewHome = adminController.allCommunities(model);
		assertThat(viewHome).isEqualTo("admin_allCommunities");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testToCommunityInfo() {
		Model model = new ExtendedModelMap();
		String viewHome = adminController.communityInfo(model,"gruppe");
		assertThat(viewHome).isEqualTo("admin_communityDetails");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testToAllBets(){
		Model model = new ExtendedModelMap();
		String viewHome = adminController.allBets(model);
		assertThat(viewHome).isEqualTo("admin_allBets");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testToAllData(){
		Model model = new ExtendedModelMap();
		String viewHome = adminController.allData(model);
		assertThat(viewHome).isEqualTo("admin_evaluateBet");
	}

	@Test
	@WithMockUser(username="boss",roles="BOSS")
	void testToFinanceSituation(){
		Model model = new ExtendedModelMap();
		String viewHome = adminController.financeSituation(model);
		assertThat(viewHome).isEqualTo("admin_finance");
	}
}