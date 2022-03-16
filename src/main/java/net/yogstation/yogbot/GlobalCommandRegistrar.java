package net.yogstation.yogbot;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle the management of discord commands from JSON files
 * Modified to properly handle being packaged into a jar file
 * Original found in the Discord4J examples https://github.com/Discord4J/example-projects/blob/master/gradle-simple-bot/src/main/java/com/novamaday/d4j/gradle/simplebot/GlobalCommandRegistrar.java
 */
public class GlobalCommandRegistrar {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private final RestClient restClient;

	private final List<String> commandsJson = new ArrayList<>();

	public GlobalCommandRegistrar(RestClient restClient) {
		this.restClient = restClient;
	}
	
	protected void addURIs(List<String> uris) throws IOException {
		//The name of the folder the commands json is in, inside our resources folder
		final String commandsFolderName = "commands/";
		
		for (String uri : uris) {
			String resourceFileAsString = getResourceFileAsString(commandsFolderName + uri);
			commandsJson.add(resourceFileAsString);
		}
	}
	
	/* The two below methods are boilerplate that can be completely removed when using Spring Boot */
	
	/**
	 * Gets a specific resource file as String
	 *
	 * @param fileName The file path omitting "resources/"
	 * @return The contents of the file as a String, otherwise throws an exception
	 */
	private static String getResourceFileAsString(String fileName) throws IOException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		try (InputStream resourceAsStream = classLoader.getResourceAsStream(fileName)) {
			if (resourceAsStream == null) return null;
			try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
			     BufferedReader reader = new BufferedReader(inputStreamReader)) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
	}
	
	//Since this will only run once on startup, blocking is okay.
	protected void registerCommands() throws IOException {
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

		LOGGER.info("Sending {} application command requests", commands.size());

        /* Bulk overwrite commands. This is now idempotent, so it is safe to use this even when only 1 command
        is changed/added/removed
        */
		applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
			.doOnNext(ignore -> LOGGER.info("Successfully registered Global Commands"))
			.doOnError(e -> LOGGER.error("Failed to register global commands", e))
			.subscribe();
	}
}
