package library.libBookMicroservice.book;

import library.libBookMicroservice.book.v1.BookServiceImpl;
import library.libBookMicroservice.category.CategoryDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/book")
@AllArgsConstructor
@CrossOrigin("http://localhost:4200")
public class BookController {

    private BookServiceImpl bookService;

    @GetMapping("/verify/{isbn}")
    public boolean isPresent(@PathVariable("isbn") String isbn){
        return bookService.isPresent(isbn);
    }

    @GetMapping
    public Page<BookDTO> findAllBooks(Pageable pageable){
        return bookService.findAllBooks(pageable);
    }

    @GetMapping("/{isbn}")
    public BookDTO findBookById(@PathVariable("isbn") String isbn){
        return bookService.findBookById(isbn);
    }

    @GetMapping("/price_finder/{isbn}")
    public double findBookPrice(@RequestParam("isbn") String isbn){
        return bookService.findBookByPrice(isbn);
    }

    @GetMapping("/price")
    public Page<BookDTO> findByPrice(Pageable pageable,
            @RequestParam Optional<Double> low,
            @RequestParam Optional<Double> high){
        if(low.isEmpty() && high.isPresent()){
            return bookService.findByPriceLessThan(high.get(), pageable);
        } else if (low.isPresent() && high.isEmpty()) {
            return bookService.findByPriceGreaterThan(low.get(), pageable);
        } else if (low.isPresent() && high.isPresent()) {
            return bookService.findByPriceBetween(low.get(), high.get(), pageable);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ao menos um dos limites deve estar presente"
            );
        }

    }

    @GetMapping("/author/{name}")
    public Page<BookDTO> findByAuthorContaining(@PathVariable("name") String name, Pageable pageable){
        return bookService.findByAuthorContaining(name, pageable);
    }

    @GetMapping("/title/{title}")
    public Page<BookDTO> findByTitleContaining(@PathVariable("title") String title, Pageable pageable){
        return bookService.findByTitleContaining(title, pageable);
    }

    @GetMapping("/category/{name}")
    public Page<BookDTO> findByCategory(@PathVariable("name") String categoryName, Pageable pageable){
        return bookService.findByCategory(categoryName, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@RequestBody BookDTO book){
        return bookService.save(book).getIsbn();
    }

    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("isbn") String isbn){
        bookService.delete(isbn);
    }

    @PutMapping("/categories/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCategory(@PathVariable("isbn") String isbn, @RequestBody List<CategoryDTO> categories){
        bookService.addCategory(isbn, categories);
    }

    @DeleteMapping("/categories/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable("isbn") String isbn, @RequestBody List<CategoryDTO> categories){
        bookService.removeCategory(isbn, categories);
    }

    @PutMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("isbn") String isbn, @RequestBody BookDTO book){
        bookService.update(isbn, book);
    }

    @GetMapping("/categories")
    public List<CategoryDTO> getAllCategories(){
        return this.bookService.getAllCategories();
    }
}
