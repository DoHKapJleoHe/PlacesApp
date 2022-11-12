package ru.nsu.vartazaryan.controller;

public class Weather
{
    private String main;
    private String description;

    public Weather(String main, String description)
    {
        this.main = main;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getMain() {
        return main;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "main='" + main + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
