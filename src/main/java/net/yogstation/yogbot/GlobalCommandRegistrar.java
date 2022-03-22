package net.yogstation.yogbot;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.listeners.interactions.IInteractionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle the management of discord commands from JSON files
 * Modified to properly handle being packaged into a jar file
 * Original found in the Discord4J examples https://github.com/Discord4J/example-projects/blob/master/gradle-spring-bot/src/main/java/com/novamaday/d4j/gradle/springbot/GlobalCommandRegistrar.java
 */
@Component
public class GlobalCommandRegistrar implements ApplicationRunner {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private final RestClient restClient;
	private final List<IInteractionHandler> interactionHandlers;
	private final DiscordConfig config;
	
	public GlobalCommandRegistrar(RestClient restClient, List<IInteractionHandler> interactionHandlers,
	                              DiscordConfig config) {
		this.restClient = restClient;
		this.interactionHandlers = interactionHandlers;
		this.config = config;
	}
	
	/* The two below methods are boilerplate that can be completely removed when using Spring Boot */
	
	/**
	 * Gets a specific resource file as String
	 *
	 * @param fileName The file path omitting "resources/"
	 * @return The contents of the file as a String, otherwise throws an exception
	 */
	private static String getResourceFileAsString(String fileName) throws IOException {
		try (InputStream resourceAsStream = new ClassPathResource(fileName).getInputStream()) {
			try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
			     BufferedReader reader = new BufferedReader(inputStreamReader)) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
	}
	
	//Since this will only run once on startup, blocking is okay.
	@Override
	public void run(ApplicationArguments args) throws IOException {
		List<String> commandsJson = new ArrayList<>();
		//The name of the folder the commands json is in, inside our resources folder
		final String commandsFolderName = "commands/";
		
		for (IInteractionHandler interactionHandler : interactionHandlers) {
			String resourceFileAsString = getResourceFileAsString(commandsFolderName + interactionHandler.getURI());
			if(resourceFileAsString == null) {
				LOGGER.error("Failed to load command resource {}", interactionHandler.getURI());
				continue;
			}
			commandsJson.add(resourceFileAsString);
		}
		
		//Create an ObjectMapper that supports Discord4J classes
		final JacksonResources d4jMapper = JacksonResources.create();
		
		// Convenience variables for the sake of easier to read code below
		final ApplicationService applicationService = restClient.getApplicationService();
		final Long applicationId = restClient.getApplicationId().block();
		if(applicationId == null) {
			LOGGER.error("Unable to register slash commands, application ID not found.");
			return;
		}
		
		//Get our commands json from resources as command data
		List<ApplicationCommandRequest> commands = new ArrayList<>();
		for (String json : commandsJson) {
			ApplicationCommandRequest request = d4jMapper.getObjectMapper()
				.readValue(json, ApplicationCommandRequest.class);
			
			commands.add(request); //Add to our array list
		}
		
		if(config.useLocalCommands) {
			LOGGER.info("Loading guild commands");
			applicationService.bulkOverwriteGuildApplicationCommand(applicationId, config.mainGuildID, commands)
					.doOnNext(ignore -> LOGGER.debug("Successfully registered Global Commands"))
					.doOnError(e -> LOGGER.error("Failed to register global commands", e))
					.subscribe();
			return;
		}
        /* Bulk overwrite commands. This is now idempotent, so it is safe to use this even when only 1 command
        is changed/added/removed
        */
		applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
			.doOnNext(ignore -> LOGGER.debug("Successfully registered Global Commands"))
			.doOnError(e -> LOGGER.error("Failed to register global commands", e))
			.subscribe();
	}
}
