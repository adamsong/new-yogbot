package net.yogstation.yogbot.http

import discord4j.core.GatewayDiscordClient
import net.yogstation.yogbot.util.HttpUtil
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HealthController {
	var discordHasConnected = false

	@GetMapping("/health")
	fun getHealth(): Mono<HttpEntity<String>> {
		return if(discordHasConnected) HttpUtil.ok("Status normal") else HttpUtil.response("Not connected to discord", HttpStatus.SERVICE_UNAVAILABLE)
	}
}
