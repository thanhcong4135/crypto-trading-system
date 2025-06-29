package com.example.cryptotrading.repository;

import com.example.cryptotrading.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserIdAndCurrency(Long userId, String currency);
    List<Wallet> findByUserId(Long userId);
}
