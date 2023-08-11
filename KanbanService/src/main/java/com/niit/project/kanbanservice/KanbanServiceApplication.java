package com.niit.project.kanbanservice;

import com.niit.project.kanbanservice.filter.JwtFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class KanbanServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KanbanServiceApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<JwtFilter> filterFilterRegistrationBean() {
		FilterRegistrationBean<JwtFilter> filterFilterRegistration = new FilterRegistrationBean<>();
		filterFilterRegistration.setFilter(new JwtFilter());
		filterFilterRegistration.addUrlPatterns("/kanbanService/user/*");
		filterFilterRegistration.addUrlPatterns("/kanbanService/project/*");
		return filterFilterRegistration;
	}
}
