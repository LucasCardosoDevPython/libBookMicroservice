package library.libBookMicroservice.book.v1;

import library.libBookMicroservice.book.Book;
import library.libBookMicroservice.book.BookDTO;
import library.libBookMicroservice.book.BookRepository;
import library.libBookMicroservice.category.Category;
import library.libBookMicroservice.category.CategoryDTO;
import library.libBookMicroservice.category.CategoryRepository;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

        for(String c: dto.getCategories()){
            categories.add(this.fromCategoryDTO(new CategoryDTO(c)));
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
        LinkedList<String> categories = new LinkedList<String>();

        for(Category c: book.getCategories()){
            categories.add(c.getName());
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
    public Page<BookDTO> findAllBooks(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
            page,
            size);
        return new PageImpl<>(
                this.books2DTOs(books.findAll()),
                pageRequest, size);
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
    public Page<BookDTO> findByPriceLowerThen(double price, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size);
        return new PageImpl<>(
                this.books2DTOs(books.findByPriceLowerThen(price)),
                pageRequest, size);
    }

    @Override
    @Transactional
    public Page<BookDTO> findByPriceGreaterThen(double price, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size);
        return new PageImpl<>(
                this.books2DTOs(books.findByPriceGreaterThen(price)),
                pageRequest, size);
    }

    @Override
    @Transactional
    public Page<BookDTO> findByPriceBetween(double high, double low, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size);
        return new PageImpl<>(
                this.books2DTOs(books.findByPriceBetween(high, low)),
                pageRequest, size);
    }

    @Override
    @Transactional
    public Page<BookDTO> findByAuthorContaining(String name, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size);
        return new PageImpl<>(
                this.books2DTOs(books.findByAuthorContaining(name)),
                pageRequest, size);
    }

    @Override
    @Transactional
    public Page<BookDTO> findByTitleContaining(String title, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size);
        return new PageImpl<>(
                this.books2DTOs(books.findByTitleContaining(title)),
                pageRequest, size);
    }

    @Override
    @Transactional
    public Page<BookDTO> findByCategory(String categoryName, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size);
        return new PageImpl<>(
                this.books2DTOs(
                        books.findByCategory(this.findCategoryByName(categoryName).getId())
                ),
                pageRequest, size);
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
            Category category = this.fromCategoryDTO(c);
            if(!book.getCategories().contains(category)){
                book.addCategory(category);
            }
        }

        books.save(book);
    }

    @Override
    public void removeCategory(String isbn, List<CategoryDTO> categories) {
        Book book = books.findById(isbn)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não foi encontrado nehnum livro com o isbn " + isbn + " na base de dados.")
                );

        for(CategoryDTO c: categories){
            Category category = this.fromCategoryDTO(c);
            if(book.getCategories().contains(category)){
                book.removeCategory(category);
            }else{
                books.save(book);
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "O livro " + book.getTitle() + " não possue a categoria " + c.getName());
            }
        }

        books.save(book);

    }

    @Override
    @Transactional
    public void update(String isbn, BookDTO bookDTO) {
        books
                .findById(isbn)
                .map(existentBook -> {
                    bookDTO.setIsbn(existentBook.getIsbn());
                    Book book = this.fromBookDTO(bookDTO);
                    LinkedList<Category> newCategoryList = new LinkedList<Category>();
                    for(String c: bookDTO.getCategories()){
                        newCategoryList.add(this.findCategoryByName(c));
                    }
                    book.setCategories(newCategoryList);
                    books.save(book);
                    return existentBook;
                }).orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não foi encontrado nehnum livro com o isbn " + isbn + " na base de dados."
                ));
    }

    @Override
    public double findBookByPrice(String isbn) {
        return this.findBookById(isbn).getPrice();
    }

    @Override
    public boolean isPresent(String isbn) {
        return books.findById(isbn).isPresent();
    }
}

