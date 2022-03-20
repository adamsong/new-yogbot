package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReviewCommand extends PermissionsCommand{
	@Override
	protected String getRequiredPermissions() {
		return "note";
	}

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		if(args.length < 2) return reply(event, "Usage is: `%sreview <ckey> [strict]`", Yogbot.config.discordConfig.commandPrefix);
		String ckey = StringUtils.ckey_ize(args[1]);
		boolean strict = (event.getMessage().getChannelId().asLong() == Yogbot.config.channelsConfig.channelAdmin)
			|| (args.length >= 3 && args[2].equalsIgnoreCase("strict"));

		return event.getMessage().getChannel().flatMap(messageChannel -> {
			new Thread(new ReviewWorker(ckey, messageChannel, strict)).start();
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
	
	static class ReviewWorker implements Runnable {
		private static final Logger LOGGER = LoggerFactory.getLogger(ReviewWorker.class);
		
		private final String initialCkey;
		private final MessageChannel channel;
		
		private final List<String> ckeysQueue = new ArrayList<>();
		private final Map<String, String> ckeysChecked = new LinkedHashMap<>();
		public final ArrayList<Message> messages = new ArrayList<>();
		
		public final LocalDateTime startTime;
		private final boolean strict;
		private int update_idx = 0;
		
		public ReviewWorker(String initialCkey, MessageChannel channel, boolean strict) {
			this.initialCkey = initialCkey;
			this.channel = channel;
			this.strict = strict;
			startTime = LocalDateTime.now();
		}
		
		@Override
		public void run() {
			ckeysQueue.add(initialCkey);
			ckeysChecked.put(initialCkey, "Original Ckey");
			checkBannu(initialCkey);
			sendUpdate(false);
			int limiter = strict ? 2 : 30;
			while(ckeysQueue.size() > 0) {
				limiter--;
				if(limiter <= 0) break;
				
				String thisCkey = ckeysQueue.remove(0);
				Set<String> thisCids = new HashSet<>();
				Set<String> thisIps = new HashSet<>();
				Map<String, RelatedInfo> relatedKeys = new HashMap<>();
				String tableName = Yogbot.database.prefix("connection_log");
				try(Connection connection = Yogbot.database.getConnection();
				PreparedStatement connectionsStmt = connection.prepareStatement(String.format("SELECT computerid, ip FROM %s WHERE ckey = ?;", tableName));
				PreparedStatement relatedStmt = connection.prepareStatement(String.format("SELECT ckey,ip,computerid FROM `%s` WHERE computerid IN (SELECT computerid FROM `%s` WHERE ckey = ?) OR ip IN (SELECT ip FROM `%s` WHERE ckey = ?)", tableName, tableName, tableName))
				) {
					connectionsStmt.setString(1, thisCkey);
					ResultSet connectionsResult = connectionsStmt.executeQuery();
					while(connectionsResult.next()) {
						thisCids.add(connectionsResult.getString("computerid"));
						thisIps.add(connectionsResult.getString("ip"));
					}
					connectionsResult.close();
					
					relatedStmt.setString(1, thisCkey);
					relatedStmt.setString(2, thisCkey);
					ResultSet relatedResult = relatedStmt.executeQuery();
					while(relatedResult.next()) {
						String relatedCkey = relatedResult.getString("ckey");
						String relatedIp = relatedResult.getString("ip");
						String relatedCid = relatedResult.getString("computerid");
						if(ckeysChecked.containsKey(relatedCkey)) continue;
						if(!relatedKeys.containsKey(relatedCkey)) {
							relatedKeys.put(relatedCkey, new RelatedInfo());
						}
						RelatedInfo relatedInfo = relatedKeys.get(relatedCkey);
						if(thisIps.contains(relatedIp))
							relatedInfo.ips.add(relatedIp);
						if(thisCids.contains(relatedCid))
							relatedInfo.cids.add(relatedCid);
					}
				} catch (SQLException e) {
					LOGGER.error("Error getting connections", e);
					channel.createMessage("An error has occurred");
					return;
				}
				for(String relatedKey : relatedKeys.keySet()) {
					RelatedInfo info = relatedKeys.get(relatedKey);
					StringBuilder builder = new StringBuilder("Related to ").append(thisCkey).append(" via ");
					if(info.cids.size() > 0){
						builder.append("cid");
						if(info.cids.size() != 1)
							builder.append("s");
						builder.append(" ");
						builder.append(String.join(", ", info.cids));
					}
					if(info.cids.size() > 0 && info.ips.size() > 0)
						builder.append(" ");
					if(info.ips.size() > 0){
						builder.append("ip");
						if(info.ips.size() != 1)
							builder.append("s");
						builder.append(" ");
						builder.append(String.join(", ", info.ips));
					}
					ckeysChecked.put(relatedKey, builder.toString());
					ckeysQueue.add(relatedKey);
					checkBannu(relatedKey);
				}
				sendUpdate(false);
			}
			sendUpdate(true);
		}
		
		private void checkBannu(String victim) {
			try(Connection connection = Yogbot.database.getConnection();
			    PreparedStatement bannuStmt = connection.prepareStatement(String.format(
				    "SELECT 1 FROM `%s` WHERE ckey = ? AND role IN ('Server') AND unbanned_datetime IS NULL AND (expiration_time IS NULL OR expiration_time > NOW())",
				    Yogbot.database.prefix("ban")
			    ))
			) {
				bannuStmt.setString(1, victim);
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
				embedBuilder.author(String.format("Account review%s", i != 0 ? " (CONTINUED)" : ""), "", "https://i.imgur.com/GPZgtbe.png");
				for(String ckey : checkedKeys.subList(i*23, Math.min((i+1) * 23, checkedKeys.size()))) {
					embedBuilder.addField(ckey, ckeysChecked.get(ckey), false);
				}
				if(i < (count - 1)) {
					embedBuilder.addField("CONTINUED", "IN NEXT EMBED", false);
				} else if(complete) {
					embedBuilder.addField("*Done!*", String.format("Took %s seconds", startTime.until(LocalDateTime.now(), ChronoUnit.SECONDS)), false);
				} else {
					embedBuilder.addField("WORKING...", new String[]{"-", "\\", "|", "/"}[update_idx++ % 4], false);
				}
				if(messages.size() > i)
					messages.get(i).edit(MessageEditSpec.builder().addEmbed(embedBuilder.build()).build()).block();
				else
					messages.add(channel.createMessage(embedBuilder.build()).block());
			}
		}
		
		static class RelatedInfo {
			final Set<String> ips = new HashSet<>();
			final Set<String> cids = new HashSet<>();
		}
	}
}
