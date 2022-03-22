package net.yogstation.yogbot.listeners.commands;

import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class NiclasCommand extends ImageCommand {
	public NiclasCommand(DiscordConfig discordConfig, Random random) {
		super(discordConfig, random);
	}
	
	@Override
	protected List<String> getImages() {
		return List.of(
			"https://i.imgur.com/RA0v3cW.jpg",
			"https://i.imgur.com/ugbj0m1.png",
			"https://i.imgur.com/pmgSHjz.png"
		);
	}

	@Override
	protected String getTitle() {
		return "Niclas Image";
	}

	@Override
	protected String getDescription() {
		return "et-ellerandet et-eller-andet kartoffel";
	}

	@Override
	public String getName() {
		return "niclas";
	}

	@Override
	public boolean isHidden() {
		return super.isHidden();
	}
}
