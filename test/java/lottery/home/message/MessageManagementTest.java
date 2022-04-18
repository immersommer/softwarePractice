package lottery.home.message;

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
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageManagementTest {
	@Resource
	MessageManagement messageManagement;

	@Autowired
	UserManagement userManagement;

	User user;

	Message message;
	Message message1;

	@BeforeAll
	void before(){
		for(Message message : messageManagement.findAll()){
			messageManagement.deleteMessage(message.getId());
		}
		user = userManagement.findByUsername("testUser");
		message = new Message(user.getUserAccount(), "Mahnung", "Test", LocalDateTime.now());
		message1 = new Message(user.getUserAccount(), "Mahnung1", "Test1", LocalDateTime.now());
		messageManagement.save(message);
		messageManagement.save(message1);
	}

	@Test
	void testAllMessage(){
		List<Message> messageList = messageManagement.findAll().toList();
		assertThat(messageList.size()).isEqualTo(2);
	}


	@Test
	void testNotReadMessage(){
		List<Message> messageList = messageManagement.findNotReadByUser(user.getUserAccount().getUsername()).toList();
		assertThat(messageList.size()).isEqualTo(2);
	}

	@Test
	void testReadMessage(){
		List<Message> messageList = messageManagement.findReadByUser(user.getUserAccount().getUsername()).toList();
		assertThat(messageList.isEmpty()).isTrue();
	}

	@Test
	void testAllMessageByUser(){
		List<Message> messageList = messageManagement.findEntriesByUser(user.getUserAccount().getUsername()).toList();
		assertThat(messageList.size()).isEqualTo(2);
	}

	@AfterAll
	void after(){
		for(Message message : messageManagement.findAll()){
			messageManagement.deleteMessage(message.getId());
		}
	}
}
