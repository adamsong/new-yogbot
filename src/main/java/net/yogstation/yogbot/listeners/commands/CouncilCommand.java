package net.yogstation.yogbot.listeners.commands;

import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class CouncilCommand extends ImageCommand {
	public CouncilCommand(DiscordConfig discordConfig, Random random) {
		super(discordConfig, random);
	}
	
	@Override
	protected List<String> getImages() {
		return Arrays.asList(
			"https://cdn.discordapp.com/attachments/134720091576205312/871910842164183070/8PQmYdL.png",
			"https://cdn.discordapp.com/attachments/734475284446707753/826240137225830400/image0.png", //Ashcorr gets fooled by Morderhel
			"https://cdn.discordapp.com/attachments/734475284446707753/804697809323556864/unknown.png", //Get a sense of humor
			"https://cdn.discordapp.com/attachments/734475284446707753/804192496322084864/ban.png", //Banned by public vote
			"https://cdn.discordapp.com/attachments/734475284446707753/800864882870059028/image0.png" //Council don't know hotkeys
		);
	}

	@Override
	protected String getTitle() {
		return "Council Image";
	}

	@Override
	public String getName() {
		return "council";
	}

	@Override
	protected String getDescription() {
		return "Pictures of the goings-on at the top of the hierarchy";
	}
}
