package library.libBookMicroservice.book;

import library.libBookMicroservice.book.v1.BookServiceImplementation;
import library.libBookMicroservice.category.CategoryDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
public class BookController {

    private BookServiceImplementation service;

    @GetMapping("/verify/{isbn}")
    public boolean isPresent(@PathVariable("isbn") String isbn){
        return service.isPresent(isbn);
    }

    @GetMapping
    public Page<BookDTO> findAllBooks(Pageable pageable){
        return service.findAllBooks(pageable);
    }

    @GetMapping("/{isbn}")
    public BookDTO findBookById(@PathVariable("isbn") String isbn){
        return service.findBookById(isbn);
    }

    @GetMapping("/price_finder/{isbn}")
    public double findBookPrice(@RequestParam("isbn") String isbn){
        return service.findBookByPrice(isbn);
    }

    @GetMapping("/price")
    public Page<BookDTO> findByPrice(Pageable pageable,
            @RequestParam Optional<Double> low,
            @RequestParam Optional<Double> high){
        if(low.isEmpty() && high.isPresent()){
            return service.findByPriceLowerThen(high.get(), pageable);
        } else if (low.isPresent() && high.isEmpty()) {
            return service.findByPriceGreaterThen(low.get(), pageable);
        } else if (low.isPresent() && high.isPresent()) {
            return service.findByPriceBetween(low.get(), high.get(), pageable);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ao menos um dos limites deve estar presente"
            );
        }

    }

    @GetMapping("/author/{name}")
    public Page<BookDTO> findByAuthorContaining(@PathVariable("name") String name, Pageable pageable){
        return service.findByAuthorContaining(name, pageable);
    }

    @GetMapping("/title/{title}")
    public Page<BookDTO> findByTitleContaining(@PathVariable("title") String title, Pageable pageable){
        return service.findByTitleContaining(title, pageable);
    }

    @GetMapping("/Category/{name}")
    public Page<BookDTO> findByCategory(@PathVariable("name") String categoryName, Pageable pageable){
        return service.findByCategory(categoryName, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String save(@RequestBody BookDTO book){
        return service.save(book).getIsbn();
    }

    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("isbn") String isbn){
        service.delete(isbn);
    }

    @PutMapping("/categories/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCategory(@PathVariable("isbn") String isbn, @RequestBody List<CategoryDTO> categories){
        service.addCategory(isbn, categories);
    }

    @DeleteMapping("/categories/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable("isbn") String isbn, @RequestBody List<CategoryDTO> categories){
        service.removeCategory(isbn, categories);
    }

    @PutMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("isbn") String isbn, @RequestBody BookDTO book){
        service.update(isbn, book);
    }

}
