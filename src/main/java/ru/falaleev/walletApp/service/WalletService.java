package ru.falaleev.walletApp.service;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import ru.falaleev.walletApp.exception.InsufficientBalanceException;
import ru.falaleev.walletApp.exception.WalletNotFoundException;
import ru.falaleev.walletApp.model.Wallet;
import ru.falaleev.walletApp.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    public Wallet getWalletById(UUID walletId) {
        logger.debug("Getting wallet by ID: {}", walletId);
        return walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.error("Wallet with ID {} not found", walletId);
                    return new WalletNotFoundException(walletId);
                });
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Wallet deposit(Wallet wallet, BigDecimal amount) {
        logger.debug("Depositing {} to wallet with ID {}", amount, wallet.getId());
        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Wallet withdraw(Wallet wallet, BigDecimal amount) {
        logger.debug("Withdrawing {} from wallet with ID {}", amount, wallet.getId());
        if (wallet.getBalance().compareTo(amount) < 0) {
            logger.error("Insufficient balance to withdraw {} from wallet with ID {}", amount, wallet.getId());
            throw new InsufficientBalanceException(wallet.getId(), amount, wallet.getBalance());
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet saveWallet(Wallet wallet) {
        logger.debug("Saving wallet: {}", wallet);
        return walletRepository.save(wallet);
    }

    public List<Wallet> getAllWallets() {
        logger.debug("Getting all wallets");
        return walletRepository.findAll();
    }
}
