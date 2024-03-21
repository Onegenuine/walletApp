package ru.falaleev.walletApp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.falaleev.walletApp.exception.WalletNotFoundException;
import ru.falaleev.walletApp.model.OperationType;
import ru.falaleev.walletApp.model.Wallet;
import ru.falaleev.walletApp.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void testWithdraw() {
        // Устанавливаем значения для теста
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(100));
        BigDecimal amount = BigDecimal.valueOf(50);
        BigDecimal expectedBalance = BigDecimal.valueOf(50);

        // Создаем заглушку для возвращаемого значения
        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Вызываем метод, который тестируем
        Wallet updatedWallet = walletService.withdraw(wallet, amount);

        // Проверяем, что метод save вызван с правильным аргументом
        verify(walletRepository, times(1)).save(wallet);

        // Проверяем, что баланс кошелька изменился правильно
        assertEquals(expectedBalance, updatedWallet.getBalance());
    }

    @Test
    void testSaveWallet() {
        // Устанавливаем значения для теста
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(100));

        // Создаем заглушку для возвращаемого значения
        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Вызываем метод, который тестируем
        Wallet savedWallet = walletService.saveWallet(wallet);

        // Проверяем, что метод save вызван с правильным аргументом
        verify(walletRepository, times(1)).save(wallet);

        // Проверяем, что возвращенный кошелек соответствует ожиданиям
        assertEquals(wallet, savedWallet);
    }

    @Test
    void testGetAllWallets() {
        // Устанавливаем значения для теста
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(100));
        List<Wallet> expectedWallets = Collections.singletonList(wallet);

        // Создаем заглушку для возвращаемого значения
        when(walletRepository.findAll()).thenReturn(expectedWallets);

        // Вызываем метод, который тестируем
        List<Wallet> wallets = walletService.getAllWallets();

        // Проверяем, что метод findAll вызван
        verify(walletRepository, times(1)).findAll();

        // Проверяем, что список кошельков не пустой и содержит ожидаемый кошелек
        assertFalse(wallets.isEmpty());
        assertEquals(expectedWallets, wallets);
    }

    @Test
    void testPerformOperation_deposit() {
        // Устанавливаем значения для теста
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(100));
        BigDecimal amount = BigDecimal.valueOf(50);
        BigDecimal expectedBalance = BigDecimal.valueOf(150);

        // Создаем заглушки для возвращаемых значений
        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Вызываем метод, который тестируем
        Wallet updatedWallet = walletService.performOperation(wallet, OperationType.DEPOSIT, amount);

        // Проверяем, что метод save вызван с правильным аргументом
        verify(walletRepository, times(1)).save(wallet);

        // Проверяем, что баланс кошелька изменился правильно
        assertEquals(expectedBalance, updatedWallet.getBalance());
    }

    @Test
    void testHandleBadRequest() {
        // Устанавливаем значения для теста
        String errorMessage = "Bad request error message";

        // Вызываем метод, который тестируем
        ResponseEntity<?> responseEntity = walletService.handleBadRequest(errorMessage);

        // Проверяем, что создан корректный объект ResponseEntity с HTTP статусом 400
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void testHandleNotFoundException() {
        // Устанавливаем значения для теста
        WalletNotFoundException exception = new WalletNotFoundException(UUID.randomUUID());

        // Вызываем метод, который тестируем
        ResponseEntity<?> responseEntity = walletService.handleNotFoundException(exception);

        // Проверяем, что создан корректный объект ResponseEntity с HTTP статусом 404
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }
}