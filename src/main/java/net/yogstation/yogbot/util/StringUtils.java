package net.yogstation.yogbot.util;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

	// Taken from https://stackoverflow.com/a/13592567/2628615
	public static Map<String, List<String>> splitQuery(String url) {
		return Arrays.stream(url.split("&"))
			.map(StringUtils::splitQueryParameter)
			.collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, Collectors.mapping(Map.Entry::getValue, toList())));
	}

	public static AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
		final int idx = it.indexOf("=");
		final String key = idx > 0 ? it.substring(0, idx) : it;
		final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : "";
		return new AbstractMap.SimpleImmutableEntry<>(
			URLDecoder.decode(key, StandardCharsets.UTF_8),
			URLDecoder.decode(value, StandardCharsets.UTF_8)
		);
	}

	public static String ckey_ize(String key) {
		return key.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
	}
}
