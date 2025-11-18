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

        // Criar alguns FacilityTypes fictícios
        FacilityType stationType = new FacilityType(2, "Station");

        // Lista de estações de exemplo
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(1, "Porto", stationType, 41.1579, -8.6291, false, "PT", "Europe/Lisbon", "WEST", true, true, false));
        stations.add(new Station(2, "Lisboa", stationType, 38.7169, -9.1393, false, "PT", "Europe/Lisbon", "WEST", true, true, false));
        stations.add(new Station(3, "Madrid", stationType, 40.4168, -3.7038, false, "ES", "Europe/Madrid", "CET", true, true, false));
        stations.add(new Station(4, "Barcelona", stationType, 41.3851, 2.1734, false, "ES", "Europe/Madrid", "CET", true, false, false));
        stations.add(new Station(5, "Coimbra", stationType, 40.2033, -8.4103, false, "PT", "Europe/Lisbon", "WEST", true, false, false));

        // Criar RangeQueryService e construir KD-tree
        RangeQueryService service = new RangeQueryService();
        service.buildIndex(stations);

        System.out.println("Altura da árvore: " + service.getHeight());
        System.out.println("Número de nós distintos: " + service.getDistinctNodeCount());
        System.out.println();

        // Query 1: todas as estações em Portugal
        System.out.println("Query 1: Estaçoes em Portugal");
        List<Station> result1 = service.rangeQuery(36, 43, -10, -6, null, null, "PT");
        result1.forEach(s -> System.out.println(s.getName()));
        System.out.println();

        // Query 2: cidades principais em Portugal
        System.out.println("Query 2: Cidades principais em Portugal");
        List<Station> result2 = service.rangeQuery(36, 43, -10, -6, true, true, "PT");
        result2.forEach(s -> System.out.println(s.getName()));
        System.out.println();

        // Query 3: todas as estações em Espanha
        System.out.println("Query 3: Estaçoes em Espanha");
        List<Station> result3 = service.rangeQuery(36, 44, -4, 3, null, null, "ES");
        result3.forEach(s -> System.out.println(s.getName()));
        System.out.println();

        // Query 4: cidades principais independentemente do país
        System.out.println("Query 4: Cidades principais (qualquer país)");
        List<Station> result4 = service.rangeQuery(36, 44, -10, 4, true, true, "ALL");
        result4.forEach(s -> System.out.println(s.getName()));
        System.out.println();

        // Query 5: todas as cidades
        System.out.println("Query 5: Todas as cidades");
        List<Station> result5 = service.rangeQuery(36, 44, -10, 4, true, null, "ALL");
        result5.forEach(s -> System.out.println(s.getName()));

        System.out.println("\n\n\n");

        MainMenu.display();
    }
}