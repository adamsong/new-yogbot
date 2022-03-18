package net.yogstation.yogbot.commands;

import java.util.Arrays;
import java.util.List;

public class AshCommand extends ImageCommand {
	@Override
	protected List<String> getImages() {
		return Arrays.asList(
			"https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Einstein-formal_portrait-35.jpg/220px-Einstein-formal_portrait-35.jpg",
			"https://www.biography.com/.image/c_fill,cs_srgb,dpr_1.0,g_face,h_300,q_80,w_300/MTE4MDAzNDEwODQwOTQ2MTkw/ada-lovelace-20825279-1-402.jpg"
		);
	}

	@Override
	protected String getTitle() {
		return "Ash Image";
	}

	@Override
	public String getName() {
		return "ash";
	}
}
