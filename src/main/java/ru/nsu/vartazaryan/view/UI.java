package ru.nsu.vartazaryan.view;

import ru.nsu.vartazaryan.controller.Controller;
import ru.nsu.vartazaryan.controller.InterestingPlaces;
import ru.nsu.vartazaryan.controller.Place;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UI
{
    private final Controller controller = new Controller();
    private List<Place> placeList = new ArrayList<>();
    private List<InterestingPlaces> interestingPlacesList = new ArrayList<>();

    public void run()
    {
        JFrame frame = new JFrame();
        frame.setBounds(300, 0, 1000, 800);
        frame.getContentPane().setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JButton findButton = new JButton("Find!");
        findButton.setBounds(30, 30, 100, 25);
        findButton.setVisible(true);
        frame.add(findButton);

        JTextField textField = new JTextField("Write here text...");
        textField.setVisible(true);
        textField.setBounds(160, 30, 300, 25);
        frame.add(textField);

        DefaultListModel<String> placeModel = new DefaultListModel<>();
        JList<String> placeJList = new JList(placeModel);
        placeJList.setVisible(true);
        placeJList.setBounds(480, 30, 400, 300);
        placeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frame.add(placeJList);

        JLabel weatherLabel = new JLabel("Weather");
        weatherLabel.setBounds(480, 340, 400, 15);
        weatherLabel.setVisible(true);
        frame.add(weatherLabel);

        DefaultListModel<String> interestingPlacesModel = new DefaultListModel<>();
        JList<String> interestingPlacesJList = new JList<>(interestingPlacesModel);
        interestingPlacesJList.setVisible(true);
        interestingPlacesJList.setBounds(480, 380, 400, 300);
        frame.add(interestingPlacesJList);

        JTextArea infoAboutPlace = new JTextArea();
        infoAboutPlace.setBounds(50, 280, 400, 400);
        infoAboutPlace.setVisible(true);
        infoAboutPlace.setEditable(false);
        infoAboutPlace.setLineWrap(true);
        frame.add(infoAboutPlace);

        frame.setVisible(true);

        findButton.addActionListener(e -> {
            try
            {
                controller.findPlaces(textField.getText()).thenAccept(places -> SwingUtilities.invokeLater(() -> {
                    placeModel.clear(); // clear list from the previous request
                    placeList = places;
                    for (Place place : places)
                    {
                        placeModel.addElement(place.toString());
                    }
                }));
            } catch (ExecutionException | InterruptedException ex) {
                System.out.println("Error while getting list of places!");
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        placeJList.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting())
            {
                String lat, lng, place;
                int index = e.getLastIndex();

                lat = placeList.get(index).getLat();
                lng = placeList.get(index).getLng();

                try
                {
                    controller.findWeather(lat, lng).thenAccept(weather -> SwingUtilities.invokeLater(() ->{
                        weatherLabel.setText(weather.getMain() + " " + weather.getDescription());
                    }));

                    controller.getInterestingPlaces(lat, lng).thenAccept(interestingPlaces -> SwingUtilities.invokeLater(() -> {
                        interestingPlacesModel.clear();
                        for(InterestingPlaces places : interestingPlaces)
                        {
                            interestingPlacesList = interestingPlaces;

                            interestingPlacesModel.addElement(places.toString());
                        }
                    }));
                }
                catch (ExecutionException | InterruptedException ex)
                {
                    System.out.println("Error while getting weather!");
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        interestingPlacesJList.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting())
            {
                int index = e.getLastIndex();

                String id = interestingPlacesList.get(index).getId();
                id = id.substring(1, id.length()-1);
                System.out.println(id);

                try {
                    controller.getPlaceInfoById(id).thenAccept(info -> SwingUtilities.invokeLater(() -> {
                        infoAboutPlace.setText(info);
                        System.out.println("Hi");
                    }));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
