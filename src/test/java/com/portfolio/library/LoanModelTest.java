package com.portfolio.library;

import static org.assertj.core.api.Assertions.*;

import com.portfolio.library.model.Book;
import com.portfolio.library.model.Loan;
import com.portfolio.library.model.Member;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LoanModelTest {
    private final LocalDate due = LocalDate.of(2025, 3, 15);
    private final Loan loan = new Loan(new Book("9780134685991", "Effective Java", "Joshua Bloch", 2018, 1),
            new Member("Sara", "sara@example.com", LocalDate.of(2025, 1, 1)), due.minusDays(14), due);

    @Test
    void noFineWhenReturnedOnTime() {
        loan.close(due, 20);
        assertThat(loan.getFine()).isZero();
    }

    @Test
    void finePerLateDay() {
        loan.close(due.plusDays(4), 20);
        assertThat(loan.getFine()).isEqualTo(80);
    }

    @Test
    void cannotReturnTwice() {
        loan.close(due, 20);
        assertThatThrownBy(() -> loan.close(due, 20)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void bookCopiesNeverGoNegative() {
        Book b = new Book("9780132350884", "Clean Code", "Robert C. Martin", 2008, 1);
        b.checkOut();
        assertThatThrownBy(b::checkOut).isInstanceOf(IllegalStateException.class);
    }
}
