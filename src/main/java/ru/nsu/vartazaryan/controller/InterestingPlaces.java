package ru.nsu.vartazaryan.controller;

public class InterestingPlaces
{
    private String name;
    private String id;

    public InterestingPlaces(String name, String id)
    {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
