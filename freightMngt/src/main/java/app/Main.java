package app;

import app.model.FacilityType;
import app.model.Station;
import app.service.sprint1.WagonsUnloading;
import app.service.sprint2.RangeQueryService;
import app.view.MainMenu;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        AppInitializer appInitializer = new AppInitializer();

        System.out.println("Initializing dataset...");
        appInitializer.run();

        WagonsUnloading wagonsUnloading = new WagonsUnloading();
        wagonsUnloading.unloadAllWagons();
        System.out.println("Dataset initialized successfully.\n\n");

        System.out.println("\n\n\n");

        MainMenu.display();
    }
}