package com.friarframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

public class BookData {
	
	private static BookData mInstance = null;
	
	private List<String> htmlFiles = new ArrayList<String>();
	private HashMap<String, Integer> htmlMap = new HashMap<String, Integer>();
	
	private BookData(Context context) {
		loadData(context);
	}
	
	public static BookData getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new BookData(context);
		}
		return mInstance;
	}
	
	public int numPages() {
		return htmlFiles.size();
	}
	
	public String getPage(int index) {
		return htmlFiles.get(index);
	}
	
	private void loadData(Context context) {
		try {
			InputStream instream = context.getAssets().open("book/book.json");
			String json = convertStreamToString(instream);
			JSONObject jsonObject = (JSONObject) new JSONTokener(json).nextValue();
//			if (jsonObject.getString("orientation").equals("landscape")) {
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			}
			JSONArray contents = jsonObject.getJSONArray("contents");

			for (int index = 0; index < contents.length(); index++) {
				htmlFiles.add(contents.getString(index));
				htmlMap.put(contents.getString(index), index);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private String convertStreamToString(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}

}
