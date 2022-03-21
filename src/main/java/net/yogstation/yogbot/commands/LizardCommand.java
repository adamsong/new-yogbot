package net.yogstation.yogbot.commands;

import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class LizardCommand extends ImageCommand {
	public LizardCommand(DiscordConfig discordConfig, Random random) {
		super(discordConfig, random);
	}
	
	@Override
	protected List<String> getImages() {
		return List.of(
			"http://claycord.com/wp-content/uploads/2015/03/nature.jpg",
			"http://i1.treknature.com/photos/1457/kuscar.jpg",
			"https://c1.staticflickr.com/8/7460/9393543410_dfef909a67_b.jpg",
			"http://www.richard-seaman.com/Wallpaper/Nature/Reptiles/Lizards/AngryPhanomBenchaLizard.jpg",
			"https://s-media-cache-ak0.pinimg.com/originals/17/62/95/176295a832814825bdcd2528a459ed85.jpg",
			"http://www.richard-seaman.com/Wallpaper/Nature/Reptiles/Lizards/AngryFlyingGecko.jpg",
			"https://i.imgur.com/CvX43mP.png",
			"https://i.imgur.com/xoCR0TV.jpg",
			"https://i.imgur.com/VoUe931.jpg",
			"https://i.imgur.com/VfxMpFt.jpg"
		);
	}

	@Override
	protected String getTitle() {
		return "Lizard Image";
	}

	@Override
	protected String getDescription() {
		return "Pictures of lizards";
	}

	@Override
	public String getName() {
		return "lizard";
	}

}
