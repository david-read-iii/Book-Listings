package com.davidread.booklistings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        authorsTextView.setText(book.getAuthors());

        return convertView;
    }
}
