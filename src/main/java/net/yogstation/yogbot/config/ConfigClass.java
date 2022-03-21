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
public abstract class ConfigClass {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	protected void loadConfig(String configPath) {
		
		// Load the overrides from the config file
		File configFile = new File("config/" + configPath);
		if(!configFile.exists()) return; // No config file, use defaults

		Properties props = new Properties();
		try {
			InputStream is = new FileInputStream(configFile);
			props.load(is);
			is.close();
		} catch (FileNotFoundException e) {
			LOGGER.error("Failed to open {}.", configPath, e);
			return;
		} catch (IOException e) {
			LOGGER.error("Failed to read properties from {}.", configPath, e);
			return;
		}
		
		// Set the properties of the class using reflection
		props.keys().asIterator().forEachRemaining(key -> {
			try {
				Field field = getClass().getDeclaredField(key.toString());
				String propertyValue = props.getProperty(key.toString());
				field.set(this, toObject(field.getType(), propertyValue));
			} catch (NoSuchFieldException e) {
				LOGGER.error("{} has no property {}", getClass().getName(), key, e);
			} catch (IllegalAccessException e) {
				LOGGER.error("Unable to modify {}::{}", getClass().getName(), key, e);
			}
		});
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
