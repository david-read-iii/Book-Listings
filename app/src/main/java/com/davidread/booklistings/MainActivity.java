package com.davidread.booklistings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MainActivity} is an activity class that represents a searchable book listing. A
 * {@link SearchView} in the app bar specifies the name of the book they want to query. A
 * {@link ListView} maintains a list of books returned by the most recent search query.
 * TODO: Keep updating documentation.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * {@link MenuItem} that holds the {@link SearchView}. Made global so it is accessible in the
     * {@link androidx.appcompat.widget.SearchView.OnQueryTextListener}.
     */
    private MenuItem searchViewMenuItem;

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

            // Update UI with a list of sample books.
            ArrayList<Book> sampleBooks = new ArrayList<>();
            sampleBooks.add(new Book("Android For Dummies", "Dan Gookin", "http://books.google.com/books?id=JGH0DwAAQBAJ&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Learning Android", "Marko Gargenta", "http://books.google.com/books?id=oMYQz4_BW48C&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Hello, Android", "Ed Burnette", "https://play.google.com/store/books/details?id=_A5QDwAAQBAJ&source=gbs_api"));
            sampleBooks.add(new Book("Android Phones For Dummies", "Dan Gookin", "http://books.google.com/books?id=-OwtDQAAQBAJ&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Embedded Android", "Karim Yaghmour", "http://books.google.com/books?id=KER0dd2oYP8C&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Android App Development For Dummies", "Michael Burton", "http://books.google.com/books?id=nDqkBgAAQBAJ&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Programming Android", "Zigurd Mednieks", "http://books.google.com/books?id=5BGBswAQSiEC&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Teach Yourself VISUALLY Android Phones and Tablets", "Guy Hart-Davis", "http://books.google.com/books?id=M7ngCAAAQBAJ&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Beginning Android Tablet Application Development", "Wei-Meng Lee", "http://books.google.com/books?id=WLrAqVo4HzcC&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Professional Android", "Reto Meier", "http://books.google.com/books?id=aYpoDwAAQBAJ&dq=android&hl=&source=gbs_api"));
            updateUi(sampleBooks);

            getSupportActionBar().setTitle(getString(R.string.results, query));
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
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

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
     * sets up the {@link SearchView} with an
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
     * Updates the {@link ListView} in the activity layout to display the data contained within
     * the {@link List} of {@link Book} objects.
     *
     * @param books {@link List} of {@link Book} objects to display.
     */
    private void updateUi(List<Book> books) {
        ListView listView = findViewById(R.id.book_list_view);

        // Setup BookAdapter to adapt Book objects into View objects for the ListView.
        BookAdapter bookAdapter = new BookAdapter(this, books);
        listView.setAdapter(bookAdapter);

        // Attach onItemClickListener to ListView.
        listView.setOnItemClickListener(onItemClickListener);

        // Setup alert TextView to be visible if the list is empty.
        TextView alertTextView = findViewById(R.id.alert_text_view);
        alertTextView.setText(R.string.alert_list_empty);
        listView.setEmptyView(alertTextView);
    }

    /**
     * Invalidates the data displayed in the {@link ListView} in the activity layout.
     *
     * @param alertMessage {@link String} explaining why the data was invalidated.
     */
    private void invalidateUi(String alertMessage) {
        ListView listView = findViewById(R.id.book_list_view);

        // Remove all elements from the list in BookAdapter.
        BookAdapter bookAdapter = (BookAdapter) listView.getAdapter();
        if (bookAdapter != null) {
            bookAdapter.clear();
        }

        // Setup alert TextView to explain why the list is invalidated.
        TextView listTextView = findViewById(R.id.alert_text_view);
        listTextView.setText(alertMessage);
        listTextView.setVisibility(View.VISIBLE);
    }
}