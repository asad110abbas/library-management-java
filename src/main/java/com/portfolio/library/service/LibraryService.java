package com.portfolio.library.service;

import com.portfolio.library.model.Book;
import com.portfolio.library.model.Loan;
import com.portfolio.library.model.Member;
import com.portfolio.library.repository.BookRepository;
import com.portfolio.library.repository.LoanRepository;
import com.portfolio.library.repository.MemberRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryService {
    private final BookRepository books;
    private final MemberRepository members;
    private final LoanRepository loans;
    private final Clock clock;
    private final int loanDays;
    private final int maxActiveLoans;
    private final long finePerDay;

    public LibraryService(BookRepository books, MemberRepository members, LoanRepository loans, Clock clock,
                          @Value("${library.loan-days}") int loanDays,
                          @Value("${library.max-active-loans}") int maxActiveLoans,
                          @Value("${library.fine-per-day}") long finePerDay) {
        this.books = books;
        this.members = members;
        this.loans = loans;
        this.clock = clock;
        this.loanDays = loanDays;
        this.maxActiveLoans = maxActiveLoans;
        this.finePerDay = finePerDay;
    }

    private LocalDate today() { return LocalDate.now(clock); }

    @Transactional
    public Book addBook(String isbn, String title, String author, int year, int copies) {
        if (books.existsByIsbn(isbn)) throw new BusinessRuleException("ISBN already exists: " + isbn);
        return books.save(new Book(isbn, title, author, year, copies));
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooks(String q) {
        return (q == null || q.isBlank()) ? books.findAll() : books.search(q.trim());
    }

    @Transactional
    public Member addMember(String name, String email) {
        if (members.existsByEmailIgnoreCase(email)) throw new BusinessRuleException("email already registered");
        return members.save(new Member(name, email.toLowerCase(), today()));
    }

    @Transactional
    public Loan borrow(Long memberId, Long bookId) {
        Member member = members.findById(memberId).orElseThrow(() -> new NotFoundException("member " + memberId));
        Book book = books.findById(bookId).orElseThrow(() -> new NotFoundException("book " + bookId));
        if (!member.isActive()) throw new BusinessRuleException("member is inactive");
        if (loans.countByMemberIdAndReturnedOnIsNull(memberId) >= maxActiveLoans)
            throw new BusinessRuleException("loan limit of " + maxActiveLoans + " reached");
        if (loans.existsByMemberIdAndBookIdAndReturnedOnIsNull(memberId, bookId))
            throw new BusinessRuleException("member already has this book");
        if (book.getAvailableCopies() == 0) throw new BusinessRuleException("no copies available");
        if (!loans.findByReturnedOnIsNullAndDueOnBefore(today()).stream()
                .filter(l -> l.getMember().getId().equals(memberId)).toList().isEmpty())
            throw new BusinessRuleException("member has overdue books");
        book.checkOut();
        return loans.save(new Loan(book, member, today(), today().plusDays(loanDays)));
    }

    @Transactional
    public Loan giveBack(Long loanId) {
        Loan loan = loans.findById(loanId).orElseThrow(() -> new NotFoundException("loan " + loanId));
        if (!loan.isOpen()) throw new BusinessRuleException("loan already returned");
        loan.close(today(), finePerDay);
        loan.getBook().checkIn();
        return loan;
    }

    @Transactional(readOnly = true)
    public List<Loan> overdue() {
        return loans.findByReturnedOnIsNullAndDueOnBefore(today());
    }

    @Transactional(readOnly = true)
    public List<Loan> memberHistory(Long memberId) {
        if (!members.existsById(memberId)) throw new NotFoundException("member " + memberId);
        return loans.findByMemberIdOrderByBorrowedOnDesc(memberId);
    }
}
