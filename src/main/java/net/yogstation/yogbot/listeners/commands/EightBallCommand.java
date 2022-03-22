package net.yogstation.yogbot.listeners.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Random;

@Component
public class EightBallCommand extends TextCommand {
	private final Random random;
	
	public EightBallCommand(DiscordConfig discordConfig, Random random) {
		super(discordConfig);
		this.random = random;
	}
	
	@Override
	public String getName() {
		return "8ball";
	}

	@Override
	public Mono<?> doCommand(MessageCreateEvent event) {
		final String[] responses = {
			"It is certain.",
			"It is decidedly so.",
			"Without a doubt.",
			"Yes - definitely.",
			"You may rely on it.",
			"As I see it, yes.",
			"Most likely.",
			"Outlook good.",
			"Yes.",
			"Signs point to yes.",
			"Reply hazy, try again.",
			"Ask again later.",
			"Better not tell you now.",
			"Cannot predict now.",
			"Concentrate and ask again.",
			"Don't count on it.",
			"My reply is no.",
			"My sources say no.",
			"Outlook not so good.",
			"Very doubtful."
		};
		return reply(event, responses[random.nextInt(responses.length)]);
	}

	@Override
	protected String getDescription() {
		return "Gaze into the future.";
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}
