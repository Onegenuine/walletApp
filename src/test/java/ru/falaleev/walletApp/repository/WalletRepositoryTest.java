package ru.falaleev.walletApp.repository;


import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.falaleev.walletApp.model.Wallet;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletRepositoryTest {

    @Mock
    private Wallet mockedWallet;

    @Mock
    private WalletRepository walletRepository;

    @Test
    void testFindByIdWithPessimisticWriteLock() {
        // Устанавливаем значения для теста
        UUID walletId = UUID.randomUUID();

        // Создаем заглушку для возвращаемого значения
        when(walletRepository.findByIdWithPessimisticWriteLock(walletId)).thenReturn(mockedWallet);

        // Вызываем метод, который тестируем
        Wallet wallet = walletRepository.findByIdWithPessimisticWriteLock(walletId);

        // Проверяем, что метод findByIdWithPessimisticWriteLock вызван с правильными аргументами
        verify(walletRepository, times(1)).findByIdWithPessimisticWriteLock(walletId);

        // Проверяем, что возвращенный объект соответствует ожидаемому
        assertEquals(mockedWallet, wallet);
    }
}

