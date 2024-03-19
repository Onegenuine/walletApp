package ru.falaleev.walletApp.repository;


import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import ru.falaleev.walletApp.model.Wallet;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void testWalletRepository() {
        // Создаем новый кошелек
        Wallet wallet = new Wallet(UUID.randomUUID(), BigDecimal.valueOf(100));
        // Сохраняем кошелек в репозитории
        walletRepository.save(wallet);

        // Извлекаем кошелек из репозитория по его ID
        Wallet retrievedWallet = walletRepository.findById(wallet.getId()).orElse(null);

        // Проверяем, что кошелек был успешно извлечен из репозитория
        assertNotNull(retrievedWallet);
    }
}

