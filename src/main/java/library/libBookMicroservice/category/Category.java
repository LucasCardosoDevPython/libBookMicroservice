package library.libBookMicroservice.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Category {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CategorySeq")
    @SequenceGenerator(name = "CaregorySeq", sequenceName = "category_id_generator", allocationSize = 1)
    private Integer id;
    @Column(length = 20)
    private String name;

}