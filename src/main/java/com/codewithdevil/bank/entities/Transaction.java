package com.codewithdevil.bank.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private TransactionDirection direction;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private CurrencyCode currency;

    @Column(name = "narrative")
    private String narrative;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "balance_after")
    private BigDecimal balanceAfter;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
}
