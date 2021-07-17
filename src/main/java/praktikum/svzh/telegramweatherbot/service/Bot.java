package praktikum.svzh.telegramweatherbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import praktikum.svzh.telegramweatherbot.config.BotConfig;
import praktikum.svzh.telegramweatherbot.entity.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final WeatherService weatherService;
    private final UserService userService;

    @Autowired
    public Bot(BotConfig config, WeatherService weatherService, UserService userService) {
        this.config = config;
        this.weatherService = weatherService;
        this.userService = userService;
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            photoMessageHandler(update);
            return;
        } else if (update.hasMessage() && update.getMessage().hasLocation()) {
            // location handlers
            try {
                locationMessageHandler(update);
            } catch (InterruptedException e) {
                log.error("Interrupted exception while getting weather", e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException | IOException e) {
                log.error("Can't get weather", e);
            } catch (TelegramApiException e) {
                log.error("Telegram API exception", e);
            }
            return;
        } else if (update.hasCallbackQuery()) {
            // callback handlers
            callbackMessageHander(update);
            return;
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            textMessageHandler(update);
            return;
        }
    }

    private void photoMessageHandler(Update update) {
        throw new NotImplementedException();
    }

    private void locationMessageHandler(Update update) throws IOException, ExecutionException, InterruptedException, TelegramApiException {
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        builder.chatId(update.getMessage().getChatId().toString());

        Location location = update.getMessage().getLocation();
        String answer = weatherService.getWeatherReportForCoords(location.getLatitude(), location.getLongitude()).get();
        builder.text(answer);

        Long userId = update.getMessage().getFrom().getId();
        User user = userService.getByTelegramId(userId);
        if (Objects.isNull(user)) {
            user = userService.getOrCreate(update.getMessage().getFrom());
        }
        // show button only if this user has not subscribed still
        if (Boolean.FALSE.equals(user.getSubscribed())) {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            userService.updateLocation(user, location);

            rowInline.add(InlineKeyboardButton.builder()
                    .text("Receive hourly updates?")
                    .callbackData("receive_hourly_updates")
                    .build());

            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            builder.replyMarkup(markupInline);
        }

        execute(builder.build());
    }

    private void callbackMessageHander(Update update) {
        String callData = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        User user = userService.getOrCreate(update.getCallbackQuery().getFrom());

        if (callData.equals("receive_hourly_updates")) {
            String answer;
            if (Boolean.FALSE.equals(user.getSubscribed())) {
                userService.subscribeUser(user);
                answer = "Now you'll get updates every hour!";
            } else {
                answer = "You're subscribed already!";
            }
            EditMessageText newMessage = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(answer)
                    .build();
            try {
                execute(newMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void textMessageHandler(Update update) {
        throw new NotImplementedException();
    }


    public String getBotUsername() {
        return config.getBotUserName();
    }

    public String getBotToken() {
        return config.getToken();
    }
}
