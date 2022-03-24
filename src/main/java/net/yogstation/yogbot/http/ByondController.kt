package net.yogstation.yogbot.http

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.yogstation.yogbot.config.ByondConfig
import net.yogstation.yogbot.http.byond.IByondEndpoint
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class ByondController(private val endpoints: List<IByondEndpoint>, private val byondConfig: ByondConfig) {
	@GetMapping("/byond")
	fun processWebhook(@RequestParam(value = "key", required = false) key: String?,
					   @RequestParam(value = "method", required = false) method: String?,
					   @RequestParam(value = "data", required = false) dataString: String?): Mono<HttpEntity<String>> {
		if (key == null || key == "") return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No key provided"))
		if (method == null || method == "") return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No method provided"))
		if (dataString == null || dataString == "") return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No data provided"))
		if (key != byondConfig.serverWebhookKey) return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key"))
		val data: JsonNode = try {
			mapper.readTree(String.format("{%s}", dataString))
		} catch (e: JsonProcessingException) {
			LOGGER.error("Failed to parse json", e)
			return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid json"))
		}
		return Flux.fromIterable(endpoints).filter { endpoint: IByondEndpoint -> endpoint.method == method }.next().flatMap { endpoint: IByondEndpoint -> endpoint.receiveData(data) }
	}

	companion object {
		private val mapper = ObjectMapper()
		private val LOGGER = LoggerFactory.getLogger(ByondController::class.java)
	}
}
