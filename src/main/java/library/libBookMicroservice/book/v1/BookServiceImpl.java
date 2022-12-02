package library.libBookMicroservice.book.v1;

import library.libBookMicroservice.book.Book;
import library.libBookMicroservice.book.BookDTO;
import library.libBookMicroservice.book.BookRepository;
import library.libBookMicroservice.category.Category;
import library.libBookMicroservice.category.CategoryDTO;
import library.libBookMicroservice.category.CategoryRepository;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService{

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

    private Category fromCategoryDTO(CategoryDTO dto){
        return this.findCategoryByName(dto.getName());
    }
    private static CategoryDTO toCategoryDTO(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
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

    private static BookDTO toBookDTO(Book book){
        LinkedList<CategoryDTO> categories = new LinkedList<CategoryDTO>();

        for(Category c: book.getCategories()){
            categories.add(toCategoryDTO(c));
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

    private Page<BookDTO> fromPage(Page<Book> page){
        return page.map(BookServiceImpl::toBookDTO);
    }

    @Override
    @Transactional
    public Page<BookDTO> findAllBooks(Pageable pageable) {
        return this.fromPage(books.findAll(pageable));
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
    public Page<BookDTO> findByPriceLessThan(double price, Pageable pageable) {
        return this.fromPage(books.findByPriceLessThan(price, pageable));
    }

    @Override
    @Transactional
    public Page<BookDTO> findByPriceGreaterThan(double price, Pageable pageable) {
        return this.fromPage(books.findByPriceGreaterThan(price, pageable));
    }

    @Override
    @Transactional
    public Page<BookDTO> findByPriceBetween(double high, double low, Pageable pageable) {
        return this.fromPage(books.findByPriceBetween(high, low, pageable));
    }

    @Override
    @Transactional
    public Page<BookDTO> findByAuthorContaining(String name, Pageable pageable) {
        return this.fromPage(books.findByAuthorContaining(name, pageable));
    }

    @Override
    @Transactional
    public Page<BookDTO> findByTitleContaining(String title, Pageable pageable) {
        return this.fromPage(books.findByTitleContaining(title, pageable));
    }

    @Override
    @Transactional
    public Page<BookDTO> findByCategory(String categoryName, Pageable pageable) {
        return fromPage(
                books.findByCategoriesId(this.findCategoryByName(categoryName).getId(), pageable)
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
                    for(CategoryDTO c: bookDTO.getCategories()){
                        newCategoryList.add(fromCategoryDTO(c));
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

    @Override
    public List<CategoryDTO> getAllCategories() {
        LinkedList<CategoryDTO> categoryList = new LinkedList<>();
        for(Category c: this.categories.findAll()){
            categoryList.add(this.toCategoryDTO(c));
        }
        return categoryList;
    }
}

