package com.davidread.booklistings;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

/**
 * {@link SearchActivity} is an activity class whose user interface includes a {@link SearchView} in
 * the app bar for specifying a query term for starting the {@link ResultsActivity}.
 */
public class SearchActivity extends AppCompatActivity {

    /**
     * {@link String} names for identifying intent extras that originate from this activity.
     */
    public static final String INTENT_EXTRA_QUERY = "intent_extra_query";

    /**
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener} defines how the
     * {@link SearchView} handles its queryTextSubmit and queryTextChange events.
     */
    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        /**
         * Handles queryTextSubmit event. Collapse the search view {@link MenuItem} and start the
         * {@link ResultsActivity} for the query term on this event.
         *
         * @param query {@link String} query term specified by the user.
         * @return Whether the queryTextSubmit event was handled by this handler.
         */
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchMenuItem.collapseActionView();
            Intent resultsIntent = new Intent(SearchActivity.this, ResultsActivity.class);
            resultsIntent.putExtra(INTENT_EXTRA_QUERY, query);
            startActivity(resultsIntent);
            return false;
        }

        /**
         * Handles queryTextChange events. Do nothing on this event.
         *
         * @param newText {@link String} query term specified by the user.
         * @return Whether the queryTextChange event was handled by this handler.
         */
        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }
    };

    /**
     * {@link MenuItem} that holds the {@link SearchView}. Is global so it's accessible in the
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener} object.
     */
    private MenuItem searchMenuItem;

    /**
     * Handles {@link AppCompatActivity} create event. Inflate the activity layout on this event.
     *
     * @param savedInstanceState {@link Bundle} for the superclass constructor.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    /**
     * Handles {@link AppCompatActivity} createOptionsMenu event. Inflate the menu layout and setup
     * the {@link SearchView} on this event.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchMenuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }
}