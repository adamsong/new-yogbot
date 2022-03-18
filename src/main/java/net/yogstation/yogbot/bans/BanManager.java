package net.yogstation.yogbot.bans;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.commands.AddRankCommand;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
				member.removeRole(Snowflake.of(Yogbot.config.discordConfig.softbanRole), "Softban Expired")).block();
			toRemove.add(banRecord);
		});
		tempBans.removeAll(toRemove);
	}

	public Mono<?> ban(Member member, String reason, int duration, String author) {
		if(duration > 0) {
			tempBans.add(new BanRecord(member.getId(), LocalDateTime.now().plusMinutes(duration)));
		}
		return member.addRole(Snowflake.of(Yogbot.config.discordConfig.softbanRole),
			String.format("%ssoftban by %s for %s",
			duration <= 0 ? "Permanent ": "Temporary ", author, reason.trim()));
	}
}

record BanRecord(Snowflake snowflake, LocalDateTime expiration) {
	public boolean hasExpired() {
		return expiration.isBefore(LocalDateTime.now());
	}
}
