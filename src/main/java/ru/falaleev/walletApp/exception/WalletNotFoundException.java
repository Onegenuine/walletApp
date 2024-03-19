package ru.falaleev.walletApp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(WalletNotFoundException.class);

    public WalletNotFoundException(UUID walletId) {
        super("Wallet with ID " + walletId + " not found");
        logger.error("Wallet with ID {} not found", walletId);
    }
}
