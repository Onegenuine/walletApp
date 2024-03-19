package ru.falaleev.walletApp.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.falaleev.walletApp.exception.InsufficientBalanceException;
import ru.falaleev.walletApp.model.Wallet;
import ru.falaleev.walletApp.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private WalletService walletService;

    @Test
    void testDeposit() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(100));
        BigDecimal depositAmount = BigDecimal.valueOf(50);

        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet result = walletService.deposit(wallet, depositAmount);

        assertEquals(wallet.getBalance().add(depositAmount), result.getBalance());
        verify(logger).debug("Depositing {} to wallet with ID {}", depositAmount, walletId);
    }

    @Test
    void testWithdraw_InsufficientBalance() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(100));
        BigDecimal withdrawAmount = BigDecimal.valueOf(150);

        assertThrows(InsufficientBalanceException.class, () -> walletService.withdraw(wallet, withdrawAmount));
        verify(logger).error("Insufficient balance to withdraw {} from wallet with ID {}", withdrawAmount, walletId);
    }

    @Test
    void testWithdraw_SufficientBalance() {
        UUID walletId = UUID.randomUUID();
        BigDecimal initialBalance = BigDecimal.valueOf(200);
        BigDecimal withdrawAmount = BigDecimal.valueOf(150);
        Wallet wallet = new Wallet(walletId, initialBalance);

        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet result = walletService.withdraw(wallet, withdrawAmount);

        assertEquals(initialBalance.subtract(withdrawAmount), result.getBalance());
        verify(logger).debug("Withdrawing {} from wallet with ID {}", withdrawAmount, walletId);
    }

    @Test
    void testSaveWallet() {
        UUID walletId = UUID.randomUUID();
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        Wallet wallet = new Wallet(walletId, initialBalance);

        when(walletRepository.save(wallet)).thenReturn(wallet);

        Wallet result = walletService.saveWallet(wallet);

        assertEquals(wallet, result);
        verify(logger).debug("Saving wallet: {}", wallet);
    }

    @Test
    void testGetAllWallets() {
        UUID walletId1 = UUID.randomUUID();
        UUID walletId2 = UUID.randomUUID();
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        Wallet wallet1 = new Wallet(walletId1, initialBalance);
        Wallet wallet2 = new Wallet(walletId2, initialBalance);

        when(walletRepository.findAll()).thenReturn(List.of(wallet1, wallet2));

        List<Wallet> wallets = walletService.getAllWallets();

        assertEquals(2, wallets.size());
        assertTrue(wallets.contains(wallet1));
        assertTrue(wallets.contains(wallet2));
        verify(logger).debug("Getting all wallets");
    }
}
