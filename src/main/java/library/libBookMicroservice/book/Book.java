package library.libBookMicroservice.book;

import library.libBookMicroservice.category.Category;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="book")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Book {

    @Id
    @Column
    private String isbn;
    @Column(length = 50)
    private String title;
    @Column
    private String author;
    @Column(length = 500)
    private String sinopse;
    @Column(name="year_release")
    private Integer year;
    @Column
    private Double price;
    @Column
    private Integer stock;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_category",
            joinColumns = { @JoinColumn(name = "book_id") },
            inverseJoinColumns = { @JoinColumn(name = "category_id") })
    private List<Category> categories;

    public void addCategory(Category category){
        categories.add(category);
    }

}
