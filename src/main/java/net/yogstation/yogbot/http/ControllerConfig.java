package net.yogstation.yogbot.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.server.ServerWebInputException;

@ControllerAdvice
public class ControllerConfig {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handle(ServerWebInputException e) {
		LOGGER.warn("Returning HTTP 400 Bad Request", e);
		throw e;
	}
}
