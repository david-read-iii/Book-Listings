package com.davidread.booklistings;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Book} is a model class for a book listing. It implements the {@link Parcelable} interface
 * so that {@link Book} objects may be passed as arguments between activities.
 */
public class Book implements Parcelable {

    /**
     * {@link android.os.Parcelable.Creator} object that generates instances of this class from
     * a {@link Parcelable} object.
     */
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    /**
     * {@link String} representing the title of the book.
     */
    private String title;

    /**
     * {@link String} array representing the authors that wrote the book.
     */
    private String[] authors;

    /**
     * {@link String} representing the URL that points to a detailed web page for the book.
     */
    private String url;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param title   {@link String} representing the title of the book.
     * @param authors {@link String} array representing the authors that wrote the book.
     * @param url     {@link String} representing the URL that points to a detailed web page for the
     *                book.
     */
    public Book(String title, String[] authors, String url) {
        this.title = title;
        this.authors = authors;
        this.url = url;
    }

    /**
     * Constructs a new {@link Book} object.
     *
     * @param in {@link Parcelable} object that contains the member variables of the {@link Book}
     *           object to be constructed.
     */
    protected Book(Parcel in) {
        title = in.readString();
        authors = in.createStringArray();
        url = in.readString();
    }

    /**
     * Returns a {@link String} representing the title of the book.
     *
     * @return {@link String} representing the title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns a {@link String} array representing the authors that wrote the book.
     *
     * @return {@link String} array representing the authors that wrote the book.
     */
    public String[] getAuthors() {
        return authors;
    }

    /**
     * Returns a {@link String} representing the URL that points to a detailed web page for the
     * book.
     *
     * @return {@link String} representing the URL that points to a detailed web page for the book.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns an int that describes the type of objects contained in this {@link Parcelable}
     * instance.
     *
     * @return An int that describes the type of objects contained in this {@link Parcelable}
     * instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Converts the member variables of this {@link Book} object into a {@link Parcel} object.
     *
     * @param dest  {@link Parcel} object where the member variables will be stored.
     * @param flags Additional flags.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeStringArray(authors);
        dest.writeString(url);
    }
}
