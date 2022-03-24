package net.yogstation.yogbot.listeners.commands

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import net.yogstation.yogbot.DatabaseManager
import net.yogstation.yogbot.config.DiscordConfig
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class WhoIsCommand(
	discordConfig: DiscordConfig,
	private val database: DatabaseManager,
	private val client: GatewayDiscordClient
) : TextCommand(
	discordConfig
) {
	override fun doCommand(event: MessageCreateEvent): Mono<*> {
		val target =
			getTarget(event) ?: return reply(event, "Usage: `${discordConfig.commandPrefix}whois <@Username|ckey>")
		val error = target.populate(database)
		if (error != null) return reply(event, error)
		val snowflake: Snowflake = target.snowflake ?: return reply(event, "Error getting discord id")
		return client.getUserById(snowflake)
			.flatMap { user ->
				reply(
					event, "Ckey ${target.ckey} is linked to discord account ${user.username}#${user.discriminator}"
				)
			}
	}

	override val description = "Checks a Discord users ckey if they have their accounts linked."
	override val name = "whois"
}
