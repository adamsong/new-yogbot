package net.yogstation.yogbot.commands;

import java.util.List;

public class KMCCommand extends ImageCommand{
	@Override
	protected List<String> getImages() {
		return List.of(
			"https://cdn.discordapp.com/attachments/300800158260658177/313801305271566337/images26.jpg",
			"https://cdn.discordapp.com/attachments/300800158260658177/313801304520523776/images5.png",
			"https://cdn.discordapp.com/attachments/300800158260658177/313801304449351680/images6.png",
			"https://cdn.discordapp.com/attachments/300800158260658177/313801383059128322/yeezy-boost-350-v2-hbx-restock-1.jpg",
			"https://cdn.discordapp.com/attachments/300800158260658177/313801382568132611/adidas-Yeezy-Boost-350-v2-.jpg"
		);
	}

	@Override
	protected String getTitle() {
		return "KMC Image";
	}

	@Override
	protected String getDescription() {
		return "Pictures of event organizers";
	}

	@Override
	public String getName() {
		return "kmc";
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}
