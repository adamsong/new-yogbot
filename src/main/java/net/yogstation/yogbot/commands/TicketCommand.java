package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TicketCommand extends PermissionsCommand {
	private final DatabaseManager database;
	
	public TicketCommand(DiscordConfig discordConfig, PermissionsManager permissions, DatabaseManager database) {
		super(discordConfig, permissions);
		this.database = database;
	}
	
	@Override
	protected String getRequiredPermissions() {
		return "note";
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		if (args.length < 2) {
			return reply(event, String.format("Usage is `%sticket <help|get>`", discordConfig.commandPrefix));
		}
		return switch (args[1]) {
			case "help" -> ticket_help(event, args);
			case "get" -> get_ticket(event, args);
			default -> reply(event, String.format("Unknown subcommand `%s`", args[1]));
		};
	}
	
	private Mono<?> ticket_help(MessageCreateEvent event, String[] args) {
		if (args.length < 3) {
			return reply(event, String.format(
				"Gets information on a ticket from the database.\n" + "Use `%sticket help <subcommand>` for help on a specific subcommand",
				discordConfig.commandPrefix));
		}
		return args[2].equals("get") ? reply(event, String.format(
			"Gets either the content of a ticket from a specific round, or the list of tickets from that round\n" + "\tUsage: `%sticket get <round_id> [ticket_id]`",
			discordConfig.commandPrefix)) : reply(event, String.format("Unknown subcommand `%s`", args[1]));
	}
	
	private Mono<?> get_ticket(MessageCreateEvent event, String[] args) {
		if(args.length < 3) {
			return reply(event, String.format("Usage: `%s get <round_id> [ticket_id]`", args[0]));
		}
		String round_id = args[2];
		if(args.length < 4) {
			try(Connection connection = database.getConnection();
				PreparedStatement lookupStatement = connection.prepareStatement(String.format(
                    """
					SELECT * FROM (
						SELECT `tickets`.`ticket_id`, `tickets`.`ckey`, `tickets`.`a_ckey`, `interactions`.`text`,
						RANK() OVER (PARTITION BY `tickets`.`ticket_id` ORDER BY `interactions`.`id`) as `rank`
						FROM %s as tickets
						JOIN %s interactions on tickets.id = interactions.ticket_id
						WHERE `tickets`.`round_id` = ?
					) as ticket_list WHERE ticket_list.`rank` = 1;
					""",
					database.prefix("admin_tickets"),
					database.prefix("admin_ticket_interactions")))) {
				
				lookupStatement.setString(1, round_id);
				ResultSet resultSet = lookupStatement.executeQuery();
				
				boolean hasData = false;
				StringBuilder builder = new StringBuilder("Tickets for round ").append(round_id).append(":\n```\n");
				while(resultSet.next()) {
					hasData = true;
					builder.append("#");
					builder.append(resultSet.getString("ticket_id"));
					builder.append(" ");
					builder.append(resultSet.getString("ckey"));
					builder.append(": ");
					builder.append(resultSet.getString("text"));
					builder.append(", ");
					builder.append(resultSet.getString("a_ckey"));
					builder.append("\n");
				}
				resultSet.close();
				if(!hasData) return reply(event, String.format("Failed to get ticket for round %s", round_id));
				builder.append("```");
				return reply(event, builder.toString());
			} catch (SQLException e) {
				LOGGER.error("Failed to get admin tickets", e);
				return reply(event, "Failed to get tickets.");
			}
		}
		
		String ticket_id = args[3];
		
		try(Connection connection = database.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(String.format(
			"""
				SELECT `interactions`.`when`, `interactions`.`user`, `interactions`.`text`
				FROM %s as tickets
				JOIN %s interactions on tickets.id = interactions.ticket_id
				WHERE `tickets`.`round_id` = ? AND `tickets`.`ticket_id` = ?;
			""",
			database.prefix("admin_tickets"),
			database.prefix("admin_ticket_interactions")
			))) {
			
			preparedStatement.setString(1, round_id);
			preparedStatement.setString(2, ticket_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			boolean hasData = false;
			StringBuilder builder = new StringBuilder("Ticket ").append(ticket_id);
			builder.append(" for round ").append(round_id).append("\n```\n");
			
			while(resultSet.next()) {
				hasData = true;
				builder.append(resultSet.getTimestamp("when").toString());
				builder.append(": ").append(resultSet.getString("user"));
				builder.append(": ").append(resultSet.getString("text")).append("\n");
			}
			if(!hasData) return reply(event, String.format("Unable to find ticket %s in round %s", ticket_id, round_id));
			builder.append("```");
			return reply(event, builder.toString());
			
		} catch (SQLException e) {
			LOGGER.error("Error getting ticket", e);
			return reply(event, "Failed to get ticket.");
		}
	}
	
	@Override
	protected String getDescription() {
		return "Gets ticket information.";
	}
	
	@Override
	public String getName() {
		return "ticket";
	}
}
