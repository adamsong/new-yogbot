package net.yogstation.yogbot.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class HttpUtil {
	public static <T> Mono<HttpEntity<T>> badRequest(T body) {
		return response(body, HttpStatus.BAD_REQUEST);
	}
	
	public static <T> Mono<HttpEntity<T>> response(T body, HttpStatus status) {
		return Mono.just(ResponseEntity.status(status).body(body));
	}
	
	public static Mono<HttpEntity<String>> ok(String body) {
		return response(body, HttpStatus.OK);
	}
}
