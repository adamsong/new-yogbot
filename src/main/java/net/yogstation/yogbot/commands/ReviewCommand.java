package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.StringUtils;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReviewCommand extends PermissionsCommand{
	@Override
	protected String getRequiredPermissions() {
		return "note";
	}

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		if(args.length < 2) return reply(event, String.format("Usage is: `%sreview <ckey>`", Yogbot.config.discordConfig.commandPrefix));
		String ckey = StringUtils.ckey_ize(args[1]);

		return event.getMessage().getChannel().flatMap(messageChannel -> {
			new Thread(new ReviewWorker(ckey, messageChannel)).start();
			return Mono.empty();
		});
	}

	@Override
	protected String getDescription() {
		return "Check the ckey inputted for related ckeys.";
	}

	@Override
	public String getName() {
		return "review";
	}
}

class ReviewWorker implements Runnable {

	private final String initialCkey;
	private final MessageChannel channel;

	private final List<String> ckeysQueue = new ArrayList<>();
	private final Map<String, String> ckeysChecked = new HashMap<>();
	public final ArrayList<MessageChannel> messages = new ArrayList<>();

	public ReviewWorker(String initialCkey, MessageChannel channel) {
		this.initialCkey = initialCkey;
		this.channel = channel;
	}

	@Override
	public void run() {
		ckeysQueue.add(initialCkey);
		ckeysChecked.put(initialCkey, "Original Ckey");
		checkBannu(initialCkey);
	}

	private void checkBannu(String victim) {
		try(Connection connection = Yogbot.database.getConnection();
			PreparedStatement bannuStmt = connection.prepareStatement(String.format(
				"SELECT 1 FROM `%s` WHERE ckey = ? AND role IN ('Server') AND unbanned_datetime IS NULL AND (expiration_time IS NULL OR expiration_time > NOW())",
				Yogbot.database.prefix("ban")
			))
		) {
			ResultSet resultSet = bannuStmt.executeQuery();
			if(resultSet.next()) {
				ckeysChecked.put(victim, String.format("%s (BANNED)", ckeysChecked.get(victim)));
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void sendUpdate(boolean complete) {
		List<String> checkedKeys = ckeysChecked.keySet().stream().toList();
		int count = (int) Math.ceil(checkedKeys.size() / 23.0);
		for(int i = 0; i < count; i++) {
			EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder();
			embedBuilder.author(String.format("Account review%s", i != 0 ? " (CONTINUED)" : ""), "", "http://i.imgur.com/GPZgtbe.png");
			for(String ckey : checkedKeys.subList(i*23, Math.min((i+1) * 23, checkedKeys.size()))) {
				embedBuilder.addField(ckey, ckeysChecked.get(ckey), false);
			}
		}
	}
}
