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
        // The UI is in English, so format numbers and dates the same way on every machine (e.g. 12.50, not 12,50)
        java.util.Locale.setDefault(java.util.Locale.US);

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