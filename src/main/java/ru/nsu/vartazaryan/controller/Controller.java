package ru.nsu.vartazaryan.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Controller
{
    public CompletableFuture<List<Place>> findPlaces(String text) throws ExecutionException, InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();


        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("keys.properties"));

        var stringURI_places = String.format("https://graphhopper.com/api/1/geocode?q=%s&locale=en&key=%s", text, properties.get("key_places"));
        var request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(stringURI_places))
                .build();

        CompletableFuture<List<Place>> places = new CompletableFuture<List<Place>>();
        places = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parsePlace);

        return places;
    }

    private List<Place> parsePlace(String response)
    {
        List<Place> placeList = new ArrayList<>();

        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        JsonArray arr = json.get("hits").getAsJsonArray();

        String name, lng, lat;
        for(int i = 0; i < arr.size(); i++)
        {
            name = arr.get(i).getAsJsonObject().get("name").toString();
            lng = arr.get(i).getAsJsonObject().get("point").getAsJsonObject().get("lng").toString();
            lat = arr.get(i).getAsJsonObject().get("point").getAsJsonObject().get("lat").toString();

            placeList.add(new Place(lat, lng, name));
        }

        return placeList;
    }

    public CompletableFuture<Weather>  findWeather(String lat, String lng) throws ExecutionException, InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("keys.properties"));

        var stringURI_weather = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s", lat, lng, properties.get("key_weather"));
        var request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(stringURI_weather))
                .build();

        CompletableFuture<Weather> placeWeather = new CompletableFuture<>();
        placeWeather = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseWeather);

        return placeWeather;
    }

    private Weather parseWeather(String response)
    {
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        JsonArray arr = json.get("weather").getAsJsonArray();

        String main, descr;
        main = arr.get(0).getAsJsonObject().get("main").toString();
        descr = arr.get(0).getAsJsonObject().get("description").toString();

        Weather weather = new Weather(main, descr);

        return weather;
    }

    public CompletableFuture<List<InterestingPlaces>> getInterestingPlaces(String lat, String lng) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("keys.properties"));

        var stringURI_interestingPlaces = String.format("http://api.opentripmap.com/0.1/en/places/radius?lang=en&radius=1000&lat=%s&lon=%s&limit=5&apikey=%s",
                                                                lat, lng, properties.get("key_tripmap"));
        var request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(stringURI_interestingPlaces))
                .build();

        System.out.println(request);

        CompletableFuture<List<InterestingPlaces>> interestingPlaces = new CompletableFuture<>();
        interestingPlaces = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseInterestingPlaces);

        return interestingPlaces;
    }

    public List<InterestingPlaces> parseInterestingPlaces(String response)
    {
        List<InterestingPlaces> places = new ArrayList<>();

        String name, id;
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        JsonArray json = object.getAsJsonArray("features");

        for(int i = 0; i < json.size(); i++)
        {
            name = json.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("name").toString();
            id = json.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("xid").toString();

            places.add(new InterestingPlaces(name, id));
        }

        return places;
    }

    public CompletableFuture<String> getPlaceInfoById(String xid) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("keys.properties"));

        var stringURI_info = String.format("http://api.opentripmap.com/0.1/en/places/xid/%s?apikey=%s", xid, properties.get("key_tripmap"));
        var request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(stringURI_info))
                .build();

        System.out.println(request);

        CompletableFuture<String> info = new CompletableFuture<>();
        info = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseInfo);

        return info;
    }

    //TODO: add another parser if there is an info field in response
    private String parseInfo(String response)
    {
        System.out.println(response);
        JsonObject obj = JsonParser.parseString(response).getAsJsonObject();
        String info = obj.get("wikipedia_extracts").getAsJsonObject().get("text").toString();

        return info;
    }
}