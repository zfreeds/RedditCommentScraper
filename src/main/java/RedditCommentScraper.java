import java.io.BufferedWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cedarsoftware.util.io.JsonWriter;

public class RedditCommentScraper {

	/*
	 * Author: Zachary Freedman Purpose: Scrapes all primary comments on a
	 * reddit thread and stores them in a json file. Running the program without
	 * changes will collect all the jokes from a dark humour thread
	 * 
	 */

	// variables to change for personalization
	static String url = "https://www.reddit.com/r/AskReddit/comments/2b55qg/whats_your_favorite_dark_humor_joke/";
	static String path = ""; // Path to the new files location: can be empty
	static String fileName = "darkJokes.json";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String formattedJson = scrapeRedditComments(url);

		Boolean success = writeJsonToFile(formattedJson);
		if (!success) {
			System.out.println("Program Not Successful");
		}

	}

	// takes in the url of a reddit thread
	public static String scrapeRedditComments(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			// we want parent comments but don't want child comments
			List<Element> parents = doc.select(".entry.unvoted .usertext-body.may-blank-within.md-container");
			List<Element> children = doc.select(".child .entry.unvoted .usertext-body.may-blank-within.md-container");

			// remove all child comments from the list so only parent comments
			// remain
			for (Element child : children) {

				if (parents.contains(child)) {
					parents.remove(child);
				}
			}
			JSONArray json = new JSONArray();
			// add comments to json
			for (Element comment : parents) {

				// ignore deleted or removed comments
				if (!comment.text().equalsIgnoreCase("[deleted]") && !comment.text().equalsIgnoreCase("[removed]")) {
					JSONObject commentObject = new JSONObject();
					commentObject.put("comment", comment.text());
					json.put(commentObject);

				}
			}
			return JsonWriter.formatJson(json.toString());

		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
	}

	// takes in the output of scrapeRedditComments(String url)
	// straightforward writting a string to file
	// WARNING: OVERWRITES FILES
	public static boolean writeJsonToFile(String json) {
		if (json == null) {
			return false;
		}
		try {
			FileWriter fw;
			fw = new FileWriter(path + fileName);

			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(json);

			bw.close();
			fw.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
