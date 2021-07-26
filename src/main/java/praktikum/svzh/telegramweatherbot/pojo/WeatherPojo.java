package praktikum.svzh.telegramweatherbot.pojo;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
WeatherPojo weatherPojo = om.readValue(myJsonString), WeatherPojo.class); */

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class WeatherPojo{
    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Clouds clouds;
    public int dt;
    public Sys sys;
    public int timezone;
    public int id;
    public String name;
    public int cod;
    @JsonIgnore
    public Object rain;
}



