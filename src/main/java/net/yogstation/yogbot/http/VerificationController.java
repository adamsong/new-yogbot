package net.yogstation.yogbot.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.config.HttpConfig;
import net.yogstation.yogbot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

@RestController
public class VerificationController {
	private static final String errorTpl = "`<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, shrink-to-fit=no\"><title>Yogstation Account Linking</title><link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\"></head><body><header class=\"d-flex justify-content-center align-items-center\" style=\"height: 3rem;background: var(--bs-blue);\"><h1 style=\"color: rgb(255,255,255);\">Yogstation Account Linking</h1></header><div class=\"container\" style=\"margin-top: 2rem;\"><div class=\"card\"><div class=\"card-body d-flex flex-column align-items-center\"><h4 class=\"card-title\">An error occured!</h4><p class=\"card-text\">$errormsg$</p></div></div></div><script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js\"></script></body></html>`";
	private static final String confirmTpl = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, shrink-to-fit=no\"><title>Yogstation Account Linking</title><link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\"></head><body><header class=\"d-flex justify-content-center align-items-center\" style=\"height: 3rem;background: var(--bs-blue);\"><h1 style=\"color: rgb(255,255,255);\">Yogstation Account Linking</h1></header><div class=\"container\" style=\"margin-top: 2rem;\"><div class=\"card\"><div class=\"card-body d-flex flex-column justify-content-center align-items-center\"><h4 class=\"card-title\">Please confirm account linking</h4><p class=\"card-text\">Is this your discord account?</p><span>$usertag$</span><img class=\"rounded-circle d-block\" src=\"$useravatar$\" style=\"margin: 0 auto;\"><form style=\"margin-top: 2rem;\" method=\"post\"><input class=\"form-control\" type=\"hidden\" value=\"$csrftoken$\" name=\"csrftoken\"><input class=\"form-control\" type=\"hidden\" name=\"state\" value=\"$state$\"><button class=\"btn btn-danger\" type=\"button\" style=\"margin: 0 1rem;\" onclick=\"window.close()\">No</button><button class=\"btn btn-success\" type=\"submit\" style=\"margin: 0 1rem;\">Yes</button></form></div></div></div><script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js\"></script></body></html>";
	private static final String completeTpl = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, shrink-to-fit=no\"><title>Yogstation Account Linking</title><link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css\"></head><body><header class=\"d-flex justify-content-center align-items-center\" style=\"height: 3rem;background: var(--bs-blue);\"><h1 style=\"color: rgb(255,255,255);\">Yogstation Account Linking</h1></header><div class=\"container\" style=\"margin-top: 2rem;\"><div class=\"card\"><div class=\"card-body d-flex flex-column align-items-center\"><h4 class=\"card-title\">Your account is now linked!</h4></div></div></div><script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js\"></script></body></html>";
	private static final Logger LOGGER = LoggerFactory.getLogger(VerificationController.class);
	public final Map<String, AuthIdentity> oauthState = new HashMap<>();
	private final WebClient webClient;
	private final ObjectMapper mapper;
	private final DiscordConfig discordConfig;
	private final HttpConfig httpConfig;
	private final DatabaseManager database;
	private final GatewayDiscordClient client;
	private final SecureRandom random = new SecureRandom();
	
	public VerificationController(WebClient webClient, ObjectMapper mapper, DiscordConfig discordConfig, HttpConfig httpConfig, DatabaseManager database,
	                              GatewayDiscordClient client) {
		this.webClient = webClient;
		this.mapper = mapper;
		this.discordConfig = discordConfig;
		this.httpConfig = httpConfig;
		this.database = database;
		this.client = client;
	}
	
