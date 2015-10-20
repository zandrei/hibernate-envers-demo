
package org.azafiu.hibernatetest;

import org.h2.server.web.WebServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author andrei.zafiu
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	/**
	 * function used to run the application
	 *
	 * @param args
	 *            application arguments
	 */
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Register bean to be able to access H2 database administration console
	 * with the url http://localhost:8080/console
	 *
	 * @return a {@link ServletRegistrationBean}
	 */
	@Bean
	public ServletRegistrationBean h2servletRegistration() {
		final ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
		registration.addUrlMappings("/console/*");
		return registration;
	}

}
