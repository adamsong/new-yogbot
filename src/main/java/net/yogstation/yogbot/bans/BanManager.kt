package net.yogstation.yogbot.bans

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import net.yogstation.yogbot.config.DiscordConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class BanManager(val client: GatewayDiscordClient, val discordConfig: DiscordConfig) {
	private val logger: Logger = LoggerFactory.getLogger(this::class.java)
	private val tempBans: MutableList<BanRecord> = ArrayList()

	@Scheduled(fixedRate = 15000)
	fun checkBans() {
		val guild: Guild? = client.getGuildById(Snowflake.of(discordConfig.mainGuildID)).block()
		if (guild == null) {
			logger.error("Unable to locate guild, cannot handle unbans")
			return
		}

		val toRemove: MutableList<BanRecord> = ArrayList()
		tempBans.stream().filter(BanRecord::hasExpired).forEach { banRecord ->
			guild.getMemberById(banRecord.snowflake)
				.flatMap { member -> member.removeRole(Snowflake.of(discordConfig.softBanRole), "Softban Expired") }
				.block()
			toRemove.add(banRecord)
		}
		tempBans.removeAll(toRemove)
	}

	fun ban(member: Member, reason: String, duration: Int, author: String): Mono<*> {
		tempBans.removeAll(
			tempBans.stream().filter { banRecord -> banRecord.snowflake == member.id }.toList())

		val banMessage: StringBuilder = StringBuilder("You have been banned from ")
		banMessage.append(discordConfig.serverName)
		banMessage.append(" `")
		banMessage.append(reason)
		banMessage.append("` It will ")
		if (duration > 0) {
			banMessage.append("expire in ")
			banMessage.append(duration)
			banMessage.append(" minutes.")
			tempBans.add(BanRecord(member.id, LocalDateTime.now().plusMinutes(duration.toLong())))
		} else {
			banMessage.append("not expire.")
		}

		return member.addRole(Snowflake.of(discordConfig.softBanRole),
			"${if (duration <= 0) "Permanent" else "Temporary"} softban by $author for ${reason.trim()}")
			.and(member.privateChannel
				.flatMap { privateChannel -> privateChannel.createMessage(banMessage.toString()) })
	}

	fun unban(member: Member, reason: String, user: String): Mono<*> {
		tempBans.removeAll(
			tempBans.stream().filter { banRecord -> banRecord.snowflake == member.id }.toList())
		return member.removeRole(Snowflake.of(discordConfig.softBanRole),
			String.format("Unbanned by %s for %s", user, reason))
	}

	data class BanRecord(val snowflake: Snowflake, val expiration: LocalDateTime) {
		fun hasExpired(): Boolean {
			return expiration.isBefore(LocalDateTime.now())
		}
	}
}
