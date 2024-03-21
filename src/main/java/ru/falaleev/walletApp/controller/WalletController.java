package ru.falaleev.walletApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.falaleev.walletApp.dto.WalletOperationRequestDto;
import ru.falaleev.walletApp.exception.InsufficientBalanceException;
import ru.falaleev.walletApp.exception.WalletNotFoundException;
import ru.falaleev.walletApp.model.Wallet;
import ru.falaleev.walletApp.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;


@RestController
public class WalletController {
    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/api/v1/wallet")
    public ResponseEntity<?> processWalletOperation(@RequestBody WalletOperationRequestDto request) {
        try {
            Wallet wallet = walletService.getWalletOrHandleNotFoundException(request.getWalletId());
            Wallet updatedWallet = walletService.performOperation(wallet, request.getOperationType(), request.getAmount());
            walletService.saveWallet(updatedWallet);
            return ResponseEntity.ok().build();
        } catch (InsufficientBalanceException e) {
            return walletService.handleBadRequest(e.getMessage());
        }
    }

    @GetMapping("api/v1/wallets/{walletId}")
    public ResponseEntity<?> getWalletBalance(@PathVariable UUID walletId) {
        try {
            Wallet wallet = walletService.getWalletOrHandleNotFoundException(walletId);
            BigDecimal balance = wallet.getBalance();
            return ResponseEntity.ok(balance);
        } catch (WalletNotFoundException e) {
            return walletService.handleNotFoundException(e);
        }
    }
}
