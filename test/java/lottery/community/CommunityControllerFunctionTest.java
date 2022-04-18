package lottery.community;


import lottery.user.User;
import lottery.user.UserManagement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import javax.annotation.Resource;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommunityControllerFunctionTest {

	@Resource
	CommunityController communityController;

	@Autowired
	CommunityManagement communityManagement;

	@Autowired
	UserManagement userManagement;

	User user;

	@Mock
	Errors errors;

	@Mock
	BindingResult res;

	CreateForm testCommunity1;

	CreateForm testCommunity2;

	CreateForm testFalseCommunity;

	@BeforeAll
	void before(){

		user = userManagement.findByUsername("testUser");
		testCommunity1 = new CreateForm("testCommunity1","123");
		testCommunity2 = new CreateForm("testCommunity2","123");
		testFalseCommunity = new CreateForm("testCommunity1","1234");
		communityManagement.createCommunity(testCommunity1);
	}

	@Test
	void testCreateCommunityWithExistName(){

		String viewHome = communityController.createNew(testFalseCommunity,user.getUserAccount(),errors);
		assertThat(viewHome).isEqualTo("redirect:/community/create?error");

	}

	@Test
	void testCreateCommunity(){

		String viewHome = communityController.createNew(testCommunity2,user.getUserAccount(),errors);
		String result = communityManagement.findByCommunityName("testCommunity2").getName();
		int resultUser = communityManagement.findPersonalCommunities(user.getUserAccount()).size();
		assertThat(viewHome).isEqualTo("redirect:/community");

	}
	@Test
	void testJoinCommunityWithFalseData() {

		String viewHome = communityController.join(testFalseCommunity,user.getUserAccount(),res,errors);
		assertThat(viewHome).isEqualTo("community_join");

	}

	@Test
	void testJoinCommunity(){

		String viewHome = communityController.join(testCommunity1,user.getUserAccount(),res,errors);
		int result =communityManagement.findPersonalCommunities(user.getUserAccount()).size();
		assertThat(viewHome).isEqualTo("redirect:/community");

	}

	@AfterAll
	void after(){
		communityManagement.deleteCommunity("testCommunity1");
		communityManagement.deleteCommunity("testCommunity2");
	}


}
