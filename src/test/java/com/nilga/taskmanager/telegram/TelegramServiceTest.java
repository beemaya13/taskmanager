package com.nilga.taskmanager.telegram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;


public class TelegramServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TelegramService telegramService;

    private String botToken = "dummy_bot_token";
    private String chatIds = "123456,789012";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(telegramService, "botToken", botToken);
        ReflectionTestUtils.setField(telegramService, "chatIds", chatIds);
    }

    @Test
    public void testSendMessageToMultipleChats() {
        String message = "Test message";
        telegramService.sendMessageToMultipleChats(message);

        verify(restTemplate, times(1)).getForObject(
                eq("https://api.telegram.org/botdummy_bot_token/sendMessage?chat_id=123456&text=Test message"),
                eq(String.class));
        verify(restTemplate, times(1)).getForObject(
                eq("https://api.telegram.org/botdummy_bot_token/sendMessage?chat_id=789012&text=Test message"),
                eq(String.class));
    }

    @Test
    public void testSendMessage() {
        String chatId = "123456";
        String message = "Single message";
        telegramService.sendMessage(chatId, message);

        String expectedUrl = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s", botToken, chatId, message);
        verify(restTemplate, times(1)).getForObject(expectedUrl, String.class);
    }
}
