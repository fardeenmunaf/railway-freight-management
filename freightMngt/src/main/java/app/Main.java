package app;

import app.controller.PickRouteController;
import app.model.PickTask;
import app.model.PickWave;
import app.model.InventoryBalance;
import app.controller.InventoryBalanceController;
import app.model.ReturnLine;
import app.model.ReturnOrder;
import app.repositories.*;
import app.service.*;
import app.view.MainMenu;

import java.util.*;

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