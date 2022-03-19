package net.yogstation.yogbot.bans;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.Yogbot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

public class BanManager extends TimerTask{
	private static final Logger LOGGER = LoggerFactory.getLogger(BanManager.class);
	final List<BanRecord> tempBans = new ArrayList<>();

	public BanManager() {
		Timer timer = new Timer();
		timer.schedule(this, 0, 15000);
	}

	@Override
	public void run() {
		if(Yogbot.client == null) return;
		Guild guild = Yogbot.client.getGuildById(Snowflake.of(Yogbot.config.discordConfig.mainGuildID)).block();
		if(guild == null) {
			LOGGER.error("Unable to locate guild, cannot handle unbans");
			return;
		}
		List<BanRecord> toRemove = new ArrayList<>();
		tempBans.stream().filter(BanRecord::hasExpired).forEach(banRecord -> {
			guild.getMemberById(banRecord.snowflake()).flatMap(member ->
				member.removeRole(Snowflake.of(Yogbot.config.discordConfig.softBanRole), "Softban Expired")).block();
			toRemove.add(banRecord);
		});
		tempBans.removeAll(toRemove);
	}

	public Mono<?> ban(Member member, String reason, int duration, String author) {
		tempBans.removeAll(tempBans.stream().filter(banRecord -> banRecord.snowflake().equals(member.getId())).toList());

		StringBuilder banMessage = new StringBuilder("You have been banned from ");
		banMessage.append(Yogbot.config.discordConfig.serverName);
		banMessage.append(" `");
		banMessage.append(reason);
		banMessage.append("` It will ");
		if(duration > 0) {
			banMessage.append("expire in ");
			banMessage.append(duration);
			banMessage.append(" minutes.");
			tempBans.add(new BanRecord(member.getId(), LocalDateTime.now().plusMinutes(duration)));
		} else {
			banMessage.append("not expire.");
		}

		return member.addRole(Snowflake.of(Yogbot.config.discordConfig.softBanRole),
			String.format("%ssoftban by %s for %s",
			duration <= 0 ? "Permanent ": "Temporary ", author, reason.trim())).and(
				member.getPrivateChannel().flatMap(privateChannel ->
					privateChannel.createMessage(banMessage.toString()))
		);
	}

	public Mono<?> unban(Snowflake snowflake) {
		tempBans.removeAll(tempBans.stream().filter(banRecord -> banRecord.snowflake().equals(snowflake)).toList());
		return Yogbot.client.getMemberById(Snowflake.of(Yogbot.config.discordConfig.mainGuildID), snowflake).flatMap(member ->
			member.removeRole(Snowflake.of(Yogbot.config.discordConfig.softBanRole))
		);
	}
}

record BanRecord(Snowflake snowflake, LocalDateTime expiration) {
	public boolean hasExpired() {
		return expiration.isBefore(LocalDateTime.now());
	}
}
