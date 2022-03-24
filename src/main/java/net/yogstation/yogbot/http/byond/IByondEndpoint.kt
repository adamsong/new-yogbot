package net.yogstation.yogbot.http.byond

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpEntity
import reactor.core.publisher.Mono

interface IByondEndpoint {
	val method: String
	fun receiveData(data: JsonNode): Mono<HttpEntity<String>>
}
