package praktikum.svzh.telegramweatherbot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static String getDisplayName(User user) {
        if (Objects.isNull(user)) return "";
        return String.format("%s %s",
                Optional.of(user).map(User::getFirstName).orElse(""),
                Optional.of(user).map(User::getLastName).orElse("")
        ).trim();
    }
}
