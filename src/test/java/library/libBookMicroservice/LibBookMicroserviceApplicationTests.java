package library.libBookMicroservice;

import library.libBookMicroservice.book.Book;
import library.libBookMicroservice.book.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class LibBookMicroserviceApplicationTests {
	@Autowired
	private BookRepository repositorioLivros;

	@Test
	void bookTest(){
		Book livro = Book.builder() // 'Nata Mateus', 2002, 783.25, 1351
				.isbn("9584991804130")
				.author("Nata Mateus")
				.price(783.25)
				.stock(1351)
				.title("Funeral Parade of Roses")
				.year(2002)
				.build();

		Book livroCadastrado = repositorioLivros.save(livro);
		Optional<Book> livroPesquisado = repositorioLivros.findById(livroCadastrado.getIsbn());

		if(livroPesquisado.isEmpty()){
			fail("Libro não encontrado quando deveria ter sido cadastrado");
		}else{
			assertEquals(livroCadastrado.getIsbn(),livroPesquisado.get().getIsbn());
			assertEquals(livroCadastrado.getAuthor(),livroPesquisado.get().getAuthor());
			assertEquals(livroCadastrado.getPrice(),livroPesquisado.get().getPrice());
			assertEquals(livroCadastrado.getStock(),livroPesquisado.get().getStock());
			assertEquals(livroCadastrado.getTitle(),livroPesquisado.get().getTitle());
			assertEquals(livroCadastrado.getYear(),livroPesquisado.get().getYear());
		}

		livro.setSinopse("Um Bom Libro.");

		Book livroAtualizado = repositorioLivros.save(livro);
		Optional<Book> livroPesquisado2 = repositorioLivros.findById(livroCadastrado.getIsbn());

		if(livroPesquisado2.isEmpty()){
			fail("Livro não encontrado quando deveria ter sido cadastrado");
		}else{
			assertEquals(livroAtualizado.getIsbn(),livroPesquisado2.get().getIsbn());
			assertEquals(livroAtualizado.getAuthor(),livroPesquisado2.get().getAuthor());
			assertEquals(livroAtualizado.getPrice(),livroPesquisado2.get().getPrice());
			assertEquals(livroAtualizado.getStock(),livroPesquisado2.get().getStock());
			assertEquals(livroAtualizado.getTitle(),livroPesquisado2.get().getTitle());
			assertEquals(livroAtualizado.getYear(),livroPesquisado2.get().getYear());
			assertEquals("Um Bom Libro.",livroPesquisado2.get().getSinopse());
		}

		repositorioLivros.delete(repositorioLivros.findById(livroAtualizado.getIsbn()).get());

		if(!repositorioLivros.findById(livroAtualizado.getIsbn()).isEmpty()){
			fail("O Livro deveria ter sido deletado.");
		}
	}

}
