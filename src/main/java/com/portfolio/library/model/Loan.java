package com.portfolio.library.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Loan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) private Book book;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) private Member member;
    private LocalDate borrowedOn;
    private LocalDate dueOn;
    private LocalDate returnedOn;
    private long fine;

    protected Loan() {}

    public Loan(Book book, Member member, LocalDate borrowedOn, LocalDate dueOn) {
        this.book = book;
        this.member = member;
        this.borrowedOn = borrowedOn;
        this.dueOn = dueOn;
    }

    /** Days late as of {@code date} (0 if not late). */
    public long daysLate(LocalDate date) {
        return Math.max(0, ChronoUnit.DAYS.between(dueOn, date));
    }

    public void close(LocalDate on, long finePerDay) {
        if (returnedOn != null) throw new IllegalStateException("loan already returned");
        returnedOn = on;
        fine = daysLate(on) * finePerDay;
    }

    public boolean isOpen() { return returnedOn == null; }
    public Long getId() { return id; }
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getBorrowedOn() { return borrowedOn; }
    public LocalDate getDueOn() { return dueOn; }
    public LocalDate getReturnedOn() { return returnedOn; }
    public long getFine() { return fine; }
}
