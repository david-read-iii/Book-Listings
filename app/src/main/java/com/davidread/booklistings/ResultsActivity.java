package com.davidread.booklistings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ResultsActivity} is an activity class whose user interface has a {@link ListView}
 * displaying the results of a user submitted query.
 */
public class ResultsActivity extends AppCompatActivity {

    /**
     * {@link String} names for {@link Bundle} objects used in this activities.
     */
    private static final String BUNDLE_NEXT_BOOK_LOADER_ID = "bundle_next_book_loader_id";

    /**
     * {@link android.widget.AdapterView.OnItemClickListener} defines how the {@link ListView}
     * handles its itemClick event.
     */
    private final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        /**
         * Handles itemClick event. Start an intent to open the browser on this event. The URL of
         * the site will be determined by the {@link Book} object associated with the clicked item.
         *
         * @param parent    The parent {@link AdapterView}.
         * @param view      The {@link View} object that was clicked.
         * @param position  The int index of the clicked view in the list.
         * @param id        The long id of the clicked view in the list.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // Get Book object associated with the clicked item.
            Book book = (Book) parent.getAdapter().getItem(position);

            // Do nothing if the Book object has an invalid URL.
            if (!URLUtil.isValidUrl(book.getUrl())) {
                return;
            }

            // Start intent to open the browser for the URL.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(book.getUrl()));
            startActivity(intent);
        }
    };

    /**
     * {@link android.widget.AbsListView.OnScrollListener} defines how the {@link ListView} handles
     * its scrollStateChanged and scroll events.
     */
    private final AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        /**
         * Handles scrollStateChanged event. Do nothing on this event.
         *
         * @param view          {@link View} whose scroll state is being reported.
         * @param scrollState   int representing the current scroll state. 0 means SCROLL_STATE_IDLE
         *                      and 1 means SCROLL_STATE_TOUCH_SCROLL.
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        /**
         * Handles scroll event. On this event, initialize a new {@link BookLoader} if all the
         * appropriate conditions are met.
         *
         * @param view              {@link View} whose scroll state is being reported.
         * @param firstVisibleItem  The index of the first visible item.
         * @param visibleItemCount  The number of visible items.
         * @param totalItemCount    The number of items in the list adapter.
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            // Do nothing if the last item is not visible.
            boolean lastItemVisible = view.getLastVisiblePosition() == totalItemCount - 1;
            if (!lastItemVisible) {
                return;
            }

            // Do nothing if book loading is disabled.
            if (!bookLoadingEnabled) {
                return;
            }

            // Do nothing if a BookLoader with this id has already been initialized.
            if (getLoaderManager().getLoader(nextBookLoaderId) != null) {
                return;
            }

            // Return early and disable book loading if the device is not connected to the Internet.
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isDeviceConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
            if (!isDeviceConnected) {
                bookLoadingEnabled = false;
                return;
            }

            // Initialize a new BookLoader if we don't return early.
            LoaderManager.getInstance(ResultsActivity.this).initLoader(nextBookLoaderId, null, loaderCallbacks);
            nextBookLoaderId++;
        }
    };

    /**
     * {@link LoaderManager.LoaderCallbacks} defines how the {@link BookLoader} handles its
     * createLoader, loadFinished, and loaderReset events.
     */
    private final LoaderManager.LoaderCallbacks<List<Book>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<Book>>() {

        /**
         * Handles createLoader event. On this event, disable further {@link BookLoader} objects
         * from being initialized, show a loading dialog in the UI, and initialize a new
         * {@link BookLoader}.
         *
         * @param id    Int id for the {@link BookLoader} object.
         * @param args  {@link Bundle} containing arguments for the {@link BookLoader}.
         * @return A new {@link BookLoader} object.
         */
        @NonNull
        @Override
        public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {

            // Disable further book loading.
            bookLoadingEnabled = false;

            // Show a loading dialog in the UI.
            if (loadingAlertDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultsActivity.this);
                builder.setView(getLayoutInflater().inflate(R.layout.dialog_loading, null));
                builder.setCancelable(false);
                loadingAlertDialog = builder.create();
            }
            loadingAlertDialog.show();

            // Initialize a new BookLoader.
            ListView listView = findViewById(R.id.book_list_view);
            int startIndex = listView.getCount();
            return new BookLoader(ResultsActivity.this, query, startIndex);
        }

