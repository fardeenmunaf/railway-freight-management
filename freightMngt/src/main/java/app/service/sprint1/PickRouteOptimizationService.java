package app.service.sprint1;

import java.util.*;

public class PickRouteOptimizationService {

    // Inner class para representar a localização da bay
    public static class BayLocation {
        private String aisle;
        private int bay;  // removida a palavra static
        private double quantity;

        // Construtor
        public BayLocation(String aisle, int bay, double quantity) {
            this.aisle = aisle;
            this.bay = bay;
            this.quantity = quantity;
        }

        public String getAisle() {
            return aisle;
        }

        public int getBay() {
            return bay;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "(" + aisle + "," + bay + ") qty:" + quantity;
        }

        public String getBayName() {
            return "W1-A" + aisle + "-B" + bay;
        }
    }

    // Classe para guardar o resultado de cada estratégia
    public static class RouteResult {
        private String strategyName;
        private List<BayLocation> path;
        private int totalDistance;

        public RouteResult(String strategyName, List<BayLocation> path, int totalDistance) {
            this.strategyName = strategyName;
            this.path = path;
            this.totalDistance = totalDistance;
        }

        public String getStrategyName() {
            return strategyName;
        }

        public List<BayLocation> getPath() {
            return path;
        }

        public int getTotalDistance() {
            return totalDistance;
        }

        @Override
        public String toString() {
            return strategyName + " - Distance: " + totalDistance + ", Path: " + path;
        }
    }

    /**
     * Calcula a distância entre dois pontos conforme as regras do warehouse
     */
    private int calculateDistance(int a1, int b1, int a2, int b2) {
        if (a1 == a2) {
            return Math.abs(b1 - b2);
        } else {
            return b1 + Math.abs(a1 - a2) * 3 + b2;
        }
    }

    /**
     * Estratégia A: Deterministic Sweep
     */

    public RouteResult computeDeterministicSweep(List<BayLocation> bays) {
        List<BayLocation> sorted = new ArrayList<>(bays);

        // Ordenar por aisle primeiro, depois por bay (ambos crescentes)

        sorted.sort((b1, b2) -> {
            int aisleCompare = b1.getAisle().compareTo(b2.getAisle());
            if (aisleCompare != 0) return aisleCompare;
            return Integer.compare(b1.getBay(), b2.getBay());
        });

        int totalDistance = (int) calculateTotalDistance(sorted);
        return new RouteResult("Strategy A - Deterministic Sweep", sorted, totalDistance);
    }


    /**
     * Estratégia B: Nearest Neighbour (Greedy)
     */
    public RouteResult computeNearestNeighbor(List<BayLocation> bays) {
        List<BayLocation> remaining = new ArrayList<>(bays);
        List<BayLocation> path = new ArrayList<>();

        int currentAisle = 0;
        int currentBay = 0;

        while (!remaining.isEmpty()) {
            BayLocation nearest = null;
            int minDistance = Integer.MAX_VALUE;

            for (BayLocation bay : remaining) {
                // Use os métodos getAisle() e getBay() diretamente
                int dist = calculateDistance(
                        currentAisle,
                        currentBay,
                        Integer.parseInt(bay.getAisle()),  // aisle já é "1", "2", etc.
                        bay.getBay()                       // bay já é int
                );
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = bay;
                }
            }

            path.add(nearest);
            remaining.remove(nearest);

            // Atualiza posição atual usando os valores corretos
            currentAisle = Integer.parseInt(nearest.getAisle());
            currentBay = nearest.getBay();
        }

        int totalDistance = (int) calculateTotalDistance(path);
        return new RouteResult("Strategy B - Nearest Neighbour", path, totalDistance);
    }

    /**
     * Calcula a distância total de um caminho (SEM retorno à entrada)
     */
    private double calculateTotalDistance(List<BayLocation> route) {
        if (route.isEmpty()) return 0.0;

        int total = 0;
        int currentAisle = 0; // Entrada
        int currentBay = 0;   // Entrada

        // Calcular distância para cada bay no caminho
        for (BayLocation bay : route) {
            int nextAisle = Integer.parseInt(bay.getAisle());
            int nextBay = bay.getBay();

            total += calculateDistance(currentAisle, currentBay, nextAisle, nextBay);

            currentAisle = nextAisle;
            currentBay = nextBay;
        }

        // NÃO adicionar retorno - apenas o percurso ida
        return total;
    }

    /**
     * Executa ambas as estratégias e retorna os resultados
     */
    public Map<String, RouteResult> optimizeRoute(List<BayLocation> bays) {
        Map<String, RouteResult> results = new HashMap<>();
        results.put("strategyA", computeDeterministicSweep(bays));
        results.put("strategyB", computeNearestNeighbor(bays));
        return results;
    }
}
