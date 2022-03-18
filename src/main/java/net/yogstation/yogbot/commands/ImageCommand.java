package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.Embed;
import discord4j.core.spec.EmbedCreateSpec;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.util.List;

public abstract class ImageCommand extends TextCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		List<String> urls = getImages();
		String url = urls.get(Yogbot.random.nextInt(urls.size()));
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
			.color(discord4j.rest.util.Color.of(Yogbot.random.nextInt(0xFFFFFF)))
			.title(getTitle())
			.image(url)
			.footer(url, "")
			.build();
		return reply(event, embed);
	}

	protected abstract List<String> getImages();
	protected abstract String getTitle();
}
