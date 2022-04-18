package lottery;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests bootstrapping the core {@link Lottery} configuration class.
 *
 * @author Oliver Gierke
 * @author Andreas Zaschka
 */
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@Transactional
public abstract class AbstractIntegrationTest {}