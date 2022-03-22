package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.config.HttpConfig;
import net.yogstation.yogbot.http.VerificationController;
import net.yogstation.yogbot.util.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;

@Component
public class VerifyCommand extends TextCommand {
	private final SecureRandom random = new SecureRandom();
	
	private final VerificationController verificationController;
	private final HttpConfig httpConfig;
	
	public VerifyCommand(DiscordConfig discordConfig, VerificationController verificationController, HttpConfig httpConfig) {
		super(discordConfig);
		this.verificationController = verificationController;
		this.httpConfig = httpConfig;
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		if(args.length < 2)
			return reply(event, "Usage: `%s <ckey>`", args[0]);
		
		String ckey = StringUtils.ckey_ize(String.join("", Arrays.stream(args).toList().subList(1, args.length)));
		byte[] bytes = new byte[8];
		random.nextBytes(bytes);
		String state = StringUtils.bytesToHex(bytes);
		
		Optional<User> author = event.getMessage().getAuthor();
		if(author.isEmpty()) return Mono.empty();
		verificationController.oauthState.put(state, new VerificationController.AuthIdentity(ckey, author.get().getId(), author.get().getAvatarUrl(), author.get().getTag()));
		
		return reply(event, "Click the following link to complete the linking process: %sapi/verify?state=%s", httpConfig.publicPath, state);
	}
	
	@Override
	protected String getDescription() {
		return "Verifies a connection between a Ckey and a Discord User";
	}
	
	@Override
	public String getName() {
		return "verify";
	}
}
