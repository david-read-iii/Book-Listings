package com.davidread.booklistings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
     * {@link String} constants for accessing objects contained within {@link Bundle} objects.
     */
    public static final String BUNDLE_USER_INTERFACE = "user_interface";
    public static final String BUNDLE_BOOKS = "books";
    public static final String BUNDLE_APP_BAR_TITLE = "app_bar_title";
    public static final String BUNDLE_ALERT_MESSAGE = "alert_message";

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
            sampleBooks.add(new Book("Android For Dummies", new String[]{"Dan Gookin"}, "http://books.google.com/books?id=JGH0DwAAQBAJ&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Learning Android", new String[]{"Marko Gargenta"}, "http://books.google.com/books?id=oMYQz4_BW48C&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Hello, Android", new String[]{"Ed Burnette"}, "https://play.google.com/store/books/details?id=_A5QDwAAQBAJ&source=gbs_api"));
            sampleBooks.add(new Book("Android Phones For Dummies", new String[]{"Dan Gookin"}, "http://books.google.com/books?id=-OwtDQAAQBAJ&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Embedded Android", new String[]{"Karim Yaghmour"}, "http://books.google.com/books?id=KER0dd2oYP8C&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Android App Development For Dummies", new String[]{"Michael Burton"}, "http://books.google.com/books?id=nDqkBgAAQBAJ&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Programming Android", new String[]{"Zigurd Mednieks"}, "http://books.google.com/books?id=5BGBswAQSiEC&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Teach Yourself VISUALLY Android Phones and Tablets", new String[]{"Guy Hart-Davis"}, "http://books.google.com/books?id=M7ngCAAAQBAJ&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Beginning Android Tablet Application Development", new String[]{"Wei-Meng Lee"}, "http://books.google.com/books?id=WLrAqVo4HzcC&dq=android&hl=&source=gbs_api"));
            sampleBooks.add(new Book("Professional Android", new String[]{"Reto Meier"}, "http://books.google.com/books?id=aYpoDwAAQBAJ&dq=android&hl=&source=gbs_api"));
            updateUi(sampleBooks, getString(R.string.results, query), getString(R.string.alert_list_empty));

            // Collapse SearchView.
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
        updateUi(userInterfaceBundle.getParcelableArrayList(BUNDLE_BOOKS),
                userInterfaceBundle.getString(BUNDLE_APP_BAR_TITLE),
                userInterfaceBundle.getString(BUNDLE_ALERT_MESSAGE)
        );
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
     * Updates the user interface of this activity. The {@link List} of {@link Book} objects is
     * displayed in a {@link ListView} object, the {@link String} appBarTitle is displayed in the
     * app bar's title, and the {@link String} alertMessage is set as text that displays when the
     * list is empty.
     *
     * @param books {@link List} of {@link Book} objects to display.
     */
    private void updateUi(List<Book> books, String appBarTitle, String alertMessage) {

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