package com.portfolio.library.web.dto;

import com.portfolio.library.model.Loan;
import java.time.LocalDate;

public record LoanView(Long id, Long bookId, String bookTitle, Long memberId, String memberName,
                       LocalDate borrowedOn, LocalDate dueOn, LocalDate returnedOn, long fine) {
    public static LoanView of(Loan l) {
        return new LoanView(l.getId(), l.getBook().getId(), l.getBook().getTitle(), l.getMember().getId(),
                l.getMember().getName(), l.getBorrowedOn(), l.getDueOn(), l.getReturnedOn(), l.getFine());
    }
}
