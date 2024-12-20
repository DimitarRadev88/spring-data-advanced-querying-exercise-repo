package bg.softuni.springDataAdvancedQueryingExercise.repository;

import bg.softuni.springDataAdvancedQueryingExercise.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("""
            FROM Author a
            JOIN Book b ON b.author.id = a.id
            WHERE YEAR(b.releaseDate) < ?1
            """)
    List<Author> findAllByHavingBooksReleasedBeforeYear(int year);

    @Query("""
            FROM Author a
            ORDER BY size(a.books) DESC
            """)
    List<Author> findAllOrderByBooksCountDescending();

    List<Author> findAllByFirstNameEndingWith(String nameEnd);

    @Query("""
            SELECT a, SUM(b.copies)
            FROM Author a
            JOIN a.books b
            GROUP BY a.id
            ORDER BY SUM(b.copies) DESC
            """)
    List<Object[]> findAllAuthorsWithSumOfBookCopiesOrderByBooksCopiesDesc();

}
