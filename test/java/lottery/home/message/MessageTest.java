package lottery.home.message;

import lottery.user.User;
import lottery.user.UserManagement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageTest {

	@Autowired
	UserManagement userManagement;

	User user;
	private static final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

	@BeforeAll
	void before() {
		user = userManagement.findByUsername("testUser");
	}

	@Test
	void testTopic() {
		Message message = new Message(user.getUserAccount(), "Mahnung", "Test", LocalDateTime.now());
		assertEquals("Mahnung", message.getTopic());
	}

	@Test
	void testDetail() {
		Message message = new Message(user.getUserAccount(), "Mahnung", "Test", LocalDateTime.now());
		assertEquals("Test", message.getDetail());
	}

	@Test
	void testUser() {
		Message message = new Message(user.getUserAccount(), "Mahnung", "Test", LocalDateTime.now());
		assertEquals(user.getUserAccount().getUsername(), message.getUser());
	}

	@Test
	void testDate() {
		Message message = new Message(user.getUserAccount(), "Mahnung", "Test", LocalDateTime.now());
		assertEquals(LocalDateTime.now().format(formatDateTime), message.getDate());
	}

	@Test
	void testRead() {
		Message message = new Message(user.getUserAccount(), "Mahnung", "Test", LocalDateTime.now());
		message.setRead();
		assertThat(message.getRead()).isTrue();
	}
}
