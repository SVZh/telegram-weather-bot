package praktikum.svzh.telegramweatherbot.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import praktikum.svzh.telegramweatherbot.service.Bot;
import praktikum.svzh.telegramweatherbot.service.UserService;
import praktikum.svzh.telegramweatherbot.service.WeatherService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@EnableScheduling
public class WeatherUpdateSchedule {

    private final WeatherService weatherService;
    private final UserService service;
    private final Bot bot;

    @Autowired
    public WeatherUpdateSchedule(WeatherService weatherService, UserService service, Bot bot) {
        this.weatherService = weatherService;
        this.service = service;
        this.bot = bot;
    }

    @Scheduled(cron = "0 0 * * * *")
    private void runWeatherUpdate() {
        log.info("Running weather update schedule!");
        service.getAllSubscribers()
            .forEach(user -> {
                log.info("Running schedule for user {}", user.getName());
                SendMessage message = null;
                try {
                    message = SendMessage.builder()
                            .chatId(user.getTelegramId().toString())
                            .text(weatherService.getWeatherReportForCoords(user.getLat(), user.getLon()).get())
                            .build();
                } catch (IOException | ExecutionException e) {
                    log.error("Can't get weather!", e);
                } catch (InterruptedException e) {
                    log.error("Interrupted!", e);
                    Thread.currentThread().interrupt();
                }
                try {
                    bot.execute(message);
                } catch (TelegramApiException e) {
                    log.error("Can't send scheduled message!", e);
                }
            });
    }
}
