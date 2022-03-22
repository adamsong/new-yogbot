package net.yogstation.yogbot.http;

public class CallbackData {
	public String csrftoken;
	public String state;
	
	public CallbackData(String csrftoken, String state) {
		this.csrftoken = csrftoken;
		this.state = state;
	}
}
