package com.davidread.booklistings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
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
     * {@link String} constants for accessing data contained within {@link Bundle} objects.
     */
    private static final String BUNDLE_UI_STATE = "ui_state";
    private static final String BUNDLE_QUERY = "query";
    private static final String BUNDLE_BOOK_LOADER_ID = "book_loader_id";
    private static final String BUNDLE_LIST_OF_BOOKS = "list_of_books";
    private static final String BUNDLE_START_INDEX = "start_index";

    /**
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener} for the {@link SearchView}
     * in the app bar. This object defines how to handle queryTextSubmit and queryTextChange events.
     */
    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        /**
         * Called by {@link SearchView} when a user submits a query. When this happens, start a new
         * {@link BookLoader} and collapse the search box.
         *
         * @param query {@link String} query submitted by the user.
         * @return Whether this event was handled by this method.
         */
        @Override
        public boolean onQueryTextSubmit(String query) {
            startBookLoader(query, 0);
            searchViewMenuItem.collapseActionView();
            return false;
        }

        /**
         * Called by {@link SearchView} when a user changes the text in the search box. Do nothing
         * on this event.
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
         * that was clicked.
         *
         * @param parent    The parent {@link AdapterView}.
         * @param view      The {@link View} object that was clicked.
         * @param position  The int index of the clicked view in the list.
         * @param id        The long id of the clicked view in the list.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // Do nothing if a footer view is clicked.
            if (position >= parent.getCount() - 1) {
                return;
            }

            Book book = (Book) parent.getAdapter().getItem(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(book.getUrl()));
            startActivity(intent);
        }
    };

    /**
     * {@link android.widget.AbsListView.OnScrollListener} for the {@link ListView} in the activity
     * layout. This object defines how to handle scrollStateChanged and onScroll events.
     */
    private final AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        /**
         * Do nothing while the {@link ListView} is being scrolled.
         *
         * @param view          {@link View} whose scroll state is being reported.
         * @param scrollState   int representing the current scroll state. 0 means SCROLL_STATE_IDLE
         *                      and 1 means SCROLL_STATE_TOUCH_SCROLL.
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        /**
         * Called when the {@link ListView} has been scrolled.
         *
         * @param view              {@link View} whose scroll state is being reported.
         * @param firstVisibleItem  The index of the first visible item.
         * @param visibleItemCount  The number of visible items.
         * @param totalItemCount    The number of items in the list adapter.
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount == totalItemCount) {
                startBookLoader(query, totalItemCount - 1);
            }
        }
    };

    /**
     * {@link androidx.loader.app.LoaderManager.LoaderCallbacks} for the {@link BookLoader} that is
     * initialized when a search query is submitted. This object defines how to handle createLoader,
     * loadFinished, and loaderReset events.
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
            int startIndex = 0;
            if (args != null) {
                query = args.getString(BUNDLE_QUERY);
                startIndex = args.getInt(BUNDLE_START_INDEX);
            }
            return new BookLoader(MainActivity.this, query, startIndex);
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
            if (bookLoader.getStartIndex() == 0) {
                resetList();
            }
            addBooksToList(data);
            updateUiToResultsState(bookLoader.getQuery());
        }

        /**
         * Called by {@link LoaderManager} when a {@link BookLoader} needs to be reset. It simply
         * resets the UI to a start state.
         *
         * @param loader    {@link Loader} object to be reset.
         */
        @Override
        public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        }
    };

    /**
     * {@link MenuItem} that holds the {@link SearchView}. Made global so it is accessible in the
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener}.
     */
    private MenuItem searchViewMenuItem;

    /**
     * int representing the state the user interface is currently in.
     */
    private int uiState;

    /**
     * {@link String} representing the most recent query submitted.
     */
    private String query;

    /**
     * int representing the id of the most recently initialized {@link BookLoader} object.
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
        initializeUi();
        updateUiToStartState();
    }

    /**
     * {@link AppCompatActivity} callback method that saves the instance state of the activity when
     * the configuration changes. It saves the UI state, the most recent query, the most recent
     * book loader id, and the content of the {@link ListView} within a {@link Bundle} object and
     * persists it during the configuration change.
     *
     * @param outState {@link Bundle} object where all the data is saved.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_UI_STATE, uiState);
        outState.putString(BUNDLE_QUERY, query);
        outState.putInt(BUNDLE_BOOK_LOADER_ID, bookLoaderId);

        // Only need to save the ListView content if it is in the results state.
        if (uiState == 2) {
            ListView listView = findViewById(R.id.book_list_view);
            HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView.getAdapter();
            BookAdapter bookAdapter = (BookAdapter) headerViewListAdapter.getWrappedAdapter();
            List<Book> books = bookAdapter.getObjects();
            outState.putParcelableArrayList(BUNDLE_LIST_OF_BOOKS, (ArrayList<? extends Parcelable>) books);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * {@link AppCompatActivity} callback method that restores the instance state of the activity
     * after a configuration change. It restores the UI state, the most recent query, the most
     * recent book loader id, and the content of the {@link ListView} from a {@link Bundle} object.
     *
     * @param savedInstanceState {@link Bundle} object where all the data is saved.
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        uiState = savedInstanceState.getInt(BUNDLE_UI_STATE);
        query = savedInstanceState.getString(BUNDLE_QUERY);
        bookLoaderId = savedInstanceState.getInt(BUNDLE_BOOK_LOADER_ID);

        // Restore the user interface and any loading operation that were occurring before the configuration change.
        switch (uiState) {
            case 0:
                // Restore to the start state.
                updateUiToStartState();
                break;
            case 1:
                // Restore to the loading state.
                startBookLoader(query, 0);
                break;
            case 2:
                // Restore to the results state.
                List<Book> books = savedInstanceState.getParcelableArrayList(BUNDLE_LIST_OF_BOOKS);
                resetList();
                addBooksToList(books);
                updateUiToResultsState(query);
                break;
            case 3:
                // Restore to the error state.
                updateUiToErrorState();
                break;
            default:
                updateUiToStartState();
        }

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
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }

    /**
     * Initializes the user interface of the activity. This entails setting up the {@link ListView}
     * with a new {@link BookAdapter}, a {@link android.widget.AdapterView.OnItemClickListener}, an
     * empty view, and a footer view.
     */
    private void initializeUi() {
        ListView listView = findViewById(R.id.book_list_view);
        listView.setAdapter(new BookAdapter(this, new ArrayList<>()));
        listView.setOnItemClickListener(onItemClickListener);

        TextView alertTextView = findViewById(R.id.alert_text_view);
        listView.setEmptyView(alertTextView);

        View loadingFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_loading, null, false);
        listView.addFooterView(loadingFooter);
    }

    /**
     * Updates the user interface of this activity to the start state. The app bar title will
     * display the app name, the search button will be shown, the {@link ListView} will be hidden,
     * the {@link android.widget.AbsListView.OnScrollListener} of the {@link ListView} will be
     * disabled, the alert {@link TextView} will display a start message, and the
     * {@link ProgressBar} will be hidden.
     */
    private void updateUiToStartState() {

        // Set global UI state.
        uiState = 0;

        // Update the app bar title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }

        // Show the search button.
        if (searchViewMenuItem != null) {
            searchViewMenuItem.setVisible(true);
        }

        // Hide the ListView.
        ListView listView = findViewById(R.id.book_list_view);
        listView.setVisibility(View.INVISIBLE);

        // Disable scroll listener for ListView.
        listView.setOnScrollListener(null);

        // Update the alert TextView.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        alertTextView.setText(getString(R.string.alert_start));
        alertTextView.setVisibility(View.VISIBLE);

        // Hide ProgressBar.
        ProgressBar progressBar = findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Updates the user interface of this activity to the loading state. The app bar title will be
     * hidden, the search button will be hidden, the {@link ListView} will be hidden, the
     * {@link android.widget.AbsListView.OnScrollListener} of the {@link ListView} will be disabled,
     * the alert {@link TextView} will be hidden, and the {@link ProgressBar} will be made visible.
     */
    private void updateUiToLoadingState() {

        // Set global UI state.
        uiState = 1;

        // Update the app bar title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }

        // Hide the search button.
        if (searchViewMenuItem != null) {
            searchViewMenuItem.setVisible(false);
        }

        // Hide the ListView.
        ListView listView = findViewById(R.id.book_list_view);
        listView.setVisibility(View.INVISIBLE);

        // Disable scroll listener for ListView.
        listView.setOnScrollListener(null);

        // Hide the alert TextView.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        alertTextView.setVisibility(View.INVISIBLE);

        // Show ProgressBar.
        ProgressBar progressBar = findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the user interface of this activity to the list state. The app bar title will display
     * the query, the search button will be shown, the {@link ListView} will be shown, the
     * {@link android.widget.AbsListView.OnScrollListener} of the {@link ListView} will be disabled,
     * the alert {@link TextView} will show an alert message about the list being empty, and the
     * {@link ProgressBar} will be hidden.
     *
     * @param query {@link String} containing the query.
     */
    private void updateUiToResultsState(String query) {

        // Set global UI state.
        uiState = 2;

        // Update the app bar title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.results, query));
        }

        // Show the search button.
        if (searchViewMenuItem != null) {
            searchViewMenuItem.setVisible(true);
        }

        // Show the ListView.
        ListView listView = findViewById(R.id.book_list_view);
        listView.setVisibility(View.VISIBLE);

        // Enable scroll listener for ListView.
        listView.setOnScrollListener(onScrollListener);

        // Update the alert TextView.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        alertTextView.setText(getString(R.string.alert_list_empty));
        alertTextView.setVisibility(View.INVISIBLE);

        // Hide ProgressBar.
        ProgressBar progressBar = findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Updates the user interface of this activity to the error state. The app bar title will be
     * hidden, the search button will be shown, the {@link ListView} will be hidden, the
     * {@link android.widget.AbsListView.OnScrollListener} of the {@link ListView} will be disabled,
     * the alert {@link TextView} will show some alert message, and the {@link ProgressBar} will be
     * hidden.
     */
    private void updateUiToErrorState() {

        // Set global UI state.
        uiState = 3;

        // Update the app bar title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }

        // Show the search button.
        if (searchViewMenuItem != null) {
            searchViewMenuItem.setVisible(true);
        }

        // Hide the ListView.
        ListView listView = findViewById(R.id.book_list_view);
        listView.setVisibility(View.INVISIBLE);

        // Disable scroll listener for ListView.
        listView.setOnScrollListener(null);

        // Update the alert TextView.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        alertTextView.setText(getString(R.string.alert_no_internet));
        alertTextView.setVisibility(View.VISIBLE);

        // Show ProgressBar.
        ProgressBar progressBar = findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Starts a new {@link BookLoader} to fetch book data from the Google Books API matching the
     * given query and start index parameters. If the device is not connected to the Internet, the
     * {@link BookLoader} will not be started and the UI will be put in the error state.
     *
     * @param query      {@link String} representing the query.
     * @param startIndex int representing the start index.
     */
    private void startBookLoader(String query, int startIndex) {

        // Check if device is connected to the Internet.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isDeviceConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (isDeviceConnected) {
            // Device is connected, put UI in loading state and start new BookLoader.
            updateUiToLoadingState();
            MainActivity.this.query = query;
            Bundle args = new Bundle();
            args.putString(BUNDLE_QUERY, query);
            args.putInt(BUNDLE_START_INDEX, startIndex);
            LoaderManager.getInstance(MainActivity.this).initLoader(bookLoaderId++, args, loaderCallbacks);
        } else {
            // Device is not connected, put UI in error state.
            updateUiToErrorState();
        }
    }

    /**
     * Adds an {@link List} of {@link Book} objects to the {@link ListView} adapter.
     *
     * @param books {@link List} to add to the adapter.
     */
    private void addBooksToList(List<Book> books) {
        ListView listView = findViewById(R.id.book_list_view);
        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView.getAdapter();
        BookAdapter bookAdapter = (BookAdapter) headerViewListAdapter.getWrappedAdapter();
        bookAdapter.addAll(books);
    }

    /**
     * Removes all objects from the {@link ListView} adapter.
     */
    private void resetList() {
        ListView listView = findViewById(R.id.book_list_view);
        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView.getAdapter();
        BookAdapter bookAdapter = (BookAdapter) headerViewListAdapter.getWrappedAdapter();
        bookAdapter.clear();
    }
}