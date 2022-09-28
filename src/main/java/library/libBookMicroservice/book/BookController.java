package library.libBookMicroservice.book;

import library.libBookMicroservice.book.v1.BookServiceImplementation;
import library.libBookMicroservice.category.CategoryDTO;
import lombok.AllArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping(value = "/book")
@AllArgsConstructor
public class BookController {

    private BookServiceImplementation service;

    @GetMapping
    public List<BookDTO> findAllBooks(){
        return service.findAllBooks();
    }

    @GetMapping("/{isbn}")
    public BookDTO findBookById(@PathVariable("isbn") String isbn){
        return service.findBookById(isbn);
    }

    @GetMapping("/price_finder/{isbn}")
    public double findBookPrice(@RequestParam("isbn") String isbn){
        return service.findBookByPrice(isbn);
    }

    @GetMapping("/price/{price}")
    public List<BookDTO> findByPriceGreaterThenEqual(@PathVariable("price") double price){
        return service.findByPriceGreaterThenEqual(price);
    }

    @GetMapping("/price/{low}/{high}")
    public List<BookDTO> findByPriceBetween(@PathVariable("high") double high, @PathVariable("low") double low){
        return service.findByPriceBetween(high, low);
    }

    @GetMapping("/author/{name}")
    public List<BookDTO> findByAuthorContaining(@PathVariable("name") String name){
        return service.findByAuthorContaining(name);
    }

    @GetMapping("/title/{title}")
    public List<BookDTO> findByTitleContaining(@PathVariable("title") String title){
        return service.findByTitleContaining(title);
    }

    @GetMapping("/Category/{name}")
    public List<BookDTO> findByCategory(@PathVariable("name") String categoryName){
        return service.findByCategory(categoryName);
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

    @PutMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("isbn") String isbn, @RequestBody BookDTO book){
        service.update(isbn, book);
    }

}
