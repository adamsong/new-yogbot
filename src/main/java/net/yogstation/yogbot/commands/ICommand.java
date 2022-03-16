package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import reactor.core.publisher.Mono;

import java.util.Locale;

public interface ICommand<T extends ApplicationCommandInteractionEvent> {
    /**
     * Gets the name of the interaction as sent by discord
     * @return the name
     */
    String getName();

    /**
     * Gets the URI of the json file describing this interaction
     * Default is the name, to lower case, .json
     * @return The URI
     */
    default String getURI() {
        return getName().toLowerCase(Locale.ROOT) + ".json";
    }

    /**
     * This is the method that is actuallhy called when the interaction actually triggers
     * @param event The inciting event
     * @return The Mono with reply information
     */
    Mono<Void> handle(T event);
}
