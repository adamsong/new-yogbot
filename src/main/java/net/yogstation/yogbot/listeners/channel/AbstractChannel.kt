package net.yogstation.yogbot.listeners.channel

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.message.MessageCreateEvent
import net.yogstation.yogbot.config.DiscordChannelsConfig
import reactor.core.publisher.Mono

/**
 * A class used to listen to a specific channel, handle is called when a message create event is dispatched
 * matching the channel specified in the channel property.
 */
abstract class AbstractChannel(protected val channelsConfig: DiscordChannelsConfig) {
	/**
	 * The ID of the channel to get events for
	 */
	abstract val channel: Snowflake

	/**
	 * Dispatched to handle the event
	 */
	abstract fun handle(event: MessageCreateEvent): Mono<*>
}
