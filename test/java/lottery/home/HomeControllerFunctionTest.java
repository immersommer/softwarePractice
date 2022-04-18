package lottery.home;

import lottery.community.Community;
import lottery.community.CommunityManagement;
import lottery.home.message.Message;
import lottery.home.message.MessageManagement;
import lottery.user.User;
import lottery.user.UserManagement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HomeControllerFunctionTest {
	@Resource
	HomeController homeController;

	@Autowired
	MessageManagement messageManagement;

	@Autowired
	UserManagement userManagement;

	@Autowired
	CommunityManagement communityManagement;

	User user;

	Message message;
	Message message1;
	Message message2;
	Message message3;
	Message message4;
	Message message5;
	Message message6;
	Message message7;
	Message message8;
	Message message9;

	Community community;

	@BeforeAll
	void before(){
		for(Message message : messageManagement.findAll()){
			messageManagement.deleteMessage(message.getId());
		}
		user = userManagement.findByUsername("testUser");
		message = new Message(user.getUserAccount(), "Mahnung", "Test", LocalDateTime.now());
		message1 = new Message(user.getUserAccount(), "Mahnung1", "Test1", LocalDateTime.now());
		message2 = new Message(user.getUserAccount(), "Mahnung2", "Test2", LocalDateTime.now());
		message3 = new Message(user.getUserAccount(), "Mahnung3", "Test3", LocalDateTime.now());
		message4 = new Message(user.getUserAccount(), "Mahnung4", "Test4", LocalDateTime.now());
		message5 = new Message(user.getUserAccount(), "Mahnung5", "Test5", LocalDateTime.now());
		message6 = new Message(user.getUserAccount(), "Mahnung6", "Test6", LocalDateTime.now());
		message7 = new Message(user.getUserAccount(), "Mahnung7", "Test7", LocalDateTime.now());
		message8 = new Message(user.getUserAccount(), "Mahnung8", "Test8", LocalDateTime.now());
		message9 = new Message(user.getUserAccount(), "Mahnung9", "Test9", LocalDateTime.now());
		messageManagement.save(message2);
		messageManagement.save(message3);
		messageManagement.save(message2);
		messageManagement.save(message3);
		messageManagement.save(message4);
		messageManagement.save(message5);
		messageManagement.save(message6);
		messageManagement.save(message7);
		messageManagement.save(message8);
		messageManagement.save(message9);
		messageManagement.save(message);
		messageManagement.save(message1);

		community = communityManagement.findCommunityByName("gruppe");
		communityManagement.joinCommunity(community, user.getUserAccount());
	}

	@Test
	void testReadMessages(){
		assertThat(communityManagement.findPersonalCommunities(user.getUserAccount())).isNotNull();
		homeController.readMessages(user.getUserAccount());
		assertThat(communityManagement.findPersonalCommunities(user.getUserAccount())).isNullOrEmpty();
	}

	@AfterAll
	void after(){
		for(Message message : messageManagement.findAll()){
			messageManagement.deleteMessage(message.getId());
		}
		communityManagement.removeFromCommunity(community,user.getUserAccount());
	}
}
