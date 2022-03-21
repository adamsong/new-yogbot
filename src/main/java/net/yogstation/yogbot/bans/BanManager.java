package net.yogstation.yogbot.bans;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.config.DiscordConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BanManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(BanManager.class);
	final List<BanRecord> tempBans = new ArrayList<>();
	
	private final GatewayDiscordClient client;
	private final DiscordConfig discordConfig;
	
	public BanManager(GatewayDiscordClient client, DiscordConfig discordConfig) {
		this.client = client;
		this.discordConfig = discordConfig;
	}
	
	@Scheduled(fixedRate = 15000)
	public void checkBans() {
		Guild guild = client.getGuildById(Snowflake.of(discordConfig.mainGuildID)).block();
		if (guild == null) {
			LOGGER.error("Unable to locate guild, cannot handle unbans");
			return;
		}
		List<BanRecord> toRemove = new ArrayList<>();
		tempBans.stream().filter(BanRecord::hasExpired).forEach(banRecord -> {
			guild.getMemberById(banRecord.snowflake())
				.flatMap(member -> member.removeRole(Snowflake.of(discordConfig.softBanRole), "Softban Expired"))
				.block();
			toRemove.add(banRecord);
		});
		tempBans.removeAll(toRemove);
	}
	
	public Mono<?> ban(Member member, String reason, int duration, String author) {
		tempBans.removeAll(
			tempBans.stream().filter(banRecord -> banRecord.snowflake().equals(member.getId())).toList());
		
		StringBuilder banMessage = new StringBuilder("You have been banned from ");
		banMessage.append(discordConfig.serverName);
		banMessage.append(" `");
		banMessage.append(reason);
		banMessage.append("` It will ");
		if (duration > 0) {
			banMessage.append("expire in ");
			banMessage.append(duration);
			banMessage.append(" minutes.");
			tempBans.add(new BanRecord(member.getId(), LocalDateTime.now().plusMinutes(duration)));
		} else {
			banMessage.append("not expire.");
		}
		
		return member.addRole(Snowflake.of(discordConfig.softBanRole),
		                      String.format("%ssoftban by %s for %s", duration <= 0 ? "Permanent " : "Temporary ",
		                                    author, reason.trim()))
			.and(member.getPrivateChannel()
				     .flatMap(privateChannel -> privateChannel.createMessage(banMessage.toString())));
	}
	
	public Mono<?> unban(Snowflake snowflake) {
		tempBans.removeAll(tempBans.stream().filter(banRecord -> banRecord.snowflake().equals(snowflake)).toList());
		return client.getMemberById(Snowflake.of(discordConfig.mainGuildID), snowflake)
			.flatMap(member -> member.removeRole(Snowflake.of(discordConfig.softBanRole)));
	}
	
	public Mono<?> unban(Member member, String reason, String user) {
		tempBans.removeAll(
			tempBans.stream().filter(banRecord -> banRecord.snowflake().equals(member.getId())).toList());
		return member.removeRole(Snowflake.of(discordConfig.softBanRole),
		                         String.format("Unbanned by %s for %s", user, reason));
	}
	
	record BanRecord(Snowflake snowflake, LocalDateTime expiration) {
		public boolean hasExpired() {
			return expiration.isBefore(LocalDateTime.now());
		}
	}
}
