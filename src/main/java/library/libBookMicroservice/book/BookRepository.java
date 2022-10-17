package library.libBookMicroservice.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, String> {

    Page<Book> findByAuthorContaining(String author, Pageable pageable);
    Page<Book> findByPriceLessThan(double price, Pageable pageable);
    Page<Book> findByPriceBetween(double low, double high,  Pageable pageable);
    Page<Book> findByPriceGreaterThan(double price, Pageable pageable);
    Page<Book> findByTitleContaining(String title,  Pageable pageable);
    Page<Book> findByCategoriesId(Integer id, Pageable pageable);

}

