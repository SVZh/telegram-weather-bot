package praktikum.svzh.telegramweatherbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import praktikum.svzh.telegramweatherbot.pojo.WeatherPojo;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class WeatherService {

    @Value("${owmtoken}")
    private String token;

    private final ObjectMapper om = new ObjectMapper();

    @PostConstruct
    private void init() {
        log.info("Initializing: {}", this.getClass().getName());
    }

    @Async
    public CompletableFuture<String> getWeatherReportForCoords(double lat, double lon) throws IOException {
        URL jsonURL = new URL(String.format("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s", lat, lon, token));
        log.debug("URL composed: {}", jsonURL);
        WeatherPojo report = om.readValue(jsonURL, WeatherPojo.class);
        return CompletableFuture.completedFuture(
                String.format("%s (%s:%s)%n", report.name, report.coord.lon, report.coord.lat) +
                        String.format("Current temp: %.2f%n", (report.main.temp - 273)) +
                        String.format("Feels like: %.2f%n", (report.main.feels_like - 273))
        );
    }
}
