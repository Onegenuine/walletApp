package ru.falaleev.walletApp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.falaleev.walletApp.dto.ErrorResponse;
import ru.falaleev.walletApp.dto.WalletOperationRequestDto;
import ru.falaleev.walletApp.exception.InsufficientBalanceException;
import ru.falaleev.walletApp.exception.WalletNotFoundException;
import ru.falaleev.walletApp.model.OperationType;
import ru.falaleev.walletApp.model.Wallet;
import ru.falaleev.walletApp.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;


@RestController
public class WalletController {
    private final WalletService walletService;
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/api/v1/wallet")
    public ResponseEntity<?> processWalletOperation(@RequestBody WalletOperationRequestDto request) {
        UUID walletId = request.getWalletId();
        OperationType operationType = request.getOperationType();
        BigDecimal amount = request.getAmount();

        try {
            Wallet wallet = walletService.getWalletById(walletId);
            Wallet updatedWallet;

            if (operationType == OperationType.DEPOSIT) {
                updatedWallet = walletService.deposit(wallet, amount);
            } else {
                updatedWallet = walletService.withdraw(wallet, amount);
            }

            walletService.saveWallet(updatedWallet);
            return ResponseEntity.ok().build();
        } catch (WalletNotFoundException e) {
            logger.error("Wallet not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Internal server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("api/v1/wallets/{walletId}")
    public ResponseEntity<?> getWalletBalance(@PathVariable UUID walletId) {
        try {
            Wallet wallet = walletService.getWalletById(walletId);
            return ResponseEntity.ok(wallet.getBalance());
        } catch (WalletNotFoundException e) {
            logger.error("Wallet not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Internal server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
