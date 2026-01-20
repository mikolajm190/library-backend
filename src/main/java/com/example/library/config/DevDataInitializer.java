package com.example.library.config;

import com.example.library.book.Book;
import com.example.library.book.BookRepository;
import com.example.library.loan.Loan;
import com.example.library.loan.LoanRepository;
import com.example.library.user.User;
import com.example.library.user.UserRepository;
import com.example.library.user.constants.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<User> users = List.of(
                User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build(),
                User.builder()
                        .username("user1")
                        .password(passwordEncoder.encode("pass1"))
                        .role(Role.USER)
                        .build(),
                User.builder()
                        .username("user2")
                        .password(passwordEncoder.encode("pass2"))
                        .role(Role.USER)
                        .build()
        );

        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            int totalCopies = i < 2 ? 3 : 1;
            Book book = Book.builder()
                    .title("Title" + i)
                    .author("Author" + i)
                    .totalCopies(totalCopies)
                    .build();
            books.add(book);
        }

        userRepository.saveAll(users);
        bookRepository.saveAll(books);

        User user1 = userRepository.findByUsername("user1")
                .orElseThrow(EntityNotFoundException::new);
        Book book1 = bookRepository.findByTitle("Title0")
                .orElseThrow(EntityNotFoundException::new);

        User user2 = userRepository.findByUsername("user2")
                .orElseThrow(EntityNotFoundException::new);
        Book book2 = bookRepository.findByTitle("Title1")
                .orElseThrow(EntityNotFoundException::new);

        LocalDateTime currentDate = LocalDateTime.now();
        List<Loan> loans = List.of(
                Loan.builder()
                        .user(user1)
                        .book(book1)
                        .borrowDate(currentDate.minusDays(15))
                        .returnDate(currentDate.minusDays(15).plusMonths(1))
                        .build(),
                Loan.builder()
                        .user(user2)
                        .book(book2)
                        .borrowDate(currentDate)
                        .returnDate(currentDate.plusMonths(1))
                        .build()
        );

        loanRepository.saveAll(loans);
    }
}