package behavioral.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Demo class showing Iterator pattern in action
 */
public class IteratorDemo {
    public static void main(String[] args) {
        System.out.println("=== Iterator Pattern Demo ===\n");

        System.out.println("--- Book Collection Example ---");
        BookCollection books = new BookCollection();
        books.addBook(new Book("Design Patterns", "Gang of Four"));
        books.addBook(new Book("Clean Code", "Robert Martin"));
        books.addBook(new Book("Refactoring", "Martin Fowler"));

        Iterator<Book> iterator = books.createIterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
        }

        System.out.println("\n--- Social Network Example ---");
        SocialNetwork network = new FacebookNetwork();
        SocialProfile profile = new SocialProfile("john@example.com", "John Doe");

        System.out.println("Friends:");
        Iterator<SocialProfile> friendsIterator = network.getFriends(profile);
        while (friendsIterator.hasNext()) {
            SocialProfile friend = friendsIterator.next();
            System.out.println("- " + friend.getName());
        }

        System.out.println("\nCoworkers:");
        Iterator<SocialProfile> coworkersIterator = network.getCoworkers(profile);
        while (coworkersIterator.hasNext()) {
            SocialProfile coworker = coworkersIterator.next();
            System.out.println("- " + coworker.getName());
        }
    }
}

// Iterator interface
interface Iterator<T> {
    boolean hasNext();
    T next();
}

// Aggregate interface
interface Collection<T> {
    Iterator<T> createIterator();
}

// Book class
class Book {
    private String title;
    private String author;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
}

// Concrete Iterator
class BookIterator implements Iterator<Book> {
    private List<Book> books;
    private int position = 0;

    public BookIterator(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean hasNext() {
        return position < books.size();
    }

    @Override
    public Book next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return books.get(position++);
    }
}

// Concrete Collection
class BookCollection implements Collection<Book> {
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    @Override
    public Iterator<Book> createIterator() {
        return new BookIterator(books);
    }
}

// Social Network example
class SocialProfile {
    private String email;
    private String name;

    public SocialProfile(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
}

interface SocialNetwork {
    Iterator<SocialProfile> getFriends(SocialProfile profile);
    Iterator<SocialProfile> getCoworkers(SocialProfile profile);
}

class FacebookNetwork implements SocialNetwork {
    private List<SocialProfile> mockFriends;
    private List<SocialProfile> mockCoworkers;

    public FacebookNetwork() {
        mockFriends = new ArrayList<>();
        mockFriends.add(new SocialProfile("alice@example.com", "Alice"));
        mockFriends.add(new SocialProfile("bob@example.com", "Bob"));

        mockCoworkers = new ArrayList<>();
        mockCoworkers.add(new SocialProfile("charlie@example.com", "Charlie"));
        mockCoworkers.add(new SocialProfile("david@example.com", "David"));
    }

    @Override
    public Iterator<SocialProfile> getFriends(SocialProfile profile) {
        return new ProfileIterator(mockFriends);
    }

    @Override
    public Iterator<SocialProfile> getCoworkers(SocialProfile profile) {
        return new ProfileIterator(mockCoworkers);
    }
}

class ProfileIterator implements Iterator<SocialProfile> {
    private List<SocialProfile> profiles;
    private int position = 0;

    public ProfileIterator(List<SocialProfile> profiles) {
        this.profiles = profiles;
    }

    @Override
    public boolean hasNext() {
        return position < profiles.size();
    }

    @Override
    public SocialProfile next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return profiles.get(position++);
    }
}
