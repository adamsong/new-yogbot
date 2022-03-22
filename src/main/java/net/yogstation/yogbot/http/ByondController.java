package net.yogstation.yogbot.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.yogstation.yogbot.config.ByondConfig;
import net.yogstation.yogbot.http.byond.IByondEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class ByondController {
	
	private final List<IByondEndpoint> endpoints;
	private final ByondConfig byondConfig;
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger LOGGER = LoggerFactory.getLogger(ByondController.class);
	
	public ByondController(List<IByondEndpoint> endpoints, ByondConfig byondConfig) {
		this.endpoints = endpoints;
		this.byondConfig = byondConfig;
	}
	
	@GetMapping("/byond")
	public Mono<HttpEntity<String>> processWebhook(@RequestParam(value = "key", required = false) String key,
	                                               @RequestParam(value = "method", required = false) String method,
	                                               @RequestParam(value = "data", required = false) String dataString) {
		
		if(key == null || key.equals("")) return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No key provided"));
		if(method == null || method.equals("")) return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No method provided"));
		if(dataString == null || dataString.equals("")) return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No data provided"));
		
		if(!key.equals(byondConfig.serverWebhookKey)) return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key"));
		
		JsonNode data;
		try {
			data = mapper.readTree(String.format("{%s}", dataString));
		} catch (JsonProcessingException e) {
			LOGGER.error("Failed to parse json", e);
			return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid json"));
		}
		
		return Flux.fromIterable(endpoints).filter(endpoint -> endpoint.getMethod().equals(method)).next().flatMap(
			endpoint -> endpoint.receiveData(data));
	}
}
