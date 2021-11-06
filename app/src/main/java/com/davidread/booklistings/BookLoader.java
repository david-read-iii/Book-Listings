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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link BookLoader} is a utility class that provides an {@link AsyncTaskLoader} for requesting
 * and retrieving data from the Google Books API. More specifically, it allows you to perform a
 * volumes search for book data, where you can specify a query term and a start index for
 * pagination.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * {@link String} specifying the base Google Books API URL for performing a volumes search.
     */
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    /**
     * {@link String} URL parameter specifying the maximum number of results the volumes search
     * should return.
     */
    private static final String MAX_RESULTS_URL_PARAMETER = "maxResults=40";

    /**
     * {@link String} URL parameter specifying what specific JSON fields the volumes search should
     * return.
     */
    private static final String FIELDS_URL_PARAMETER = "fields=items(volumeInfo/title,volumeInfo/authors,volumeInfo/infoLink)";

    /**
     * {@link String} specifying the query term for the volumes search.
     */
    private final String query;

    /**
     * int specifying the start index for the volumes search.
     */
    private final int startIndex;

    /**
     * Constructs a new {@link BookLoader} object.
     *
     * @param context    {@link Context} for the superclass constructor.
     * @param query      {@link String} specifying the query term for the volumes search.
     * @param startIndex int specifying the start index for the volumes search.
     */
    public BookLoader(@NonNull Context context, String query, int startIndex) {
        super(context);
        this.query = query;
        this.startIndex = startIndex;
    }

    /**
     * Callback method invoked directly before the actual load is executed. Call forceLoad() to
     * start the loader.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Callback method invoked to perform the actual load on a worker thread and return the result.
     * It performs a volumes search on the Google Books API given the parameters used to construct
     * this {@link BookLoader}, retrieves that data, and returns it in a {@link List} of
     * {@link Book} objects.
     *
     * @return A {@link List} of {@link Book} objects returned from the Google Books API volumes
     * search.
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
     * Returns a {@link String} representing the query term used for the Google Books API volumes
     * search.
     *
     * @return {@link String} representing the query term used for the Google Books API volumes
     * search.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Returns an int representing the start index used for the Google Books API volumes search.
     *
     * @return int representing the start index used for the Google Books API volumes search.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Returns a {@link URL} object for performing a Google Books API volumes search given a query
     * term and start index.
     *
     * @param query      {@link String} representing the query term.
     * @param startIndex int representing the start index.
     * @return {@link URL} object for performing a Google Books API volumes search.
     */
    private static URL constructQueryUrl(String query, int startIndex) {

        // Construct string URL.
        String stringUrl = "";
        try {
            String queryUrlParameter = "q=" + URLEncoder.encode(query, StandardCharsets.UTF_8.name());
            String startIndexUrlParameter = "startIndex=" + startIndex;
            stringUrl = BASE_URL + "?" + queryUrlParameter + "&" + startIndexUrlParameter + "&" + MAX_RESULTS_URL_PARAMETER + "&" + FIELDS_URL_PARAMETER;
        } catch (UnsupportedEncodingException e) {
            Log.e(BookLoader.class.getSimpleName(), "Error encoding query term for string URL", e);
        }

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
     * Parses a {@link String} JSON response received from a Google Books API volumes search and
     * returns it in a {@link List} of {@link Book} objects.
     *
     * @param json {@link String} JSON response from a Google Books API volumes search.
     * @return {@link List} of {@link Book} objects parsed from a JSON response.
     */
    private static List<Book> extractBooksFromJson(String json) {

        List<Book> books = new ArrayList<>();

        // Get items JSON array containing the results.
        JSONArray itemsJsonArray = null;
        try {
            JSONObject rootJsonObject = new JSONObject(json);
            itemsJsonArray = rootJsonObject.getJSONArray("items");
        } catch (JSONException e) {
            Log.e(BookLoader.class.getSimpleName(), "Error parsing items JSON array", e);
        }

        // Return early if a null items JSON array is parsed.
        if (itemsJsonArray == null) {
            return books;
        }

        // Iterate through each item in the items JSON array.
        for (int itemsIndex = 0; itemsIndex < itemsJsonArray.length(); itemsIndex++) {

            // Get the volumeInfo JSON object for this result.
            JSONObject volumeInfoJsonObject = null;
            try {
                volumeInfoJsonObject = itemsJsonArray.getJSONObject(itemsIndex).getJSONObject("volumeInfo");
            } catch (JSONException e) {
                Log.e(BookLoader.class.getSimpleName(), "Error parsing the volumeInfo JSON object for the item with index " + itemsIndex, e);
            }

            // Skip to the next item in the items JSON array if a null volumeInfo JSON object is parsed.
            if (volumeInfoJsonObject == null) {
                continue;
            }

            // Get the properties for this result.
            String title = "";
            try {
                title = volumeInfoJsonObject.getString("title");
            } catch (JSONException e) {
                Log.e(BookLoader.class.getSimpleName(), "Error parsing the title JSON property for the item with index " + itemsIndex, e);
            }

            String[] authors = new String[]{""};
            try {
                JSONArray authorsJsonArray = volumeInfoJsonObject.getJSONArray("authors");
                authors = new String[authorsJsonArray.length()];
                for (int authorsIndex = 0; authorsIndex < authorsJsonArray.length(); authorsIndex++) {
                    authors[authorsIndex] = authorsJsonArray.getString(authorsIndex);
                }
            } catch (JSONException e) {
                Log.e(BookLoader.class.getSimpleName(), "Error parsing the authors JSON property for the item with index " + itemsIndex, e);
            }

            String url = "";
            try {
                url = volumeInfoJsonObject.getString("infoLink");
            } catch (JSONException e) {
                Log.e(BookLoader.class.getSimpleName(), "Error parsing the url JSON property for the item with index " + itemsIndex, e);
            }

            // Add a new Book object for this result.
            books.add(new Book(title, authors, url));
        }

        return books;
    }
}
