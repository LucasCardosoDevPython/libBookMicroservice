package library.libBookMicroservice.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface BookRepository extends JpaRepository<Book, String> {

    Set<Book> findByTitle(String title);
    Set<Book> findByAuthorContaining(String author);

    @Query(value = "select * from book where price <= :price",nativeQuery = true)
    Set<Book> findByPrice(@Param("price") double price);
    Set<Book> findByPriceBetween(double low, double high);
    Set<Book> findByTitleContaining(String title);

    @Query(value = "select * from book where id in (select book_id from book_category where category_id = :categoryId)", nativeQuery = true)
    Set<Book> findByCategory(@Param("categoryId") Integer categoryId);

}

