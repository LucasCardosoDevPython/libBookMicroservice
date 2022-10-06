package library.libBookMicroservice.book.v1;

import library.libBookMicroservice.book.Book;
import library.libBookMicroservice.book.BookDTO;
import library.libBookMicroservice.category.CategoryDTO;

import org.springframework.data.domain.Page;

import java.util.List;

interface BookService {
    Page<BookDTO> findAllBooks(int page, int size);
    BookDTO findBookById(String isbn);
    Page<BookDTO> findByPriceLowerThen(double price, int page, int size);
    Page<BookDTO> findByPriceGreaterThen(double price, int page, int size);
    Page<BookDTO> findByPriceBetween(double high, double low, int page, int size);
    Page<BookDTO> findByAuthorContaining(String name, int page, int size);
    Page<BookDTO> findByTitleContaining(String title, int page, int size);
    Page<BookDTO> findByCategory(String categoryName, int page, int size);
    Book save(BookDTO book);
    void delete(String isbn);
    void addCategory(String isbn, List<CategoryDTO> categories);
    void removeCategory(String isbn, List<CategoryDTO> categories);
    void update(String isbn, BookDTO book);
    double findBookByPrice(String isbn);
    boolean isPresent(String isbn);
}
