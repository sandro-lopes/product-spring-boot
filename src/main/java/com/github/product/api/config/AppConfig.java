package com.github.product.api.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class AppConfig {

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().setDefaultPropertyInclusion(Include.NON_NULL)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).findAndRegisterModules();
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
}
