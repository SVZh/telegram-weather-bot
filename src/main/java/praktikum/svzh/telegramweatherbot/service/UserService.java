package praktikum.svzh.telegramweatherbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;
import praktikum.svzh.telegramweatherbot.entity.User;
import praktikum.svzh.telegramweatherbot.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static praktikum.svzh.telegramweatherbot.Utils.getDisplayName;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAllSubscribers() {
        return repository.findAllBySubscribedEquals(true);
    }

    public User getByTelegramId(Long id) {
        return repository.findByTelegramId(id);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public void subscribeUser(User user) {
        user.setSubscribed(true);
        save(user);
    }

    public void updateLocation(User user, Location location) {
        user.setLat(Optional.ofNullable(location)
                .map(Location::getLatitude)
                .orElse(null)
        );
        user.setLon(Optional.ofNullable(location)
                .map(Location::getLongitude)
                .orElse(null)
        );
        save(user);
    }

    public User getOrCreate(org.telegram.telegrambots.meta.api.objects.User user) {
        User existingUser = getByTelegramId(user.getId());
        return Objects.nonNull(existingUser) ? existingUser : save(User.builder()
                .name(getDisplayName(user))
                .subscribed(false)
                .telegramId(user.getId())
                .build());
    }
}
