package net.yogstation.yogbot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Handles the configuration from a properties file into the specified config class
 * The configuration files override the default config in the specific class
 */
public final class ConfigManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

	public final DiscordConfig discordConfig;
	public final DatabaseConfig databaseConfig;

	public ConfigManager() {
		discordConfig = loadConfig(DiscordConfig.class, "discord.properties");
		databaseConfig = loadConfig(DatabaseConfig.class, "database.properties");
	}

	/**
	 * Takes a given class, instantiates it, then overrides the fields from the given config file
	 * @param clazz The class to load
	 * @param filepath The config to load overrides from
	 * @return The class with applied overrides
	 */
	private <T> T loadConfig(Class<T> clazz, String filepath) {
		T instance;
		try {
			// Attempt to instantiate the class
			instance = clazz.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			LOGGER.error("Failed to initialize {}.", clazz.getName(), e);
			return null;
		}

		// Load the overrides from the config file
		File configFile = new File("config/" + filepath);
		if(!configFile.exists()) return instance; // No config file, use defaults

		Properties props = new Properties();
		try {
			InputStream is = new FileInputStream(configFile);
			props.load(is);
			is.close();
		} catch (FileNotFoundException e) {
			LOGGER.error("Failed to open {}.", filepath, e);
			return instance;
		} catch (IOException e) {
			LOGGER.error("Failed to read properties from {}.", filepath, e);
			return instance;
		}

		// Set the properties of the class using reflection
		props.keys().asIterator().forEachRemaining(key -> {
			try {
				Field field = clazz.getDeclaredField(key.toString());
				String propertyValue = props.getProperty(key.toString());
				field.set(instance, toObject(field.getType(), propertyValue));
			} catch (NoSuchFieldException e) {
				LOGGER.error("{} has no property {}", clazz.getName(), key, e);
			} catch (IllegalAccessException e) {
				LOGGER.error("Unable to modify {}::{}", clazz.getName(), key, e);
			}
		});

		return instance;
	}

	// Turn a string into the integral type associated with the class
	// Used to set the fields properly
	// Taken from https://stackoverflow.com/a/13943623
	private static Object toObject( Class<?> clazz, String value ) {
		if( Boolean.class == clazz || Boolean.TYPE == clazz ) return Boolean.parseBoolean( value );
		if( Byte.class == clazz || Byte.TYPE == clazz ) return Byte.parseByte( value );
		if( Short.class == clazz || Short.TYPE == clazz ) return Short.parseShort( value );
		if( Integer.class == clazz || Integer.TYPE == clazz ) return Integer.parseInt( value );
		if( Long.class == clazz || Long.TYPE == clazz ) return Long.parseLong( value );
		if( Float.class == clazz || Float.TYPE == clazz ) return Float.parseFloat( value );
		if( Double.class == clazz || Double.TYPE == clazz ) return Double.parseDouble( value );
		return value;
	}
}
