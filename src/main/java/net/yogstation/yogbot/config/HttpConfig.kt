package net.yogstation.yogbot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "yogbot.http")
class HttpConfig {
	// Public facing address of yogbot
	var publicPath: String = "https://yogbot.yogstation.net/"

}
