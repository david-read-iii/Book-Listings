package com.davidread.booklistings;

/**
 * {@link Book} is a model class for a book listing.
 */
public class Book {

    /**
     * {@link String} representing the title of the book.
     */
    private String title;

    /**
     * {@link String} representing the authors that wrote the book.
     */
    private String authors;

    /**
     * {@link String} representing the URL that points to a detailed web page for the book.
     */
    private String url;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param title   {@link String} representing the title of the book.
     * @param authors {@link String} representing the authors that wrote the book.
     * @param url     {@link String} representing the URL that points to a detailed web page for the
     *                book.
     */
    public Book(String title, String authors, String url) {
        this.title = title;
        this.authors = authors;
        this.url = url;
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
     * Returns a {@link String} representing the authors that wrote the book.
     *
     * @return {@link String} representing the authors that wrote the book.
     */
    public String getAuthors() {
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
}
