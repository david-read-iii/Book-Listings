package com.davidread.booklistings;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link BookLoader} is a utility class that provides an {@link AsyncTaskLoader} for requesting
 * and retrieving Google Book API data via a network request on a background thread.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * {@link String} specifying the base Google Books API URL.
     */
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    /**
     * {@link String} specifying the query for the API request.
     */
    private final String query;

    /**
     * int specifying the start index for the API request.
     */
    private final int startIndex;

    /**
     * Constructs a new {@link BookLoader} object.
     *
     * @param context    {@link Context} for the superclass constructor.
     * @param query      {@link String} specifying the query for the API request.
     * @param startIndex int specifying the start index for the API request.
     */
    public BookLoader(@NonNull Context context, String query, int startIndex) {
        super(context);
        this.query = query;
        this.startIndex = startIndex;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Callback method invoked to perform the actual load on a worker thread and return the result.
     * It fetches book data from the Google Books API that matches the query and start index
     * parameters and returns it in a {@link List} of {@link Book} objects.
     *
     * @return A {@link List} of {@link Book} objects returned by the API request.
     */
    @Nullable
    @Override
    public List<Book> loadInBackground() {

        // Construct URL object for network request.
        URL url = constructQueryUrl(query, startIndex);

        // Perform network request.
        String json = null;
        try {
            json = getJsonFromUrl(url);
        } catch (IOException e) {
            Log.e(BookLoader.class.getSimpleName(), "Error closing input stream", e);
        }

        // Return extracted List of Book objects from the JSON string.
        return extractBooksFromJson(json);
    }

    /**
     * Returns a {@link String} representing the query used for the API request.
     *
     * @return {@link String} representing the query used for the API request.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Returns an int representing the start index used for the API request.
     *
     * @return int representing the start index used for the API request.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Returns a {@link URL} object that queries the Google Books API given a {@link String}
     * specifying the query.
     *
     * @param query      {@link String} specifying the query for the API request.
     * @param startIndex int specifying the start index for the API request.
     * @return A {@link URL} object that queries the Google Books API given a {@link String}
     * specifying the query.
     */
    private static URL constructQueryUrl(String query, int startIndex) {

        // Construct string url.
        query = query.replace(" ", "+");
        String stringUrl = BASE_URL + "?q=" + query + "&startIndex=" + startIndex + "&maxResults=40";

        // Construct URL object.
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(BookLoader.class.getSimpleName(), "Error constructing URL object", e);
        }

        return url;
    }

    /**
     * Returns a {@link String} JSON response fetched from the network request made using the
     * given {@link URL} object.
     *
     * @param url {@link URL} object to make a network request on.
     * @return {@link String} JSON response fetched from the network request.
     */
    private static String getJsonFromUrl(URL url) throws IOException {

        // Initialize objects used for network request.
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String json = "";

        try {
            // Setup the network request and execute it.
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            /* If the request is successful, get the input stream from the request and convert it
             * into a JSON string. */
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                json = getJsonFromInputStream(inputStream);
            } else {
                Log.e(BookLoader.class.getSimpleName(), "Network request returned with response code " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(BookLoader.class.getSimpleName(), "Error making network request", e);
        } finally {
            // Cleanup objects used for network request.
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return json;
    }

    /**
     * Returns a {@link String} JSON response given an {@link InputStream} fetched from a network
     * request.
     *
     * @param inputStream {@link InputStream} object fetched from a network request.
     * @return A {@link String} JSON response.
     */
    private static String getJsonFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String bufferedReaderLine = bufferedReader.readLine();
        while (bufferedReaderLine != null) {
            stringBuilder.append(bufferedReaderLine);
            bufferedReaderLine = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    /**
     * Returns a {@link List} of {@link Book} objects parsed from a {@link String} JSON response
     * received from the Google Books API.
     *
     * @param json {@link String} JSON response received from Google Books API.
     * @return {@link List} of {@link Book} objects parsed from a {@link String} JSON response.
     */
    private static List<Book> extractBooksFromJson(String json) {

        List<Book> books = new ArrayList<>();

        try {
            JSONObject rootJsonObject = new JSONObject(json);
            JSONArray itemsJsonArray = rootJsonObject.getJSONArray("items");
            for (int itemsIndex = 0; itemsIndex < itemsJsonArray.length(); itemsIndex++) {

                JSONObject volumeInfoJsonObject = itemsJsonArray.getJSONObject(itemsIndex).getJSONObject("volumeInfo");

                String title = volumeInfoJsonObject.getString("title");
                JSONArray authorsJsonArray = volumeInfoJsonObject.getJSONArray("authors");
                String[] authors = new String[authorsJsonArray.length()];
                for (int authorsIndex = 0; authorsIndex < authorsJsonArray.length(); authorsIndex++) {
                    authors[authorsIndex] = authorsJsonArray.getString(authorsIndex);
                }
                String url = volumeInfoJsonObject.getString("infoLink");

                books.add(new Book(title, authors, url));
            }
        } catch (JSONException e) {
            Log.e(BookLoader.class.getSimpleName(), "Error parsing JSON response", e);
        }

        return books;
    }
}
