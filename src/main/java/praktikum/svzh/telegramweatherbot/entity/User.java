package praktikum.svzh.telegramweatherbot.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="PEOPLE")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Boolean subscribed;
    private Long telegramId;
    private Double lat;
    private Double lon;
}
