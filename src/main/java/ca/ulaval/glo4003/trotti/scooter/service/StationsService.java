package ca.ulaval.glo4003.trotti.scooter.service;

import java.util.HashMap;

import ca.ulaval.glo4003.trotti.scooter.model.Scooter;
import ca.ulaval.glo4003.trotti.scooter.model.Station;

public class StationsService {
    private static HashMap<String, Station> stations;

    public StationsService() {
        if (stations != null) {
            return;
        }
        stations = new HashMap<>();

        stations.put("VACHON", new Station("VACHON", "Entrée Vachon #1", 15));
        stations.put("PEPS", new Station("PEPS", "Entrée PEPS #3", 25));
        stations.put("DESJARDINS", new Station("DESJARDINS", "Centre de service - Desjardins", 20));
        stations.put("VANDRY", new Station("VANDRY", "Cours du vandry", 15));
        stations.put("MYRAND", new Station("MYRAND", "Secteur EST", 12));
        stations.put("PYRAMIDE", new Station("PYRAMIDE", "Secteur Nord - Chemin Ste-foy", 15));
        stations.put("CASAULT", new Station("CASAULT", "Secteur art", 14));
        stations.put("PLACE_STE_FOY", new Station("PLACE_STE_FOY", "Secteur Ouest - Place Ste-foy", 15));
    }

    public Scooter takeScooterFromStation(String stationName, int slot) {
        Station station = stations.get(stationName);
        if (station == null) {
            throw new IllegalArgumentException("Station not found");
        }
        return station.takeScooter(slot);
    }
}
