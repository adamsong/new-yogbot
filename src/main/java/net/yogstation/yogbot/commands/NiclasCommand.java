package net.yogstation.yogbot.commands;

import java.util.List;

public class NiclasCommand extends ImageCommand {
	@Override
	protected List<String> getImages() {
		return List.of(
			"http://i.imgur.com/RA0v3cW.jpg",
			"http://i.imgur.com/ugbj0m1.png",
			"http://i.imgur.com/pmgSHjz.png"
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
		return true;
	}
}
