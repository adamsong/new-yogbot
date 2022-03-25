package net.yogstation.yogbot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "yogbot.discord")
class DiscordConfig {

	var botToken: String = "no token here"
	var commandPrefix: String = "!"

	var serverName: String = "Yogstation 13"
	var useLocalCommands: Boolean = false

	var oauthAuthorizeUrl: String = "https://bab.yogstation.net/auth/authorize"

	var oauthTokenUrl: String = "https://bab.yogstation.net/auth/token"
	var oauthUserInfoUrl: String = "https://bab.yogstation.net/auth/userinfo"
	var oauthClientId: String = ""
	var oauthClientSecret: String = ""

	var msayWebhookUrl: String = ""
	var asayWebhookUrl: String = ""
	var ticketWebhookUrl: String = ""

	var mainGuildID: Long = 134720091576205312L
	var aoRole: Long = 471634210923216906L
	var mentorRole: Long = 505280515322937355L
	var jesterRole: Long = 539846767827746817L
	var subscriberRole: Long = 213375106888499200L

	var manualVerifyRole: Long = 768196461313523727L
	var byondVerificationRole: Long = 762005208326733845L

	// Punishment Roles
	var softBanRole: Long = 302093934551629827L
	var loreBanRole: Long = 716784185192480798L
	var mentorBanRole: Long = 865043884170149928L
	var wikibanRole: Long = 792982865989992490L

	var firstWarningRole: Long = 514066643119505408L
	var secondWarningRole: Long = 514066798216347662L
	var staffPublicBanRole: Long = 290926097028218884L
}
