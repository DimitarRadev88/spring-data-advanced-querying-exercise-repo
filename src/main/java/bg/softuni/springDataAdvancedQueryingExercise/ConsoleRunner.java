package bg.softuni.springDataAdvancedQueryingExercise;

import bg.softuni.springDataAdvancedQueryingExercise.entity.Author;
import bg.softuni.springDataAdvancedQueryingExercise.entity.Book;
import bg.softuni.springDataAdvancedQueryingExercise.entity.Category;
import bg.softuni.springDataAdvancedQueryingExercise.enums.AgeRestriction;
import bg.softuni.springDataAdvancedQueryingExercise.enums.EditionType;
import bg.softuni.springDataAdvancedQueryingExercise.service.interfaces.AuthorService;
import bg.softuni.springDataAdvancedQueryingExercise.service.interfaces.BookService;
import bg.softuni.springDataAdvancedQueryingExercise.service.interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private static final String RESOURCES_FILES_PATH = "src/main/resources/files/";
    private static final String AUTHORS_FILE_NAME = "authors.txt";
    private static final String CATEGORIES_FILE_NAME = "categories.txt";
    private static final String BOOKS_FILE_NAME = "books.txt";
    private static final Scanner SCANNER = new Scanner(System.in);
    private AuthorService authorService;
    private BookService bookService;
    private CategoryService categoryService;

    @Autowired
    public ConsoleRunner(AuthorService authorService, BookService bookService, CategoryService categoryService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @Override
    public void run(String... args) throws Exception {
//        seedDatabase();
//        printAllBooksReleasedAfterYear(2000);
//        printAllAuthorsFirstAndLastNamesWhoReleasedABookBeforeYear(1990);
//        printAllAuthorsFirstLastNamesAndBookCount();
//        printAllBooksTitlesReleaseDateAndCopiesByAuthor("George", "Powell");
//        booksTitlesByAgeRestriction();
//        goldenBooks();
//        booksByPrice();
//        notReleasedBooks();
//        booksReleasedBeforeDate();
//        authorsSearch();
//        booksSearch();
//        bookTitlesSearch();
//        countBooks();
//        totalBookCopies();
//        reducedBook();
    }

    private void reducedBook() {
        String title = promptInput("Enter book title");

        Book book = bookService.getBookWithTitleEditionTypeAgeRestrictionAndPriceByTitle(title);

        System.out.printf("%s %s %s %.2f%n", book.getTitle(), book.getEditionType().name(), book.getAgeRestriction().name(), book.getPrice());
    }

    private void totalBookCopies() {
        Map<Author, Long> authorSumOfBookCopies = authorService.getAllAuthorsSortedByNumberOfBooksCopies();

        authorSumOfBookCopies
                .forEach((k, v) -> System.out.printf("%s %s - %d%n", k.getFirstName(), k.getLastName(), v));
    }

    private void countBooks() {
        int minTitleLength = Integer.parseInt(promptInput("Enter book title minimum length"));

        long count = bookService.getCountOfBooksWithTitleLengthGreaterThan(minTitleLength);
        System.out.println("There are " + count + " books with longer titles than " + minTitleLength + " symbols");
    }

    private void bookTitlesSearch() {
        String authorName = promptInput("Enter authors' last name starting characters");

        bookService
                .getAllBooksFromAuthorsWithLastNameStartingWith(authorName)
                .forEach(ConsoleRunner::printBookTitleAndAuthorNames);
    }

    private static void printBookTitleAndAuthorNames(Book b) {
        System.out.println(b.getTitle() + "(" + b.getAuthor().getFirstName() + " " + b.getAuthor().getLastName() + ")");
    }

    private void booksSearch() {
        String bookTitleSearchSubstring = promptInput("Enter book title characters");

        bookService
                .getAllBooksWithTitlesContainingSubstring(bookTitleSearchSubstring)
                .forEach(ConsoleRunner::printBookTitle);
    }

    private void authorsSearch() {
        String authorNameEnd = promptInput("Enter author name end");

        authorService
                .getAllAuthorsWithFirstNameEndingWith(authorNameEnd)
                .forEach(ConsoleRunner::printAuthorFirstAndLastName);
    }

    private void booksReleasedBeforeDate() {
        String date = promptInput("Enter date");

        bookService
                .getAllBooksReleasedBeforeDate(date)
                .forEach(b -> System.out.printf("%s %s %.2f%n", b.getTitle(), b.getEditionType(), b.getPrice()));
    }

    private void notReleasedBooks() {
        String year = promptInput("Enter year");

        bookService.getAllBooksWithReleaseYearNotEqualTo(year).forEach(ConsoleRunner::printBookTitle);
    }

    private void booksByPrice() {
        BigDecimal toPrice = new BigDecimal("5");
        BigDecimal fromPrice = new BigDecimal("40");

        bookService
                .getAllBooksWithPriceLessThanAndPriceHigherThan(toPrice, fromPrice)
                .forEach(b -> System.out.printf("%s - $%.2f%n", b.getTitle(), b.getPrice()));


    }

    private void goldenBooks() {
        EditionType editionType = EditionType.GOLD;
        int maxNumberOfCopies = 5000;

        bookService
                .getAllBooksWithEditionAndWithNumberOfCopiesLessThan(editionType, maxNumberOfCopies)
                .forEach(ConsoleRunner::printBookTitle);

    }

    private void booksTitlesByAgeRestriction() {
        AgeRestriction ageRestriction = AgeRestriction.valueOf(promptInput("Age restriction ").toUpperCase());
        bookService.getAllBooksWithAgeRestriction(ageRestriction).forEach(ConsoleRunner::printBookTitle);

    }

    private static String promptInput(String promptMessage) {
        System.out.print(promptMessage + ": ");
        return SCANNER.nextLine();
    }

    private void printAllBooksTitlesReleaseDateAndCopiesByAuthor(String firstName, String lastName) {
        bookService
                .getAllBooksFromAuthorOrderedByReleaseDateDescendingAndBookTitileAscending(firstName, lastName)
                .forEach(b -> System.out.println(b.getTitle() + " " + b.getReleaseDate() + " " + b.getCopies()));
    }

    private void printAllAuthorsFirstLastNamesAndBookCount() {
        List<Author> authors = authorService.getAllAuthorsOrderedByCountOfBooksDescending();
        authors.forEach(a -> System.out.println(a.getFirstName() + " " + a.getLastName() + " " + a.getBooks().size()));
    }

    private void printAllAuthorsFirstAndLastNamesWhoReleasedABookBeforeYear(int year) {
        List<Author> authors = authorService.getAllAuthorsWithBooksReleasedBefore(year);

        authors.forEach(ConsoleRunner::printAuthorFirstAndLastName);
    }

    private void printAllBooksReleasedAfterYear(int year) {
        List<Book> books = bookService.getAllBooksReleasedAfterYear(year);
        System.out.println(books.size());
        books.forEach(ConsoleRunner::printBookTitle);
    }

    private void seedDatabase() throws IOException {
        seedAuthors();
        seedCategories();
        seedBooks();
    }

    private void seedBooks() throws IOException {
        List<Book> books = parseBooksFromBooksFile();

        bookService.addAll(books);
    }

    private List<Book> parseBooksFromBooksFile() throws IOException {
        List<String> booksInfo = Files.readAllLines(Path.of(RESOURCES_FILES_PATH + BOOKS_FILE_NAME));

        return booksInfo.stream()
                .map(row -> row.split(" "))
                .map(this::parseBook).toList();
    }

    private Book parseBook(String[] bookInfo) {
        String title = parseBookTitle(bookInfo);
        LocalDate releaseDate = getReleaseDate(bookInfo);
        BigDecimal price = new BigDecimal(bookInfo[3]);
        EditionType editionType = getEditionType(bookInfo);
        int copies = Integer.parseInt(bookInfo[2]);
        AgeRestriction ageRestriction = getAgeRestriction(bookInfo);
        Set<Category> categories = categoryService.getRandomCategories();
        Author author = authorService.getRandomAuthor();

        return new Book(title, releaseDate, price, editionType, copies, ageRestriction, categories, author);
    }

    private static EditionType getEditionType(String[] bookInfo) {
        return EditionType.values()[Integer.parseInt(bookInfo[0])];
    }

    private static LocalDate getReleaseDate(String[] bookInfo) {
        return LocalDate.parse(bookInfo[1], DateTimeFormatter.ofPattern("d/M/yyyy"));
    }

    private static AgeRestriction getAgeRestriction(String[] bookInfo) {
        return AgeRestriction.values()[Integer.parseInt(bookInfo[4])];
    }

    private static String parseBookTitle(String[] row) {
        return Arrays.stream(row).skip(5).collect(Collectors.joining(" "));
    }

    private void seedCategories() throws IOException {
        List<Category> categories = parseCategoriesFromCategoriesFile();
        categoryService.addAll(categories);
    }

    private List<Category> parseCategoriesFromCategoriesFile() throws IOException {
        List<String> categories = Files.readAllLines(Path.of(RESOURCES_FILES_PATH + CATEGORIES_FILE_NAME));
        return categories.stream().map(Category::new).toList();
    }

    private void seedAuthors() throws IOException {
        List<Author> authors = parseAuthorsFromAuthorsFile();

        authors.forEach(System.out::println);

        authorService.addAll(authors);
    }

    private List<Author> parseAuthorsFromAuthorsFile() throws IOException {
        List<String> authorNames = Files.readAllLines(Path.of(RESOURCES_FILES_PATH + AUTHORS_FILE_NAME));

        return authorNames.stream()
                .map(line -> line.split(" "))
                .map(info -> new Author(info[0], info[1]))
                .toList();
    }

    private static void printAuthorFirstAndLastName(Author a) {
        System.out.println(a.getFirstName() + " " + a.getLastName());
    }

    private static void printBookTitle(Book b) {
        System.out.println(b.getTitle());
    }

}
