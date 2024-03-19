package ru.falaleev.walletApp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;


public class InsufficientBalanceException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(InsufficientBalanceException.class);

    public InsufficientBalanceException(UUID walletId, BigDecimal amount, BigDecimal balance) {
        super("Wallet with ID " + walletId + " has insufficient balance " + balance + " to withdraw " + amount);
        logger.error("Wallet with ID {} has insufficient balance {} to withdraw {}", walletId, balance, amount);
    }
}

