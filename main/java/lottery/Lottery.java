package lottery;

import org.salespointframework.EnableSalespoint;
import org.salespointframework.SalespointSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableSalespoint
@SpringBootApplication
public class Lottery {

	private static final String LOGIN_ROUTE = "/login";

	public static void main(String[] args) {
		SpringApplication.run(Lottery.class, args);
	}

	@Configuration
	static class LotteryWebConfiguration implements WebMvcConfigurer {

		/**
		 * We configure {@code /login} to be directly routed to the {@code login} template without any controller
		 * interaction.
		 *
		 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
		 */
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController(LOGIN_ROUTE).setViewName("login");
			registry.addViewController("/").setViewName("landing_page");
		}
	}

	@Configuration
	static class WebSecurityConfiguration extends SalespointSecurityConfiguration {

		/**
		 * Disabling Spring Security's CSRF support as we do not implement pre-flight request handling for the sake of
		 * simplicity. Setting up basic security and defining login and logout options.
		 *
		 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.csrf().disable();

			http.authorizeRequests().antMatchers("/**").permitAll().and().//
					formLogin().loginPage(LOGIN_ROUTE).defaultSuccessUrl("/home").loginProcessingUrl(LOGIN_ROUTE).and(). //
					logout().logoutUrl("/logout").logoutSuccessUrl("/");
			http
					.headers()
					.frameOptions().sameOrigin()
					.httpStrictTransportSecurity().disable();
		}
	}

	@Configuration
	static class BeanConfiguration{
		@Bean
		public PasswordEncoder encoder() {
			return new BCryptPasswordEncoder();
		}
	}
}
