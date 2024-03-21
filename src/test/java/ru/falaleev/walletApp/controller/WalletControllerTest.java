package ru.falaleev.walletApp.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.falaleev.walletApp.dto.WalletOperationRequestDto;
import ru.falaleev.walletApp.model.OperationType;
import ru.falaleev.walletApp.model.Wallet;
import ru.falaleev.walletApp.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @Test
    void testProcessWalletOperation_deposit() throws Exception {
        // Устанавливаем значения для теста
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        WalletOperationRequestDto request = new WalletOperationRequestDto(walletId, OperationType.DEPOSIT, amount);
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(0));

        // Создаем заглушки для возвращаемых значений
        when(walletService.getWalletOrHandleNotFoundException(walletId)).thenReturn(wallet);
        when(walletService.performOperation(any(Wallet.class), any(OperationType.class), any(BigDecimal.class))).thenReturn(wallet);
        when(walletService.saveWallet(wallet)).thenReturn(wallet);

        // Вызываем метод, который тестируем
        ResponseEntity<?> responseEntity = walletController.processWalletOperation(request);

        // Проверяем, что методы сервиса были вызваны
        verify(walletService, times(1)).getWalletOrHandleNotFoundException(walletId);
        verify(walletService, times(1)).performOperation(wallet, OperationType.DEPOSIT, amount);
        verify(walletService, times(1)).saveWallet(wallet);

        // Проверяем, что возвращенный ResponseEntity имеет статус 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testGetWalletBalance() {
        // Устанавливаем значения для теста
        UUID walletId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.valueOf(100);
        Wallet wallet = new Wallet(walletId, balance);

        // Создаем заглушку для возвращаемого значения
        when(walletService.getWalletOrHandleNotFoundException(walletId)).thenReturn(wallet);

        // Вызываем метод, который тестируем
        ResponseEntity<?> responseEntity = walletController.getWalletBalance(walletId);

        // Проверяем, что метод getWalletOrHandleNotFoundException был вызван
        verify(walletService, times(1)).getWalletOrHandleNotFoundException(walletId);

        // Проверяем, что возвращенный ResponseEntity содержит ожидаемый баланс
        assertEquals(balance, responseEntity.getBody());
    }
}
