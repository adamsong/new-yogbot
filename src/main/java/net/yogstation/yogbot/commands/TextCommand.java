package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.PartialMember;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.listeners.IEventHandler;
import net.yogstation.yogbot.util.ByondLinkUtil;
import net.yogstation.yogbot.util.Result;
import net.yogstation.yogbot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class TextCommand implements IEventHandler<MessageCreateEvent> {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Override
	public Mono<?> handle(MessageCreateEvent event) {
		if(!canFire(event)) return doError(event);
		return doCommand(event);
	}

	protected abstract Mono<?> doCommand(MessageCreateEvent event);
	protected abstract String getDescription();

	public String getHelpText() {
		assert Yogbot.config.discordConfig != null;
		return String.format("    `%s%s` - %s", Yogbot.config.discordConfig.commandPrefix, getName(), getDescription());
	}

	public boolean isHidden() {
		return false;
	}

	protected Mono<?> doError(MessageCreateEvent event) {
		return Mono.empty();
	}

	protected boolean canFire(MessageCreateEvent event) {
		return true;
	}

	protected Mono<?> reply(MessageCreateEvent event, String message) {
		return event.getMessage().getChannel().flatMap(channel ->
			channel.createMessage(MessageCreateSpec.builder()
					.messageReference(event.getMessage().getId())
					.content(message).build())
		);
	}

	protected Mono<?> reply(MessageCreateEvent event, EmbedCreateSpec embed) {
		return event.getMessage().getChannel().flatMap(channel ->
				channel.createMessage(MessageCreateSpec.builder()
						.messageReference(event.getMessage().getId())
						.addEmbed(embed).build())
		);
	}

	protected Mono<?> send(MessageCreateEvent event, String message) {
		return event.getMessage().getChannel().flatMap(channel ->
				channel.createMessage(message));
	}

	protected CommandTarget getTarget(MessageCreateEvent event) {
		List<PartialMember> partialMembers = event.getMessage().getMemberMentions();
		if(partialMembers.size() > 0)
			return CommandTarget.of(partialMembers.get(0).getId());

		String[] args = event.getMessage().getContent().split(" ");
		if(args.length < 2) return null;
		return CommandTarget.of(StringUtils.ckey_ize(args[1]));
	}
}

class CommandTarget {
	private Snowflake snowflake;
	private String ckey;

	private CommandTarget(Snowflake snowflake, String ckey) {
		this.snowflake = snowflake;
		this.ckey = ckey;
	}

	public static CommandTarget of(Snowflake snowflake) {
		return new CommandTarget(snowflake, null);
	}

	public static CommandTarget of(String ckey) {
		return new CommandTarget(null, ckey);
	}

	public String populate() {
		if(snowflake == null) {
			Result<Snowflake, String> snowflakeResult = ByondLinkUtil.getMemberID(ckey);
			if(snowflakeResult.hasError()) return snowflakeResult.getError();
			snowflake = snowflakeResult.getValue();
		}

		if(ckey == null) {
			Result<String, String> ckeyResult = ByondLinkUtil.getCkey(snowflake);
			if(ckeyResult.hasError()) return ckeyResult.getError();
			ckey = ckeyResult.getValue();
		}

		return null;
	}

	public Snowflake getSnowflake() {
		return snowflake;
	}

	public String getCkey() {
		return ckey;
	}
}