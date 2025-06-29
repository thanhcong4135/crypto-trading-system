package com.example.cryptotrading.repository;

import com.example.cryptotrading.entity.TradeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TradeTransaction, Long> {
    List<TradeTransaction> findByUserIdOrderByTimestampDesc(Long userId);
}
