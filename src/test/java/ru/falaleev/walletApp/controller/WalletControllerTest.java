package ru.falaleev.walletApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.falaleev.walletApp.dto.WalletOperationRequestDto;
import ru.falaleev.walletApp.model.OperationType;
import ru.falaleev.walletApp.model.Wallet;
import ru.falaleev.walletApp.service.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void testProcessWalletOperation_deposit() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        WalletOperationRequestDto request = new WalletOperationRequestDto(walletId, OperationType.DEPOSIT, amount);
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(0));

        when(walletService.getWalletById(walletId)).thenReturn(wallet);
        when(walletService.deposit(any(Wallet.class), any(BigDecimal.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testProcessWalletOperation_withdraw() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100);
        WalletOperationRequestDto request = new WalletOperationRequestDto(walletId, OperationType.WITHDRAW, amount);
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(200));

        when(walletService.getWalletById(walletId)).thenReturn(wallet);
        when(walletService.withdraw(any(Wallet.class), any(BigDecimal.class))).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetWalletBalance() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, BigDecimal.valueOf(200));

        when(walletService.getWalletById(walletId)).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isOk());
    }
}
