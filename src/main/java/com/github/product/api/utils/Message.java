package com.github.product.api.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Message {
	
	private final MessageSource messageSource;
	
	public String get(String id) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(id, null, locale);
	}
	
	public String get(String id, Object... params) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(id, params, locale);
	}
}
