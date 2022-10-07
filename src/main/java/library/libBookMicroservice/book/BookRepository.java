package library.libBookMicroservice.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, String> {

    Page<Book> findByAuthorContaining(String author, Pageable pageable);
    @Query(value = "select * from book where price <= :price", nativeQuery = true)
    Page<Book> findByPriceLowerThen(@Param("price") double price, Pageable pageable);
    Page<Book> findByPriceBetween(double low, double high,  Pageable pageable);
    @Query(value = "select * from book where price >= :price", nativeQuery = true)
    Page<Book> findByPriceGreaterThen(@Param("price") double price, Pageable pageable);
    Page<Book> findByTitleContaining(String title,  Pageable pageable);
    @Query(value = "select * from book where id in (select book_id from book_category where category_id = :categoryId)", nativeQuery = true)
    Page<Book> findByCategory(@Param("categoryId") Integer categoryId, Pageable pageable);
}

