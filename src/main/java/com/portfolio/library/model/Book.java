package com.portfolio.library.model;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "isbn"))
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String isbn;
    @Column(nullable = false) private String title;
    @Column(nullable = false) private String author;
    private int publishedYear;
    private int totalCopies;
    private int availableCopies;

    protected Book() {}

    public Book(String isbn, String title, String author, int publishedYear, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.totalCopies = copies;
        this.availableCopies = copies;
    }

    public void checkOut() {
        if (availableCopies <= 0) throw new IllegalStateException("no copies available");
        availableCopies--;
    }

    public void checkIn() {
        if (availableCopies >= totalCopies) throw new IllegalStateException("all copies already returned");
        availableCopies++;
    }

    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPublishedYear() { return publishedYear; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
}
