package net.yogstation.yogbot.commands;

import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class CorgiCommand extends ImageCommand {
	public CorgiCommand(DiscordConfig discordConfig, Random random) {
		super(discordConfig, random);
	}
	
	@Override
	protected List<String> getImages() {
		return Arrays.asList(
			"https://cdn.discordapp.com/attachments/205784753352343552/767106936508121098/k9i7YLN.jpg",
			"https://cdn.discordapp.com/attachments/205784753352343552/767106936775770163/r1isn2kqbiq01.jpg",
			"https://cdn.discordapp.com/attachments/205784753352343552/767106936965169182/y99yj67o3utz.jpg",
			"https://cdn.discordapp.com/attachments/205784753352343552/767106937166233600/kjc24vncs4811.jpg",
			"https://cdn.discordapp.com/attachments/205784753352343552/767106937358647336/cdgz8cwgovez.jpg",
			"https://cdn.discordapp.com/attachments/205784753352343552/767106937803374622/830Bmqj.jpg",
			"https://cdn.discordapp.com/attachments/205784753352343552/767106937988186122/e05qeJD.jpg",
			"https://cdn.discordapp.com/attachments/205784753352343552/767106937569280040/b6l73MX.jpg"
		);
	}

	@Override
	protected String getTitle() {
		return "Random Corgi";
	}

	@Override
	public String getName() {
		return "corgi";
	}

	@Override
	protected String getDescription() {
		return "Pictures of Ian's children";
	}
}
