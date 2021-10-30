package com.davidread.booklistings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

/**
 * {@link MainActivity} is an activity class that represents a searchable book listing. A
 * {@link SearchView} in the app bar specifies the name of the book they want to query.
 * TODO: Keep updating documentation.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener} for the {@link SearchView}
     * in the app bar. This object defines how to handle queryTextSubmit and queryTextChange events.
     */
    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        /**
         * TODO: Implement actual search behavior.
         *
         * @param query {@link String} query submitted by the user.
         * @return Whether this event was handled by this method.
         */
        @Override
        public boolean onQueryTextSubmit(String query) {
            Toast.makeText(MainActivity.this, "Search submitted for \"" + query + "\"", Toast.LENGTH_SHORT).show();
            return false; // False so the SearchView loses focus.
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
     * {@link AppCompatActivity} callback method that inflates the activity layout.
     *
     * @param savedInstanceState {@link Bundle} for superclass constructor.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * {@link AppCompatActivity} callback method that inflates the menu layout in the app bar and
     * sets up the {@link SearchView}.
     *
     * @param menu {@link Menu} object where the menu layout should be inflated.
     * @return Whether the menu should be displayed in the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Attach OnQueryTextListener to the SearchView in the app bar.
        SearchView searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);

        return true;
    }
}