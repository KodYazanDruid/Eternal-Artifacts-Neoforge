package com.sonamorningstar.eternalartifacts.api.bible;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sonamorningstar.eternalartifacts.EternalArtifacts;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BibleAPI {
	public record BookInfo(String name, int chapters) {}
	public record Book(String name, List<Chapter> chapters) {}
	public record Chapter(int index, List<String> verses) {}
	
	private static final Map<String, Book> CACHED_BIBLE = new ConcurrentHashMap<>();
	private static final Gson GSON = new Gson();
	public static final List<BookInfo> BOOKS = List.of(
		new BookInfo("genesis", 50),
		new BookInfo("exodus", 40),
		new BookInfo("leviticus", 27),
		new BookInfo("numbers", 36),
		new BookInfo("deuteronomy", 34),
		new BookInfo("joshua", 24),
		new BookInfo("judges", 21),
		new BookInfo("ruth", 4),
		new BookInfo("1samuel", 31),
		new BookInfo("2samuel", 24),
		new BookInfo("1kings", 22),
		new BookInfo("2kings", 25),
		new BookInfo("1chronicles", 29),
		new BookInfo("2chronicles", 36),
		new BookInfo("ezra", 10),
		new BookInfo("nehemiah", 13),
		new BookInfo("esther", 10),
		new BookInfo("job", 42),
		new BookInfo("psalms", 150),
		new BookInfo("proverbs", 31),
		new BookInfo("ecclesiastes", 12),
		new BookInfo("song-of-solomon", 8),
		new BookInfo("isaiah", 66),
		new BookInfo("jeremiah", 52),
		new BookInfo("lamentations", 5),
		new BookInfo("ezekiel", 48),
		new BookInfo("daniel", 12),
		new BookInfo("hosea", 14),
		new BookInfo("joel", 3),
		new BookInfo("amos", 9),
		new BookInfo("obadiah", 1),
		new BookInfo("jonah", 4),
		new BookInfo("micah", 7),
		new BookInfo("nahum", 3),
		new BookInfo("habakkuk", 3),
		new BookInfo("zephaniah", 3),
		new BookInfo("haggai", 2),
		new BookInfo("zechariah", 14),
		new BookInfo("malachi", 4),
		new BookInfo("matthew", 28),
		new BookInfo("mark", 16),
		new BookInfo("luke", 24),
		new BookInfo("john", 21),
		new BookInfo("acts", 28),
		new BookInfo("romans", 16),
		new BookInfo("1corinthians", 16),
		new BookInfo("2corinthians", 13),
		new BookInfo("galatians", 6),
		new BookInfo("ephesians", 6),
		new BookInfo("philippians", 4),
		new BookInfo("colossians", 4),
		new BookInfo("1thessalonians", 5),
		new BookInfo("2thessalonians", 3),
		new BookInfo("1timothy", 6),
		new BookInfo("2timothy", 4),
		new BookInfo("titus", 3),
		new BookInfo("philemon", 1),
		new BookInfo("hebrews", 13),
		new BookInfo("james", 5),
		new BookInfo("1peter", 5),
		new BookInfo("2peter", 3),
		new BookInfo("1john", 5),
		new BookInfo("2john", 1),
		new BookInfo("3john", 1),
		new BookInfo("jude", 1),
		new BookInfo("revelation", 22)
	);
	
	public static String fetch(String version, String bookName, int chapter, int verse) {
		try {
			String strUrl = createURL(version, bookName, chapter, verse);
			if (CACHED_BIBLE.containsKey(strUrl)) {
				Book book = CACHED_BIBLE.get(strUrl);
				List<Chapter> chapters = book.chapters();
				try {
					Chapter chap = chapters.get(chapter - 1);
					List<String> verses = chap.verses();
					return verses.get(verse - 1);
				} catch (IndexOutOfBoundsException e) {
					EternalArtifacts.LOGGER.error("Invalid chapter or verse number for Bible API: {}", e.getMessage(), e);
					return "";
				}
			}
			return fetch(strUrl);
		} catch (Exception e) {
			EternalArtifacts.LOGGER.error("Failed to fetch Bible verse: {}", e.getMessage());
			return "";
		}
	}
	
	public static String fetch(String version, String book, int chapter) {
		try {
			String strUrl = createURL(version, book, chapter);
			if (CACHED_BIBLE.containsKey(strUrl)) {
				StringBuilder sb = new StringBuilder();
				CACHED_BIBLE.get(strUrl).chapters().stream()
					.filter(chap -> chap.index() == chapter)
					.findFirst()
					.map(Chapter::verses)
					.ifPresent(verses -> {
						for (String verse : verses) {
							sb.append(verse).append(" ");
						}
					});
				return sb.toString().trim();
			}
			return fetch(strUrl);
		} catch (Exception e) {
			EternalArtifacts.LOGGER.error("Failed to fetch Bible chapter: {}", e.getMessage());
			return "";
		}
	}
	
	private static String fetch(String strUrl) {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			if (conn.getResponseCode() == 200) {
				try (InputStream in = conn.getInputStream();
					 InputStreamReader reader = new InputStreamReader(in)) {
					JsonElement jsonElement = GSON.fromJson(reader, JsonElement.class);
					
					String value = extractText(jsonElement);
					//CACHED_BIBLE.put(strUrl, value);
					return value;
				}
			}
			
			conn.disconnect();
		} catch (IOException e) {
			EternalArtifacts.LOGGER.error("Invalid URL for Bible API: {}", e.getMessage(), e);
		}
		return "";
	}

	private static String extractText(JsonElement el) {
		if (el == null || el.isJsonNull()) return null;
		if (el.isJsonObject()) {
			JsonObject obj = el.getAsJsonObject();
			if (obj.has("text") && obj.get("text").isJsonPrimitive()) {
				return obj.get("text").getAsString();
			}
			if (obj.has("data") && obj.get("data").isJsonArray()) {
				StringBuilder sb = new StringBuilder();
				for (JsonElement element : obj.get("data").getAsJsonArray()) {
					if (element.isJsonObject() && element.getAsJsonObject().has("text")) {
						sb.append(element.getAsJsonObject().get("text").getAsString());
					} else if (element.isJsonPrimitive()) {
						sb.append(element.getAsJsonPrimitive().getAsString());
					}
				}
				return sb.toString();
			}
		}
		if (el.isJsonArray()) {
			StringBuilder sb = new StringBuilder();
			for (JsonElement e : el.getAsJsonArray()) {
				if (e.isJsonObject() && e.getAsJsonObject().has("text")) {
					sb.append(e.getAsJsonObject().get("text").getAsString()).append(" ");
				} else if (e.isJsonPrimitive()) {
					sb.append(e.getAsJsonPrimitive().getAsString());
				}
			}
			return sb.toString();
		}
		if (el.isJsonPrimitive()) return el.getAsString();
		return null;
	}

	private static String createURL(String version, String book, int chapter, int verse) {
		return "https://cdn.jsdelivr.net/gh/wldeh/bible-api/bibles/" + version + "/books/" + book + "/chapters/" + chapter + "/verses/" + verse + ".json";
	}
	
	private static String createURL(String version, String book, int chapter) {
		return "https://cdn.jsdelivr.net/gh/wldeh/bible-api/bibles/" + version + "/books/" + book + "/chapters/" + chapter + ".json";
	}
}
