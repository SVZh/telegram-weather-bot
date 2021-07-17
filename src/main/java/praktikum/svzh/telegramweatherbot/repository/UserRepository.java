package praktikum.svzh.telegramweatherbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import praktikum.svzh.telegramweatherbot.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByTelegramId(Long id);
    List<User> findAllBySubscribedEquals(boolean subscribed);
}
