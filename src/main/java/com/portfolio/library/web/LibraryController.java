package com.portfolio.library.web;

import com.portfolio.library.model.Book;
import com.portfolio.library.model.Member;
import com.portfolio.library.service.LibraryService;
import com.portfolio.library.web.dto.LoanView;
import com.portfolio.library.web.dto.Requests;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LibraryController {
    private final LibraryService service;

    public LibraryController(LibraryService service) { this.service = service; }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@Valid @RequestBody Requests.NewBook r) {
        return service.addBook(r.isbn(), r.title(), r.author(), r.publishedYear(), r.copies());
    }

    @GetMapping("/books")
    public List<Book> books(@RequestParam(required = false) String q) { return service.searchBooks(q); }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public Member addMember(@Valid @RequestBody Requests.NewMember r) { return service.addMember(r.name(), r.email()); }

    @GetMapping("/members/{id}/loans")
    public List<LoanView> history(@PathVariable Long id) {
        return service.memberHistory(id).stream().map(LoanView::of).toList();
    }

    @PostMapping("/loans")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanView borrow(@Valid @RequestBody Requests.NewLoan r) {
        return LoanView.of(service.borrow(r.memberId(), r.bookId()));
    }

    @PostMapping("/loans/{id}/return")
    public LoanView giveBack(@PathVariable Long id) { return LoanView.of(service.giveBack(id)); }

    @GetMapping("/loans/overdue")
    public List<LoanView> overdue() { return service.overdue().stream().map(LoanView::of).toList(); }
}
