package lottery.community;


import lottery.community.Community;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommunityEntryTest {

	@Test
	void testRejectsEmptyName(){

		assertThatExceptionOfType(IllegalArgumentException.class)//
				.isThrownBy(() -> new Community(null, "password"));
	}

	@Test
	void testRejectsEmptyPassword(){

		assertThatExceptionOfType(IllegalArgumentException.class)//
				.isThrownBy(() -> new Community("name",null));
	}
}