	@GetMapping("/api/verify")
	public ResponseEntity<Void> doRedirect(@RequestParam(value = "state") String state) {
		StringBuilder urlBuilder = new StringBuilder(discordConfig.oauthAuthorizeUrl);
		urlBuilder.append("?response_type=code");
		urlBuilder.append("&client_id=").append(discordConfig.oauthClientId);
		urlBuilder.append("&redirect_uri=").append(httpConfig.publicPath).append("api/callback");
		urlBuilder.append("&scope=openid");
		urlBuilder.append("&state=").append(state);
		
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlBuilder.toString())).build();
	}
	
	@PostMapping(value = "/api/callback", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public HttpEntity<String> callbackPost(CallbackData data) {
		String state = data.state;
		String csrfToken = data.csrftoken;
		if (!oauthState.containsKey(state)) return new HttpEntity<>(hydrateError(String.format("State %s is unknown", state)));
		
		AuthIdentity authIdentity = oauthState.get(state);
		if (authIdentity.csrfToken == null || !authIdentity.csrfToken.equals(csrfToken))
			return new HttpEntity<>(hydrateError("CSRF token mismatch"));
		oauthState.remove(state);
		
		try (Connection connection = database.getConnection();
		     PreparedStatement queryStmt = connection.prepareStatement(
			     String.format("SELECT discord_id FROM `%s` WHERE `ckey` = ?", database.prefix("player")));
		     PreparedStatement linkStmt = connection.prepareStatement(
			     String.format("UPDATE `%s` SET `discord_id` = ? WHERE `ckey` = ?", database.prefix("player")))) {
			queryStmt.setString(1, authIdentity.ckey);
			ResultSet queryResults = queryStmt.executeQuery();
			if (!queryResults.next()) return new HttpEntity<>(
				hydrateError("New account detected, please login on the server at least once to proceed"));
			queryResults.close();
			
			linkStmt.setString(1, authIdentity.snowflake.asString());
			linkStmt.setString(2, authIdentity.ckey);
			linkStmt.execute();
			if (linkStmt.getUpdateCount() < 1) return new HttpEntity<>(hydrateError("Failed to link accounts!"));
			
			client.getMemberById(Snowflake.of(discordConfig.mainGuildID), authIdentity.snowflake)
				.flatMap(member -> member.addRole(Snowflake.of(discordConfig.byondVerificationRole)))
				.subscribe();
			return new HttpEntity<>(hydrateComplete());
		} catch (SQLException e) {
			LOGGER.error("Error linking accounts", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hydrateError("An error has occurred"));
		}
	}
	
	@GetMapping("/api/callback")
	public Mono<HttpEntity<String>> getCallback(@RequestParam(value = "error", required = false) String error,
	                                            @RequestParam(value = "error_description", required = false) String errorDescription,
	                                            @RequestParam(value = "state", required = false) String state,
	                                            @RequestParam(value = "code", required = false) String code) {
		if(error != null) return Mono.just(new HttpEntity<>(hydrateError(String.format("Upstream login error: %s", errorDescription == null ? error : errorDescription))));
		
		ResponseEntity.BodyBuilder response = ResponseEntity.status(HttpStatus.OK).header("X-Frame-Options", "DENY");
		
		if(!oauthState.containsKey(state)) return Mono.just(response.body(hydrateError("State is unknown")));
		AuthIdentity identity = oauthState.get(state);
		if(identity.csrfToken != null) return Mono.just(response.body(hydrateError("Authorization request already used")));
		
		MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
		
		bodyValues.add("grant_type", "authorization_code");
		bodyValues.add("code", code);
		bodyValues.add("redirect_uri", String.format("%sapi/callback", httpConfig.publicPath));
		
		String authToken = Base64.getEncoder().encodeToString(String.format("%s:%s", discordConfig.oauthClientId, discordConfig.oauthClientSecret).getBytes(
			StandardCharsets.UTF_8));
		
		return webClient.post()
			.uri(URI.create(discordConfig.oauthTokenUrl))
			.header("Authorization", String.format("Basic %s", authToken))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(bodyValues))
			.retrieve()
			.bodyToMono(String.class)
			.flatMap(token -> useToken(token, response, identity, state));
		
	}
	
	private Mono<HttpEntity<String>> useToken(String token, ResponseEntity.BodyBuilder response, AuthIdentity identity,
	                                          String state) {
		String accessToken;
		try{
			JsonNode root = mapper.readTree(token);
			JsonNode errorNode = root.get("error");
			if(errorNode != null) {
				JsonNode errorDescriptionNode = root.get("error_description");
				return Mono.just(response.body(hydrateError(String.format("Upstream error when fetching access token: %s", errorDescriptionNode == null ? errorNode.asText() : errorDescriptionNode.asText()))));
			}
			
			accessToken = root.get("access_token").asText();
			
		} catch (IOException e) {
			LOGGER.error("Error getting token", e);
			return Mono.just(response.body(hydrateError("An error occurred while fetching access token")));
		}
		
		return webClient.get().uri(URI.create(discordConfig.oauthUserInfoUrl))
			.header("Authorization", String.format("Bearer %s", accessToken))
			.retrieve()
			.toEntity(String.class)
			.flatMap(ckeyResponseEntity -> {
				if(!ckeyResponseEntity.getStatusCode().is2xxSuccessful() || ckeyResponseEntity.getBody() == null)
					return Mono.just(response.body("Invalid access token when fetching user info"));
				String ckey;
				try {
					ckey = StringUtils.ckey_ize(mapper.readTree(ckeyResponseEntity.getBody()).get("ckey").asText());
				} catch (JsonProcessingException e) {
					LOGGER.info("Error processing info response", e);
					return Mono.just(response.body(hydrateError("Failed to parse API response")));
				}
				if(!ckey.equals(identity.ckey)) {
					return Mono.just(response.body(hydrateError(String.format("Ckey does not match, you attempted to login using %s while the linking process was initialized with %s", ckey, identity.ckey))));
				}
				byte[] bytes = new byte[32];
				random.nextBytes(bytes);
				identity.csrfToken = StringUtils.bytesToHex(bytes);
				return Mono.just(response.body(hydrateConfirm(identity.tag, identity.avatar, state, identity.csrfToken)));
			});
	}
	
	private String hydrateError(String error) {
		return errorTpl.replace("$errormsg$", escapeHtml4(error));
	}
	
	private String hydrateComplete() {
		return completeTpl;
	}
	
	private String hydrateConfirm(String rawDiscordTag, String rawDiscordAvatar, String rawState, String rawCsrfToken) {
		String discordTag = escapeHtml4(rawDiscordTag);
		String discordAvatar = escapeHtml4(rawDiscordAvatar);
		String state = escapeHtml4(rawState);
		String csrfToken = escapeHtml4(rawCsrfToken);
		return confirmTpl.replaceAll("\\$usertag\\$", discordTag).replaceAll("\\$useravatar\\$", discordAvatar).replaceAll("\\$state\\$", state).replaceAll("\\$csrftoken\\$", csrfToken);
	}
	
	public static final class AuthIdentity {
		public String ckey;
		public Snowflake snowflake;
		public String avatar;
		public String tag;
		public String csrfToken = null;
		
		public AuthIdentity(String ckey, Snowflake snowflake, String avatar, String tag) {
			this.ckey = ckey;
			this.snowflake = snowflake;
			this.avatar = avatar;
			this.tag = tag;
		}
	}
	
}
