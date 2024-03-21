package ru.falaleev.walletApp.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.falaleev.walletApp.dto.ErrorResponse;
import ru.falaleev.walletApp.exception.InsufficientBalanceException;
import ru.falaleev.walletApp.exception.WalletNotFoundException;
import ru.falaleev.walletApp.model.OperationType;
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
    public Wallet deposit(Wallet wallet, BigDecimal amount) {
        logger.debug("Depositing {} to wallet with ID {}", amount, wallet.getId());
        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    @Transactional
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

    @Transactional
    public Wallet getWalletOrHandleNotFoundException(UUID walletId) {
        try {
            return  walletRepository.findByIdWithPessimisticWriteLock(walletId);
        } catch (WalletNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Transactional
    public Wallet performOperation(Wallet wallet, OperationType operationType, BigDecimal amount) throws InsufficientBalanceException {
        if (operationType == OperationType.DEPOSIT) {
            return deposit(wallet, amount);
        } else {
            return withdraw(wallet, amount);
        }
    }

    public ResponseEntity<?> handleBadRequest(String errorMessage) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    public ResponseEntity<?> handleNotFoundException(WalletNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }
}
