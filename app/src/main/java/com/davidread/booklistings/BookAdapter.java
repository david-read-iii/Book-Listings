package com.davidread.booklistings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link BookAdapter} is an adapter class. It adapts an {@link List} of {@link Book} objects into
 * {@link View} objects.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Construct a new {@link BookAdapter} object.
     *
     * @param context {@link Context} for the superclass constructor.
     * @param objects {@link List} of {@link Book} objects to adapt.
     */
    public BookAdapter(@NonNull Context context, @NonNull List<Book> objects) {
        super(context, 0, objects);
    }

    /**
     * Returns an adapted {@link View} object constructed from a {@link Book} object in the
     * {@link List} passed into the constructor of {@link BookAdapter}.
     *
     * @param position    Int index representing which {@link Book} object to adapt from the
     *                    {@link List}.
     * @param convertView {@link View} object to be returned.
     * @param parent      The parent {@link ViewGroup}.
     * @return A {@link View} object constructed from a {@link Book} object in the {@link List}
     * passed into the constructor of {@link BookAdapter}.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Inflate list item layout if passed convertView is null.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_book, parent, false);
        }

        // Get the appropriate Book object from the list.
        Book book = getItem(position);

        // Populate convertView with attributes of the Book object.
        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        titleTextView.setText(book.getTitle());

        TextView authorsTextView = convertView.findViewById(R.id.authors_text_view);
        authorsTextView.setText(getFormattedAuthorsString(book.getAuthors()));

        return convertView;
    }

    /**
     * Returns a {@link List} of {@link Book} objects that are stored within this adapter.
     *
     * @return A {@link List} of {@link Book} objects that are stored within this adapter.
     */
    public List<Book> getObjects() {
        List<Book> books = new ArrayList<>();
        if (getCount() > 0) {
            for (int index = 0; index < getCount(); index++) {
                books.add(getItem(index));
            }
        }
        return books;
    }

    /**
     * Returns a formatted string for the authors string array member variable of a {@link Book}
     * object.
     *
     * @param authors {@link String} array representing the authors of a {@link Book} object.
     * @return A formatted string for the authors string array member variable of a {@link Book}
     * object.
     */
    private String getFormattedAuthorsString(String[] authors) {
        StringBuilder formattedAuthorsString = new StringBuilder();
        for (int index = 0; index < authors.length; index++) {
            if (index > 0) {
                formattedAuthorsString.append(", ");
            }
            formattedAuthorsString.append(authors[index]);
        }
        return formattedAuthorsString.toString();
    }
}
