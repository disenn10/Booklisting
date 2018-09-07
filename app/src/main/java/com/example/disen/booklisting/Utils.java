package com.example.disen.booklisting;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by disen on 7/5/2017.
 */

public class Utils {

    public static ArrayList<Books> fetchData(String query) {
        String json = null;
        URL url = createURL(query);
        json = makeHttpRequest(url);
        Log.e(Utils.class.getSimpleName(), "Http request made ");
        ArrayList<Books> listOfBooks = extractDatafromJson(json);
        Log.e(Utils.class.getSimpleName(), "return list of Books");
        return listOfBooks;
    }

    public static URL createURL(String query) {
        URL url = null;
        if (query != null) {
            try {
                url = new URL(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    public static String makeHttpRequest(URL url) {
        String json = "";

        if (url == null) {
            Log.e(Utils.class.getSimpleName(), "json is null and is  ");
            return json;
        }
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                json = readFromJson(inputStream);
            } else {
                Log.e(Utils.class.getSimpleName(), "Http response is: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(Utils.class.getSimpleName(), "couldn't make http request ");
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e(Utils.class.getSimpleName(), "json is ");
        return json;

    }


    public static String readFromJson(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        String line = "";
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }

    public static ArrayList<Books> extractDatafromJson(String json) {
        String author;
        String title;
        String image = "";
        Double rating;
        String date = "";
        String description = "";
        String weblink = "";
        if (json == null) {
            return null;
        }
        ArrayList<Books> books = new ArrayList<>();
        try {
            JSONObject volumes = new JSONObject(json);
            JSONArray items = volumes.optJSONArray("items");
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject itemsJSONObject = items.getJSONObject(i);
                    JSONObject bookInfo = itemsJSONObject.getJSONObject("volumeInfo");
                    //If book has a title get it
                    if (bookInfo.has("title")) {
                        title = bookInfo.getString("title");
                    } else {
                        title = " no title";
                    }
                    //if book has an author, get it
                    if (bookInfo.has("authors")) {
                        author = bookInfo.getJSONArray("authors").get(0).toString();
                    } else {
                        author = "Unknown author/s";
                    }
                    //if book has images get it
                    if (bookInfo.has("imageLinks")) {
                        JSONObject imageLinks = bookInfo.getJSONObject("imageLinks");
                        if (imageLinks.has("thumbnail")) {
                            image = imageLinks.getString("thumbnail");
                        } else {
                            image = "";
                        }
                    }
                    if (bookInfo.has("averageRating")) {
                        rating = bookInfo.getDouble("averageRating");
                    } else {
                        rating = 0.0;
                    }
                    if (bookInfo.has("publishedDate")) {
                        date = bookInfo.getString("publishedDate");
                    } else {
                        date = "unknown date";
                    }
                    if (bookInfo.has("infoLink")) {
                        weblink = bookInfo.getString("infoLink");
                    }
                    if (bookInfo.has("description")) {
                        description = bookInfo.getString("description");
                    } else {
                        description = "No description";
                    }

                    Books book = new Books(author, title, image, rating, date, description, weblink);
                    books.add(book);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return books;
    }
}
