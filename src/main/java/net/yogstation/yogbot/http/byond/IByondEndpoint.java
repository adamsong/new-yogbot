package net.yogstation.yogbot.http.byond;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import reactor.core.publisher.Mono;

public interface IByondEndpoint {
	String getMethod();
	Mono<HttpEntity<String>> receiveData(JsonNode data);
}
