package com.example.library.book;

import com.example.library.loan.Loan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book")
@Check(constraints = "available_copies >= 0")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int availableCopies;

    @Column(nullable = false)
    private int copiesOnLoan;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Loan> loans;
}
