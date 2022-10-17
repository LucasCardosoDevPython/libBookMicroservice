package library.libBookMicroservice;

import library.libBookMicroservice.book.BookController;
import library.libBookMicroservice.book.v1.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@Tag("cotroller")
@DisplayName("Valida os endpoints relacionados a livros")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookController.class)
class LibBookMicroserviceControllerTests {

	@MockBean
	private BookServiceImpl bookService;
	@Autowired
	private MockMvc mockMvc;

	private static String readJson(String file) throws Exception {
		byte[] bytes = Files.readAllBytes(
				Paths.get("src/test/resources/requestJsons/" + file).toAbsolutePath());
		return new String(bytes);
	}

	@Test
	@DisplayName("Deve tentar buscar por libros pelo preço, mas falhar por não possuir parâmetros")
	void shouldNotLookForBookByPrice() throws Exception{
		mockMvc.perform(get("/book/price"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Deve tentar buscar por libros pelo menor preço e retornar totos acima dele")
	void shouldLookForBookByPriceGreaterthen() throws Exception{
		when(bookService.findByPriceGreaterThan(eq(300.0), any(Pageable.class)))
				.thenReturn(Page.empty());
		mockMvc.perform(get("/book/price?low=300"))
				.andExpect(status().isOk());
		verify(bookService, times(1)).
				findByPriceGreaterThan(
						eq(300.0),
						any(Pageable.class)
				);
	}

	@Test
	@DisplayName("Deve tentar buscar por libros pelo maior preço e retornar os preços abaixo dele")
	void shouldLookForBookByPriceLowerThen() throws Exception{
		when(bookService.findByPriceLessThan(eq(300.0), any(Pageable.class)))
				.thenReturn(Page.empty());
		mockMvc.perform(get("/book/price?high=300"))
				.andExpect(status().isOk());
		verify(bookService, times(1)).
				findByPriceLessThan(
						eq(300.0),
						any(Pageable.class)
				);
	}

	@Test
	@DisplayName("Deve tentar buscar por libros pelo maior e menor preço e retornar o intervalo entre os dois")
	void shouldNotLookForBookByPriceBetween() throws Exception{
		when(bookService.findByPriceBetween(eq(300.0), eq(500.0), any(Pageable.class)))
				.thenReturn(Page.empty());
		mockMvc.perform(get("/book/price?low=300&high=500"))
				.andExpect(status().isOk());
		verify(bookService, times(1)).
				findByPriceBetween(
						eq(300.0),
						eq(500.0),
						any(Pageable.class)
				);
	}

}
