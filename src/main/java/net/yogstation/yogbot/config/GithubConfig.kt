package net.yogstation.yogbot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "yogbot.github")
class GithubConfig {
	// Token for authenticating the webhook
	var hmac = ""

	// Oauth token
	var token = ""

	// API link to the base repo, allows for testing
	var repoLink = "https://api.github.com/repos/yogstation13/Yogstation"
}
