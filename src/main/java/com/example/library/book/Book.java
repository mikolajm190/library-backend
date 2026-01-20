package com.example.library.book;

import com.example.library.loan.Loan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Formula;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book")
@Check(constraints = "total_copies >= 0")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column
    private String description;

    @Column(nullable = false)
    private int totalCopies;

    @Formula("""
            (
                total_copies -
                (
                    SELECT COUNT(*)
                    FROM loan l
                    WHERE l.book_id = id
                )
            )
            """)
    private int availableCopies;

    @OneToMany(mappedBy = "book")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Loan> loans;
}
