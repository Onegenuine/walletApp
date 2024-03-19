package ru.falaleev.walletApp.exeption;
import org.junit.jupiter.api.Test;
import ru.falaleev.walletApp.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.UUID;



import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InsufficientBalanceExceptionTest {

    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(InsufficientBalanceException.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void testInsufficientBalanceException() {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(50);
        BigDecimal balance = BigDecimal.valueOf(30);

        InsufficientBalanceException exception = new InsufficientBalanceException(walletId, amount, balance);

        assertEquals("Wallet with ID " + walletId + " has insufficient balance " + balance + " to withdraw " + amount, exception.getMessage());

        // Проверяем, что сообщение было залогировано с правильными параметрами
        assertEquals(1, listAppender.list.size());
        ILoggingEvent loggingEvent = listAppender.list.get(0);
        assertEquals(Level.ERROR, loggingEvent.getLevel());
        assertEquals("Wallet with ID " + walletId + " has insufficient balance " + balance + " to withdraw " + amount, loggingEvent.getFormattedMessage());
    }
}

