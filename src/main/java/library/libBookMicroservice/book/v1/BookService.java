package library.libBookMicroservice.book.v1;

import library.libBookMicroservice.book.Book;
import library.libBookMicroservice.book.BookDTO;
import library.libBookMicroservice.category.CategoryDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

interface BookService {
    Page<BookDTO> findAllBooks(Pageable pageable);
    BookDTO findBookById(String isbn);
    Page<BookDTO> findByPriceLessThan(double price, Pageable pageable);
    Page<BookDTO> findByPriceGreaterThan(double price, Pageable pageable);
    Page<BookDTO> findByPriceBetween(double high, double low, Pageable pageable);
    Page<BookDTO> findByAuthorContaining(String name, Pageable pageable);
    Page<BookDTO> findByTitleContaining(String title, Pageable pageable);
    Page<BookDTO> findByCategory(String categoryName, Pageable pageable);
    Book save(BookDTO book);
    void delete(String isbn);
    void addCategory(String isbn, List<CategoryDTO> categories);
    void removeCategory(String isbn, List<CategoryDTO> categories);
    void update(String isbn, BookDTO book);
    double findBookByPrice(String isbn);
    boolean isPresent(String isbn);
}
