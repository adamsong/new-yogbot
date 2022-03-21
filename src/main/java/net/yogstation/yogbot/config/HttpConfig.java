package net.yogstation.yogbot.config;

import org.springframework.stereotype.Component;

@Component
public class HttpConfig extends ConfigClass {
	public String publicPath = "https://yogbot.yogstation.net/";
	
	public HttpConfig() {
		loadConfig("http.properties");
	}
}
