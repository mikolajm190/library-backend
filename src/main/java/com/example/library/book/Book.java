package com.example.library.book;

import com.example.library.loan.Loan;
import com.example.library.reservation.Reservation;
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
@Check(constraints = "total_copies > 0")
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
                ) -
                (
                    SELECT COUNT(*)
                    FROM reservation r
                    WHERE r.book_id = id
                    AND r.status = 'READY'
                    AND r.expires_at > CURRENT_TIMESTAMP
                )
            )
            """)
    private int availableCopies;

    @Formula("""
            (
                SELECT COUNT(*)
                FROM reservation r
                WHERE r.book_id = id
                AND r.status = 'QUEUED'
            )
            """)
    private int queueSize;

    @OneToMany(mappedBy = "book")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Reservation> reservations;

    @OneToMany(mappedBy = "book")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Loan> loans;
}
