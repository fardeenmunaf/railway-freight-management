package app;

import app.view.MainMenu;

public class Main {

    public static void main(String[] args) {

        System.out.println("\n=== Initializing Dataset ===");
        new AppInitializer().run();
        System.out.println("Dataset loaded.\n");
        MainMenu.display();
    }
}
