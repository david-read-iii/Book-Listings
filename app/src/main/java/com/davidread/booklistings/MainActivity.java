package com.davidread.booklistings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MainActivity} is an activity class that represents a searchable book listing screen. A
 * {@link SearchView} in the app bar specifies the book they want to query. A {@link ListView}
 * maintains a list of books returned by the most recent search query.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * {@link String} constants for accessing objects contained within {@link Bundle} objects.
     */
    private static final String BUNDLE_USER_INTERFACE = "user_interface";
    private static final String BUNDLE_BOOKS = "books";
    private static final String BUNDLE_APP_BAR_TITLE = "app_bar_title";
    private static final String BUNDLE_ALERT_MESSAGE = "alert_message";
    private static final String BUNDLE_BOOK_LOADER_ID = "book_loader_id";
    private static final String BUNDLE_QUERY = "query";

    /**
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener} for the {@link SearchView}
     * in the app bar. This object defines how to handle queryTextSubmit and queryTextChange events.
     */
    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        /**
         * Initialize new {@link BookLoader} for the specified query and collapses the
         * {@link SearchView}.
         *
         * @param query {@link String} query submitted by the user.
         * @return Whether this event was handled by this method.
         */
        @Override
        public boolean onQueryTextSubmit(String query) {
            updateUiToLoadingState();
            Bundle args = new Bundle();
            args.putString(BUNDLE_QUERY, query);
            getSupportLoaderManager().initLoader(bookLoaderId++, args, loaderCallbacks);
            searchViewMenuItem.collapseActionView();
            return false;
        }

        /**
         * Do nothing when the search query text is changed.
         *
         * @param newText   {@link String} query changed by the user.
         * @return Whether this event was handled by this method.
         */
        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    /**
     * {@link android.widget.AdapterView.OnItemClickListener} for the {@link ListView} in the
     * activity layout. This object defines how to handle itemClick events.
     */
    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        /**
         * Start an intent to open the device's browser when an item is clicked. The URL will be
         * the one associated with the {@link Book} object associated with the {@link View} object
         * that was clicked..
         *
         * @param parent    The parent {@link AdapterView}.
         * @param view      The {@link View} object that was clicked.
         * @param position  The int index of the clicked view in the list.
         * @param id        The long id of the clicked view in the list.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Book book = (Book) parent.getAdapter().getItem(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(book.getUrl()));
            startActivity(intent);
        }
    };

    /**
     * {@link androidx.loader.app.LoaderManager.LoaderCallbacks} for the {@link BookLoader} that is
     * initialized when a search query is submitted.
     */
    private final LoaderManager.LoaderCallbacks<List<Book>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<Book>>() {

        /**
         * Called by {@link LoaderManager} when it needs a new {@link BookLoader} object.
         *
         * @param id    Unique int id for the {@link BookLoader} object.
         * @param args  {@link Bundle} containing arguments for the {@link BookLoader}.
         * @return A {@link BookLoader} object.
         */
        @NonNull
        @Override
        public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {
            String query = "";
            if (args != null) {
                query = args.getString(BUNDLE_QUERY);
            }
            return new BookLoader(MainActivity.this, query);
        }

        /**
         * Called by {@link LoaderManager} when a {@link BookLoader} finishes. It updates the UI with
         * data fetched by the {@link BookLoader}.
         *
         * @param loader    {@link Loader} object that finished.
         * @param data      {@link List} of {@link Book} objects returned by the {@link BookLoader}.
         */
        @Override
        public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {
            BookLoader bookLoader = (BookLoader) loader;
            updateUiWithList(data, getString(R.string.results, bookLoader.getQuery()), getString(R.string.alert_list_empty));
        }

        /**
         * Called by {@link LoaderManager} when a {@link BookLoader} needs to be reset. It simply
         * resets the UI to a start state.
         *
         * @param loader    {@link Loader} object to be reset.
         */
        @Override
        public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
            updateUiToStartState();
        }
    };

    /**
     * {@link MenuItem} that holds the {@link SearchView}. Made global so it is accessible in the
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener}.
     */
    private MenuItem searchViewMenuItem;

    /**
     * int representing the id of the current {@link BookLoader} object. It must be unique for each
     * search query.
     */
    private int bookLoaderId;

    /**
     * {@link AppCompatActivity} callback method that inflates the activity layout and initializes
     * the user interface to the start state.
     *
     * @param savedInstanceState {@link Bundle} for superclass constructor.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateUiToStartState();
    }

    /**
     * {@link AppCompatActivity} callback method that saves the instance state of the activity when
     * the configuration changes. Data about the {@link ListView}, the app bar title, and the text
     * that displays when the list is empty are stored within a {@link Bundle} object that persists
     * during the configuration change.
     *
     * @param outState {@link Bundle} object where data should be saved that will survive the
     *                 configuration change.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBundle(BUNDLE_USER_INTERFACE, getUi());
        outState.putInt(BUNDLE_BOOK_LOADER_ID, bookLoaderId);
        super.onSaveInstanceState(outState);
    }

    /**
     * {@link AppCompatActivity} callback method that restores the instance state of the activity
     * after a configuration change. Data about the {@link ListView}, the app bar title, and the
     * text that displays when the list is empty are restored from a {@link Bundle} object that
     * survived the configuration change.
     *
     * @param savedInstanceState The {@link Bundle} object that was passed in onSaveInstanceState().
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Bundle userInterfaceBundle = savedInstanceState.getBundle(BUNDLE_USER_INTERFACE);
        updateUiWithList(userInterfaceBundle.getParcelableArrayList(BUNDLE_BOOKS),
                userInterfaceBundle.getString(BUNDLE_APP_BAR_TITLE),
                userInterfaceBundle.getString(BUNDLE_ALERT_MESSAGE)
        );
        bookLoaderId = savedInstanceState.getInt(BUNDLE_BOOK_LOADER_ID);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * {@link AppCompatActivity} callback method that inflates the menu layout in the app bar. It
     * also makes the search view {@link MenuItem} global and sets up the {@link SearchView} with an
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener}.
     *
     * @param menu {@link Menu} object where the menu layout should be inflated.
     * @return Whether the menu should be displayed in the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchViewMenuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    /**
     * Updates the user interface of this activity to the start state. This entails hiding the
     * {@link ProgressBar}, nullifying the adapter of the {@link ListView}, updating the app bar
     * title, and updating the alert {@link TextView}.
     */
    private void updateUiToStartState() {

        // Hide ProgressBar.
        ProgressBar progressBar = findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        // Nullify ListView adapter.
        ListView listView = findViewById(R.id.book_list_view);
        listView.setAdapter(null);

        // Update the app bar title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }

        // Update the alert TextView.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        alertTextView.setText(getString(R.string.alert_start));
        alertTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the user interface of this activity to the loading state. This entails showing the
     * {@link ProgressBar}, nullifying the adapter of the {@link ListView}, updating the app bar
     * title, and updating the alert {@link TextView}.
     */
    private void updateUiToLoadingState() {

        // Show ProgressBar.
        ProgressBar progressBar = findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        // Nullify ListView adapter.
        ListView listView = findViewById(R.id.book_list_view);
        listView.setAdapter(null);

        // Update the app bar title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }

        // Hide the TextView that shows when the list is empty.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        alertTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Updates the user interface of this activity to display a list of results. This entails hiding
     * the {@link ProgressBar}, setting up the {@link ListView}, updating the app bar title, and
     * updating the alert {@link TextView}.
     *
     * @param books        {@link List} of {@link Book} objects to display.
     * @param appBarTitle  {@link String} to display in the app bar title.
     * @param alertMessage {@link String} to display in the alert {@link TextView}.
     */
    private void updateUiWithList(List<Book> books, String appBarTitle, String alertMessage) {

        // Hide ProgressBar.
        ProgressBar progressBar = findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        // Update the ListView.
        ListView listView = findViewById(R.id.book_list_view);
        listView.setAdapter(new BookAdapter(this, books));
        listView.setOnItemClickListener(onItemClickListener);

        // Update the app bar title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(appBarTitle);
        }

        // Update the TextView that shows when the list is empty.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        alertTextView.setText(alertMessage);
        alertTextView.setVisibility(View.VISIBLE);
        listView.setEmptyView(alertTextView);
    }

    /**
     * Returns the attributes of the user interface in a {@link Bundle} object. Such attributes
     * include the {@link List} of {@link Book} objects that were displayed in the {@link ListView},
     * the {@link String} displayed in the app bar title, and the {@link String} displayed in the
     * {@link TextView} that displays when the list is empty.
     *
     * @return The attributes of the user interface in a {@link Bundle} object
     */
    private Bundle getUi() {
        Bundle bundle = new Bundle();

        // Put List object displayed on the ListView in the Bundle object.
        ListView listView = findViewById(R.id.book_list_view);
        BookAdapter bookAdapter = (BookAdapter) listView.getAdapter();
        List<Book> books = new ArrayList<>();
        if (bookAdapter != null) {
            books = bookAdapter.getObjects();
        }
        bundle.putParcelableArrayList(BUNDLE_BOOKS, (ArrayList<? extends Parcelable>) books);

        // Put app bar title in the Bundle object.
        String actionBarTitle = getString(R.string.app_name);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.getTitle() != null) {
            actionBarTitle = actionBar.getTitle().toString();
        }
        bundle.putString(BUNDLE_APP_BAR_TITLE, actionBarTitle);

        // Put alert message in the Bundle object.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        String alertMessage = alertTextView.getText().toString();
        bundle.putString(BUNDLE_ALERT_MESSAGE, alertMessage);

        return bundle;
    }
}