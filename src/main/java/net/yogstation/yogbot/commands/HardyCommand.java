package net.yogstation.yogbot.commands;

import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class HardyCommand extends ImageCommand {
	public HardyCommand(DiscordConfig discordConfig, Random random) {
		super(discordConfig, random);
	}
	
	@Override
	protected List<String> getImages() {
		return Arrays.asList(
			"https://www.seanconway.com/uploads/1/3/2/4/13241475/3193657_orig.jpg",
			"https://ilovefancydress.com/image/cache/data/7/Penguin%20Fat%20Suit%20Costume-900x900.jpg"
		);
	}

	@Override
	protected String getTitle() {
		return "Hardy Image";
	}

	@Override
	public String getName() {
		return "hardy";
	}

	@Override
	protected String getDescription() {
		return "Pictures of our overlord";
	}
}
