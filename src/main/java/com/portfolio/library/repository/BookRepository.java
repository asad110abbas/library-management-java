package com.portfolio.library.repository;

import com.portfolio.library.model.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    @Query("select b from Book b where lower(b.title) like lower(concat('%', :q, '%')) "
         + "or lower(b.author) like lower(concat('%', :q, '%')) order by b.title")
    List<Book> search(@Param("q") String q);
}
