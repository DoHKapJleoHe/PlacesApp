package ru.nsu.vartazaryan.controller;

public class Place
{
    private String lat;
    private String lng;
    private String name;

    public Place(String lat, String lng, String name)
    {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public String getName() {
        return name;
    }

    public String getLng() {
        return lng;
    }

    @Override
    public String toString()
    {
        return this.name + " (lng: " + this.lng + " lat: " + this.lat + ")" +"\n";
    }
}
