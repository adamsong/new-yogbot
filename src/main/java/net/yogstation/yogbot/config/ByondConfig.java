package net.yogstation.yogbot.config;

import org.springframework.stereotype.Component;

@Component
public class ByondConfig extends ConfigClass {
	
	public ByondConfig() {
		loadConfig("byond.properties");
	}
	
	public String serverKey = "no key for you :(";
	public String serverWebhookKey = "webkey";

	public String serverAddress = "158.69.120.60";
	public int serverPort = 4133;

	public String serverJoinAddress = "https://yogstation.net/play.php";
}
