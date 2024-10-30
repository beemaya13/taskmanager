package com.nilga.taskmanager.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for sending messages to Telegram chats.
 * This service sends messages to multiple chat IDs using the Telegram Bot API.
 */
@Service
public class TelegramService {

    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.ids}")
    private String chatIds;

    /**
     * Constructs a new TelegramService with the specified RestTemplate.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests
     */
    public TelegramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a message to multiple chat IDs as defined in the configuration.
     *
     * @param message the message to send
     */
    public void sendMessageToMultipleChats(String message) {
        String[] ids = chatIds.split(",");
        for (String chatId : ids) {
            sendMessage(chatId.trim(), message);
        }
    }

    /**
     * Sends a message to a specific chat ID.
     *
     * @param chatId  the ID of the chat to send the message to
     * @param message the message to send
     */
    public void sendMessage(String chatId, String message) {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s", botToken, chatId, message);
        restTemplate.getForObject(url, String.class);
    }
}
