package com.portfolio.library.repository;

import com.portfolio.library.model.Loan;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    long countByMemberIdAndReturnedOnIsNull(Long memberId);
    boolean existsByMemberIdAndBookIdAndReturnedOnIsNull(Long memberId, Long bookId);
    List<Loan> findByReturnedOnIsNullAndDueOnBefore(LocalDate date);
    List<Loan> findByMemberIdOrderByBorrowedOnDesc(Long memberId);
}