        /**
         * Handles loadFinished event. On this event, add the fetched {@link List} to the
         * {@link ListView} adapter, hide the loading dialog in the UI, and enable further
         * {@link BookLoader} objects from being initialized. Only do these things if the BookLoader
         * has not already added its load to the {@link ListView} adapter or if the fetched
         * {@link List} is not empty.
         *
         * @param loader    {@link BookLoader} object that completed the load.
         * @param data      {@link List} of {@link Book} objects fetched during the load.
         */
        @Override
        public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {

            // Hide the loading dialog if not already.
            if (loadingAlertDialog != null) {
                loadingAlertDialog.hide();
            }

            // Do nothing if this BookLoader has already added its load to the list.
            BookLoader bookLoader = (BookLoader) loader;
            ListView listView = findViewById(R.id.book_list_view);
            if (bookLoader.getStartIndex() != listView.getCount()) {
                return;
            }

            // Show the empty view and do not re-enable book loading if the fetched List is empty.
            if (data.isEmpty()) {
                TextView emptyTextView = findViewById(R.id.empty_book_list_text_view);
                listView.setEmptyView(emptyTextView);
                return;
            }

            // Add the List to the adapter and re-enable book loading.
            BookAdapter bookAdapter = (BookAdapter) listView.getAdapter();
            bookAdapter.addAll(data);
            bookLoadingEnabled = true;
        }

        /**
         * Handles the loaderReset event. Do nothing on this event.
         *
         * @param loader    {@link BookLoader} object that is being reset.
         */
        @Override
        public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        }
    };

    /**
     * {@link String} holding the query term.
     */
    private String query;

    /**
     * Boolean representing whether new {@link BookLoader} objects may be initialized.
     */
    private boolean bookLoadingEnabled;

    /**
     * Int representing the next id that may be assigned to a {@link BookLoader}.
     */
    private int nextBookLoaderId;

    /**
     * {@link AlertDialog} that is shown to indicate loading operations.
     */
    private AlertDialog loadingAlertDialog;

    /**
     * Handles the create event for this activity. Initialize global variables and the UI on this
     * event.
     *
     * @param savedInstanceState Optional {@link Bundle} holding saved instance state information.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set global variables.
        query = getIntent().getStringExtra(SearchActivity.INTENT_EXTRA_QUERY);
        bookLoadingEnabled = true;
        nextBookLoaderId = 0;

        // Initialize UI.
        setContentView(R.layout.activity_results);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_bar_title_results, query));
        }
        ListView listView = findViewById(R.id.book_list_view);
        listView.setAdapter(new BookAdapter(this, new ArrayList<>()));
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnScrollListener(onScrollListener);
    }

    /**
     * Handles the optionsItemSelected event for the app bar. Use this handler to have the up
     * button mimic the back button's behavior when it's clicked.
     *
     * @param item {@link MenuItem} clicked during this event.
     * @return Whether the optionsItemSelected event was handled by this handler.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the saveInstanceState event for this activity. Save the value stored in the global
     * nextBookLoaderId variable on this event.
     *
     * @param outState {@link Bundle} object preserved during the configuration change.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_NEXT_BOOK_LOADER_ID, nextBookLoaderId);
    }

    /**
     * Handles the restoreInstanceState event for this activity. Restore the value stored in the
     * global nextBookLoaderId variable and initialize all previously initialized {@link BookLoader}
     * objects on this event.
     *
     * @param savedInstanceState {@link Bundle} object preserved during the configuration change.
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nextBookLoaderId = savedInstanceState.getInt(BUNDLE_NEXT_BOOK_LOADER_ID);
        for (int id = 0; id < nextBookLoaderId; id++) {
            LoaderManager.getInstance(ResultsActivity.this).initLoader(id, null, loaderCallbacks);
        }
    }
}