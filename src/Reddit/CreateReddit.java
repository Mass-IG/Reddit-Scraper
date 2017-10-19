package Reddit;
/*
 * Brandon Dalton, Christopher Turner
 * 09/27/2017
 * University of Southern California
 * Viterbi School of Engineering
 * Mass-IG
 * 
 * CreateReddit.java
 * 
 * This class will be used to pull new data every 10 seconds.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;

import Tools.Database;

public class CreateReddit extends TimerTask {

	private Reddit reddit = null;
	private Database db = null;
	private Gson gson = new Gson();
	// Add the REST API link for the sub-reddit here
	private String url = "https://www.reddit.com/r/changemyview/.json";

	public CreateReddit(Database myDB) {
		db = myDB;
	}

	@Override
	public void run() {
		String json = getJson(this.url);
		this.reddit = gson.fromJson(json, Reddit.class);
		ArrayList<Data> allData = new ArrayList<Data>();
		for (Child c : reddit.getData().getChildren()) {
			allData.add(c.getData());
		}
		// try (Statement statement = db.getConn().createStatement()) {
		// ResultSet resultSet = statement.executeQuery("SHOW TABLES");
		// while (resultSet.next()) {
		// System.out.println(resultSet.getString(1));
		// }
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

		if (allData != null && allData.size() > 0) {
			for (Data d : allData) {
				if (!db.existsInDB(d)) {
					db.add(d);
				}
				/*
				 * for (Data commentData : d.getComments()) {
				 * commentData.setBody(cleaner.cleanPost(d.getBody()));
				 * commentData.setLink_title(cleaner.cleanPost(d.getLink_title()));
				 * 
				 * if (!db.existsInDB(commentData)) { db.add(commentData); } }
				 */
			}
		}
		allData.clear();
		String totalPosts = db.countPosts();
		System.out.println("Queried for new top-level posts. Running total: " + totalPosts + " posts.");
	}

	// String -> String
	// Connects to the reddit page then returns the json file
	private static String getJson(String url) {
		Document doc;
		try {

			doc = Jsoup.connect(url).userAgent("chrome").ignoreContentType(true).timeout(0).get();
			String json = doc.getElementsByTag("body").toString();

			return json.substring(8, json.length() - 7);
		} catch (IOException e) {
			System.out.println("JSON fetch error: " + e.getMessage());
		}
		return null;
	}

	// private void getAllData(ArrayList<Children> children) {
	// // For each child from Reddit put all their data into an ArrayList
	// for (Children c : children) {
	// allData.add(c.getData());
	// }
	// /*
	// * ArrayList<Data> allComments = new ArrayList<>();
	// *
	// * for(Data d : allData) { String commentsURL = d.getLink_url(); String
	// * commentJSON = getJson(commentsURL + ".json");
	// * System.out.println(commentJSON); Reddit comments =
	// gson.fromJson(commentJSON,
	// * Reddit.class); for(Children commentData : comments.getData().getChildren())
	// {
	// * allComments.add(commentData.getData()); } d.setComments(allComments);
	// * allComments.clear(); }
	// */
	// }
}
