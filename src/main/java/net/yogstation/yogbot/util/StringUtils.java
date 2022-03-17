package net.yogstation.yogbot.util;

public class StringUtils {
	// Taken from https://stackoverflow.com/a/8155547 then modified
	public static String center(String s, int size) {
		return center(s, size, ' ');
	}

	public static String center(String s, int size, char pad) {
		if (s == null || size <= s.length())
			return s;

		StringBuilder sb = new StringBuilder(size);
		sb.append(String.valueOf(pad).repeat((size - s.length()) / 2));
		sb.append(s);
		while (sb.length() < size) {
			sb.append(pad);
		}
		return sb.toString();
	}

	public static String padStart(String s, int size) {
		return padStart(s, size, ' ');
	}

	public static String padStart(String s, int size, char pad) {
		if(s == null || size <= s.length())
			return s;

		return String.valueOf(pad).repeat(size - s.length()) + s;
	}
}
