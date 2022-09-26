package library.libBookMicroservice.book.v1;

import library.libBookMicroservice.book.Book;
import library.libBookMicroservice.book.BookDTO;
import library.libBookMicroservice.book.BookRepository;
import library.libBookMicroservice.category.Category;
import library.libBookMicroservice.category.CategoryDTO;
import library.libBookMicroservice.category.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookServiceImplementation implements BookService{

    private BookRepository books;
    private CategoryRepository categories;

    private Category findCategoryByName(String name){
        return categories
                .findByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não foi encontrada nehnuma categoria " + name + " na base de dados."
                ));
    }

    private CategoryDTO toCategoryDTO(Category category){
        return CategoryDTO.builder()
                .name(category.getName())
                .build();
    }

    private Category fromCategoryDTO(CategoryDTO dto){
        return this.findCategoryByName(dto.getName());
    }

    private Book fromBookDTO(BookDTO dto){
        LinkedList<Category> categories = new LinkedList<Category>();

        for(CategoryDTO c: dto.getCategories()){
            categories.add(this.fromCategoryDTO(c));
        }

        return Book.builder()
                .isbn(dto.getIsbn())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .year(dto.getYear())
                .sinopse(dto.getSinopse())
                .categories(categories)
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
    }

    private BookDTO toBookDTO(Book book){
        LinkedList<CategoryDTO> categories = new LinkedList<CategoryDTO>();

        for(Category c: book.getCategories()){
            categories.add(this.toCategoryDTO(c));
        }

        return BookDTO.builder()
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .year(book.getYear())
                .sinopse(book.getSinopse())
                .categories(categories)
                .price(book.getPrice())
                .stock(book.getStock())
                .build();
    }

    private List<BookDTO> books2DTOs(Collection<Book> bookList){
        LinkedList<BookDTO> all = new LinkedList<BookDTO>();

        for(Book b: bookList){
            all.add(this.toBookDTO(b));
        }

        return all;
    }

    @Override
    @Transactional
    public List<BookDTO> findAllBooks() {
        return this.books2DTOs(books.findAll());
    }

    @Override
    @Transactional
    public BookDTO findBookById(String isbn) {
        return toBookDTO(books.findById(isbn)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não foi encontrado nehnum livro com o isbn " + isbn + " na base de dados.")
                )
        );
    }

    @Override
    @Transactional
    public List<BookDTO> findByPriceGreaterThenEqual(double price) {
        return this.books2DTOs(books.findByPriceGreaterThenEqual(price));
    }

    @Override
    @Transactional
    public List<BookDTO> findByPriceBetween(double high, double low) {
        return this.books2DTOs(books.findByPriceBetween(high, low));
    }

    @Override
    @Transactional
    public List<BookDTO> findByAuthorContaining(String name) {
        return this.books2DTOs(books.findByAuthorContaining(name));
    }

    @Override
    @Transactional
    public List<BookDTO> findByTitleContaining(String title) {
        return this.books2DTOs(books.findByTitleContaining(title));
    }

    @Override
    @Transactional
    public List<BookDTO> findByCategory(String categoryName) {
        return this.books2DTOs(
                books.findByCategory(this.findCategoryByName(categoryName).getId())
        );
    }

    @Override
    @Transactional
    public Book save(BookDTO book) {
        return books.save(this.fromBookDTO(book));
    }

    @Override
    @Transactional
    public void delete(String isbn) {
        books
                .findById(isbn)
                .map(book -> {
                    books.delete(book);
                    return book;
                }).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não foi encontrado nehnum livro com o isbn " + isbn + " na base de dados."
                ));
    }

    @Override
    @Transactional
    public void addCategory(String isbn, List<CategoryDTO> categories) {
        Book book = books.findById(isbn)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não foi encontrado nehnum livro com o isbn " + isbn + " na base de dados.")
                );

        for(CategoryDTO c: categories){
            if(!book.getCategories().contains(c)){
                book.addCategory(this.fromCategoryDTO(c));
            }
        }

        books.save(book);
    }

    @Override
    @Transactional
    public void update(String isbn, BookDTO book) {
        books
                .findById(isbn)
                .map(existentBook -> {
                    book.setIsbn(existentBook.getIsbn());
                    books.save(this.fromBookDTO(book));
                    return existentBook;
                }).orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não foi encontrado nehnum livro com o isbn " + isbn + " na base de dados."
                ));
    }
}

