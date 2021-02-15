package com.cursor.library.services;

import com.cursor.library.daos.BookDao;
import com.cursor.library.exceptions.BadIdException;
import com.cursor.library.exceptions.BookNameIsNullException;
import com.cursor.library.exceptions.BookNameIsTooLongException;
import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private BookService bookService;
    private BookDao bookDao = new BookDao();

    @BeforeAll
    void setUp() {
        bookDao = Mockito.mock(BookDao.class);
        bookService = new BookService(bookDao);
    }

    @Test
    void getBookByIdSuccessTest() {
        String bookId = "book_id";
        Mockito.when(bookDao.getById(bookId)).thenReturn(new Book(bookId));
        Book bookFromDB = bookService.getById(bookId);

        assertEquals(
                bookId,
                bookFromDB.getBookId()
        );
    }

    @Test
    void getBookByIdBadIdExceptionTest() {
        assertThrows(
                BadIdException.class,
                () -> bookService.getById("   ")
        );

        assertThrows(
                BadIdException.class,
                () -> bookService.getById(null)
        );
    }

    @Test
    void getValidatedBookNameExpectBookNameIsNullExceptionTest() {
        assertThrows(
                BookNameIsNullException.class,
                () -> bookService.getValidatedBookName(null)
        );
    }

    @Test
    void getValidatedBookNameExpectBookNameIsTooLongExceptionTest() {
        StringBuilder subString = new StringBuilder("qwertyuiopasdfghjklzxcbnm");

        for (int i = 0; i < 10; i++) {
            subString.append(subString);
        }
        String name = subString.toString();
        assertThrows(
                BookNameIsTooLongException.class,
                () -> bookService.getValidatedBookName(name)
        );
    }

    @Test
    void getValidatedBookNameTest() {
        String testBookName = "All needed";
        assertEquals(testBookName, bookService.getValidatedBookName("\n    \n    " + testBookName + "   "));
    }

    @Test
    void getCreatedBookTest() {
        CreateBookDto bookDto = new CreateBookDto();
        bookDto.setName("The Twelve Chairs");
        bookDto.setDescription("Classic satirical novel by the Odessa Soviet authors Ilf and Petrov, published in 1928");
        bookDto.setNumberOfWords(100500);
        bookDto.setRating(10);
        bookDto.setYearOfPublication(1929);
        bookDto.setAuthors(Arrays.asList("Ilya Ilf", "Evgeniy Petrov"));

        String name = "The Twelve Chairs";
        assertEquals(name, bookDto.getName());
        String description = "Classic satirical novel by the Odessa Soviet authors Ilf and Petrov, published in 1928";
        assertEquals(description, bookDto.getDescription());
        Iterable<String> authors = Arrays.asList("Ilya Ilf", "Evgeniy Petrov");
        Iterable<String> authorsFromBook = bookDto.getAuthors();
        assertIterableEquals(authors, authorsFromBook);
        int yearOfPublication = 1929;
        assertEquals(yearOfPublication, bookDto.getYearOfPublication());
        int numberOfWords = 100500;
        assertEquals(numberOfWords, bookDto.getNumberOfWords());
        int rating = 10;
        assertEquals(rating, bookDto.getRating());
        Mockito.when(bookService.createBook(bookDto)).thenReturn(null);
        assertNotNull(bookDto);
    }
}
