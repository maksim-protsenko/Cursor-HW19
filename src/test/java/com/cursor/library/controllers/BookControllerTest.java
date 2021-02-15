package com.cursor.library.controllers;

import com.cursor.library.daos.BookDao;
import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class BookControllerTest extends BaseControllerTest {

    private BookDao bookDao;

    @BeforeAll
    void setUp() {
        bookDao = new BookDao();
    }

    @Test
    @Order(1)
    void getAllTest() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/books");
        MvcResult result1 = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Book> books = OBJECT_MAPPER.readValue(
                result1.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        List<Book> booksFromDb = bookDao.getAll();
        assertIterableEquals(booksFromDb, books);
    }

    @Test
    @Order(2)
    void deleteByIdSuccessTest() throws Exception {
        String bookId = "random_id_value_3";
        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/books/" + bookId);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(3)
    void deleteByIdExpectNotFoundStatusTest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/books/{bookId}", "someId");
        mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(4)
    void getByIdSuccessTest() throws Exception {
        Book bookFromDao = bookDao.getById("random_id_value_3");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/books/" + bookFromDao.getBookId());
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Book book = OBJECT_MAPPER.readValue(
                result.getResponse().getContentAsString(),
                Book.class
        );
        assertEquals(bookFromDao, book);
    }

    @Test
    @Order(5)
    void getByIdExpectNotFoundStatus() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/books/{bookId}", "someId");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Order(6)
    public void createBookTest() throws Exception {
        CreateBookDto createBookDto = new CreateBookDto();
        createBookDto.setName("Cool createBookDto");
        createBookDto.setDescription("Cool description");
        createBookDto.setNumberOfWords(100500);
        createBookDto.setRating(10);
        createBookDto.setYearOfPublication(2020);
        createBookDto.setAuthors(Arrays.asList("author1", "author2"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/books")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(OBJECT_MAPPER.writeValueAsString(createBookDto));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        Book book = OBJECT_MAPPER.readValue(
                result.getResponse().getContentAsString(),
                Book.class
        );
        bookDao.addBook(book);
        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getBookId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
