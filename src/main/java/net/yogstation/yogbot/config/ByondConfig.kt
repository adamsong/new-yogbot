package net.yogstation.yogbot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "yogbot.byond")
class ByondConfig {
	// The comms key for communicating to byond
	var serverKey: String = "no key for you :("

	// The webhook key used by byond to communicate to yogbot
	var serverWebhookKey: String = "webkey"

	// The address of the byond server
	var serverAddress: String = "158.69.120.60"

	// The port of the byond server
	var serverPort: Int = 4133

	// Public facing addressed, send to user to indicate where to join
	var serverJoinAddress: String = "https://yogstation.net/play.php"
}
