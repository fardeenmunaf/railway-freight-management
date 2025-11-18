package app;

import app.service.sprint1.WagonsUnloading;
import app.view.MainMenu;

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