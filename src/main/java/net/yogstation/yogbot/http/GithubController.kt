package net.yogstation.yogbot.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import net.yogstation.yogbot.config.GithubConfig
import net.yogstation.yogbot.util.Result
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

@Component
class GithubController(private val webClient: WebClient, private val mapper: ObjectMapper, private val githubConfig: GithubConfig) {

	fun postPR(channel: MessageChannel, prNumber: String): Mono<*> {

		return makeRequest("https://api.github.com/repos/yogstation13/Yogstation/pulls/$prNumber")
			.onStatus( { responseCode -> responseCode == HttpStatus.NOT_FOUND }, {
				makeRequest("https://api.github.com/repos/yogstation13/Yogstation/issues/$prNumber")
					.toEntity(String::class.java)
					.flatMap { issueEntity -> channel.createMessage(getIssueEmbed(issueEntity.body)) }
					.then(Mono.empty())
			})
			.toEntity(String::class.java)
			.flatMap { prEntity -> if(prEntity.statusCode.is2xxSuccessful) channel.createMessage(getPrEmbed(prEntity.body)) else Mono.empty<Any>()}
	}

	private fun getPrEmbed(jsonData: String?): EmbedCreateSpec {
		if (jsonData == null) return EmbedCreateSpec.create().withTitle("PR data is null")
		val data = mapper.readTree(jsonData)
		if (data.get("message") != null) return EmbedCreateSpec.create().withTitle("Unable to get PR")
		val changelog = compileChangelog(data)

		var state = "Closed"
		var color: Color = Color.RED
		if(data.get("state").asText() == "open") {
			state = "Open"
			color = Color.GREEN
		} else if(data.get("merged").asBoolean()) {
			state = "Merged"
			color = Color.of(0x9541A5)
		}

		val changelogBuilder = StringBuilder()
		if (changelog.hasError()) changelogBuilder.append("There was an error compiling changelog: ").append(changelog.error)
		else {
			changelog.value?.entries?.forEach { entry -> changelogBuilder.append(":").append(entry.emoji).append(":: ").append(entry.body).append("\n") }
			if(changelogBuilder.length > 800) {
				changelogBuilder.setLength(0)
				changelogBuilder.append("Changelog exceeds maximum length")
			}
		}

		val title = data.get("title").asText().replace("<", "")
		return EmbedCreateSpec.builder()
			.author("$state Pull Request", "", "https://i.imgur.com/tpkgmo8.png")
			.description(title)
			.addField("Author", changelog.value?.author ?: data.get("user").get("login").asText(), true)
			.addField("Number", "#${data.get("number").asText()}", true)
			.addField("Github Link", data.get("html_url").asText(), false)
			.addField("Changelog", changelogBuilder.toString(), false)
			.color(color).build()
	}

	private fun getIssueEmbed(jsonData: String?): EmbedCreateSpec {
		if (jsonData == null) return EmbedCreateSpec.create().withTitle("Issue data is null")
		val data: JsonNode = mapper.readTree(jsonData)
		if(data.get("message") != null || data.get("pull_request") != null) return EmbedCreateSpec.create().withTitle("Unable to get issue data")

		var color: Color = Color.RED
		var state = "Closed"
		if(data.get("state").asText() == "open") {
			color = Color.GREEN
			state = "Open"
		}
		val title = data.get("title").asText().replace("<", "")
		return EmbedCreateSpec.builder()
			.author("$state Issue", "", "https://i.imgur.com/tpkgmo8.png")
			.description(title)
			.addField("Author", data.get("user").get("login").asText(), true)
			.addField("Number", "#${data.get("number").asText()}", true)
			.addField("Github Link", data.get("html_url").asText(), false)
			.color(color)
			.build()
	}

	private fun compileChangelog(data: JsonNode): Result<Changelog?, String?> {

		val body = data.get("body").asText().replace("\r\n", "\n").split("\n")
		var username = data.get("user").get("login").asText()

		val changelog: MutableList<ChangelogEntry> = ArrayList()
		var inCLTag = false
		var foundOpeningTag = false
		var foundClosingTag = false

		for (wideLine in body) {
			val line = wideLine.trim()

			if (line.startsWith(":cl:") || line.startsWith("\uD83C\uDD91")) {
				inCLTag = true
				foundOpeningTag = true

				val clAuthors: List<String> = line.split(" ", limit = 2)
				username = if (clAuthors.size < 2) username else clAuthors[2]
				continue
			}

			if (line.startsWith("/:cl:") ||
				line.startsWith("/ :cl:") ||
				line.startsWith("/\uD83C\uDD91") ||
				line.startsWith("/ \uD83C\uDD91") ||
				line.startsWith(":/\uD83C\uDD91")
			) {
				if(!inCLTag) return Result.error("Found the end of the changelog before the beginning")
				inCLTag = false
				foundClosingTag = true
				continue
			}

			if(!inCLTag) continue

			val entryData = line.split(" ", limit = 2)
			if(entryData.size < 2 || !entryData[0].endsWith(":")) continue

			val entryType = entryData[0].substring(0, entryData[0].length - 1)
			val entryText = entryData[1]
			changelog.add(when(entryType) {
				"fix", "fixes", "bugfix" -> ChangelogEntry("bugfix", "bug", entryText)
				"wip" -> ChangelogEntry("wip", "biohazard", entryText)
				"rsctweak", "tweaks", "tweak" -> ChangelogEntry("tweak", "wrench", entryText)
				"soundadd" -> ChangelogEntry("soundadd", "loud_sound", entryText)
				"sounddel" -> ChangelogEntry("sounddel", "mute", entryText)
				"add", "adds", "rscadd" -> ChangelogEntry("rscadd", "battery", entryText)
				"del", "dels", "rscdel" -> ChangelogEntry("rscdel", "octagonal_sign", entryText)
				"imageadd" -> ChangelogEntry("imageadd", "art", entryText)
				"imagedel" -> ChangelogEntry("imagedel", "scissors", entryText)
				"typo",	"spellcheck" ->	ChangelogEntry("spellcheck", "pen_ballpoint", entryText)
				"experimental",	"experiment" -> ChangelogEntry("experiment", "biohazard", entryText)
				"tgs" -> ChangelogEntry("tgs", "question", entryText)
				else -> return Result.error("Unknown tag $entryType")
			})
		}
		if (foundOpeningTag && !foundClosingTag) {
			return Result.error("Changlog closing tag was never found")
		}

		if(!foundOpeningTag) {
			return Result.error("Changelog not found")
		}

		return Result.success(Changelog(username, changelog))

	}

	data class Changelog(val author: String, val entries: List<ChangelogEntry>)
	data class ChangelogEntry(val type: String, val emoji: String, val body: String)

	private fun makeRequest(uri: String): WebClient.ResponseSpec {
		val clientRequest = webClient
			.get()
			.uri(URI.create(uri))
			.header("User-Agent", "Yogbot13")
		if(githubConfig.token != "") {
			clientRequest.header("Authorization", "Token ${githubConfig.token}")
		}
		return clientRequest.retrieve()
	}
}
