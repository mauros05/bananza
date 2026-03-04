package com.mauricio.bank.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    Page<TransactionEntity> findByAccountNumber(String accountNumber, Pageable pageable);
}
