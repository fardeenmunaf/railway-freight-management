package app.service;

import app.model.*;
import app.repositories.*;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class TravelTimeFeature {

    /**
     * Cria uma nova instância do calculador de tempo de viagem.
     *
     * @param facilityRepository repositório de instalações ferroviárias
     * @param lineSegmentRepository repositório de segmentos de linha
     * @param lineSegmentFacilityRepository repositório de ligações entre segmentos e instalações
     * @param locomotiveRepository repositório de locomotivas
     * @return nova instância de TravelTimeCalculator configurada
     */
    public static TravelTimeCalculator create(
            FacilityRepository facilityRepository,
            LineSegmentRepository lineSegmentRepository,
            LineSegmentFacilityRepository lineSegmentFacilityRepository,
            LocomotiveRepository locomotiveRepository) {
        return new TravelTimeCalculator(
                facilityRepository,
                lineSegmentRepository,
                lineSegmentFacilityRepository,
                locomotiveRepository
        );
    }

    /**
     * Solicita ao utilizador dados de viagem e calcula o tempo estimado.
     * Apresenta menu interativo para seleção de estações e locomotiva,
     * depois exibe os detalhes completos da viagem calculada.
     *
     * @param scanner scanner para entrada do utilizador
     */
    public static void calculateTravelTimeFromUserInput(Scanner scanner) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           CALCULATE TRAVEL TIME");
        System.out.println("=".repeat(50));
        System.out.println("Type 'b' at any time to return to menu.\n");

        Repositories repositories = Repositories.getInstance();
        FacilityRepository facilityRepo = repositories.getFacilityRepository();
        LocomotiveRepository locomotiveRepo = repositories.getLocomotiveRepository();

        System.out.println("AVAILABLE STATIONS:");
        System.out.println("-".repeat(30));
        List<Facility> facilities = facilityRepo.getAllFacilities();
        if (facilities != null && !facilities.isEmpty()) {
            facilities.stream()
                    .filter(f -> f != null && f.getFacilityType() != null
                            && Integer.valueOf(2).equals(f.getFacilityType().getFacilityTypeId()))
                    .sorted(Comparator.comparing(f -> f.getFacilityId() == null ? Integer.MAX_VALUE : f.getFacilityId()))
                    .forEach(f -> System.out.printf("   %d - %s%n",
                            f.getFacilityId(),
                            f.getName() != null ? f.getName() : "(no name)"));
        } else {
            System.out.println("   (no stations found)");
        }

        System.out.println("\nAVAILABLE LOCOMOTIVES:");
        System.out.println("-".repeat(30));
        List<Locomotive> locos = locomotiveRepo.getAllLocomotives();
        if (locos != null && !locos.isEmpty()) {
            locos.stream()
                    .sorted(Comparator.comparing(l -> l.getLocomotiveId() == null ? Integer.MAX_VALUE : l.getLocomotiveId()))
                    .forEach(l -> System.out.printf("   %d - %s (Max Speed: %.0f km/h)%n",
                            l.getLocomotiveId(),
                            l.getName() != null ? l.getName() : "(no name)",
                            l.getMaxSpeed() != null ? l.getMaxSpeed() : 0.0));
        } else {
            System.out.println("   (no locomotives found)");
        }

        try {
            System.out.print("\nEnter origin station ID: ");
            String originInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(originInput)) return;
            int originId = Integer.parseInt(originInput);

            System.out.print("Enter destination station ID: ");
            String destInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(destInput)) return;
            int destinationId = Integer.parseInt(destInput);

            System.out.print("Enter locomotive ID: ");
            String locoInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(locoInput)) return;
            int locomotiveId = Integer.parseInt(locoInput);

            TravelTimeCalculator calculator = TravelTimeFeature.create(
                    repositories.getFacilityRepository(),
                    repositories.getLineSegmentRepository(),
                    repositories.getLineSegmentFacilityRepository(),
                    repositories.getLocomotiveRepository()
            );

            double travelTime = calculator.calculateTravelTimeMinutes(originId, destinationId, locomotiveId);

            Facility origin = facilityRepo.getFacilityById(originId);
            Facility destination = facilityRepo.getFacilityById(destinationId);
            Locomotive locomotive = locomotiveRepo.getLocomotiveById(locomotiveId);

            System.out.println("\n" + "=".repeat(50));
            System.out.println("              JOURNEY RESULTS");
            System.out.println("=".repeat(50));
            System.out.printf("Locomotive: %s (ID: %d)%n",
                    locomotive.getName() != null ? locomotive.getName() : "(no name)", locomotiveId);
            System.out.printf("Origin: %s (ID: %d)%n", origin.getName(), originId);
            System.out.printf("Destination: %s (ID: %d)%n", destination.getName(), destinationId);
            System.out.println("-".repeat(50));
            System.out.printf("Distance: %.2f km%n", calculator.getDistanceKm());
            System.out.printf("Speed: %.2f km/h%n", calculator.getEffectiveSpeedKmh());
            System.out.printf("Travel Time: %.2f minutes (%.0fh %.0fm)%n",
                    travelTime,
                    Math.floor(travelTime / 60.0),
                    travelTime % 60.0);
            System.out.println("=".repeat(50));

        } catch (NumberFormatException e) {
            System.out.println("Error: please enter valid numeric IDs.");
        } catch (NoDirectConnectionException e) {
            System.out.println("Connection Error: " + e.getMessage());
        } catch (InvalidSpeedException e) {
            System.out.println("Speed Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    public static class TravelTimeCalculator {
        private final FacilityRepository facilityRepository;
        private final LineSegmentRepository lineSegmentRepository;
        private final LineSegmentFacilityRepository lineSegmentFacilityRepository;
        private final LocomotiveRepository locomotiveRepository;

        private LineSegment currentSegment;
        private double effectiveSpeed;
        private double distance;

        /**
         * Obtém a distância total da última viagem calculada.
         *
         * @return distância em quilómetros
         */
        public double getDistanceKm() {
            return this.distance;
        }

        /**
         * Obtém a velocidade efetiva da última viagem calculada.
         * Calculada como distância total dividida pelo tempo total.
         *
         * @return velocidade efetiva em km/h
         */
        public double getEffectiveSpeedKmh() {
            return this.effectiveSpeed;
        }

        /**
         * Construtor do calculador de tempo de viagem.
         *
         * @param facilityRepository repositório de instalações
         * @param lineSegmentRepository repositório de segmentos
         * @param lineSegmentFacilityRepository repositório de ligações
         * @param locomotiveRepository repositório de locomotivas
         */
        public TravelTimeCalculator(
                FacilityRepository facilityRepository,
                LineSegmentRepository lineSegmentRepository,
                LineSegmentFacilityRepository lineSegmentFacilityRepository,
                LocomotiveRepository locomotiveRepository) {
            this.facilityRepository = facilityRepository;
            this.lineSegmentRepository = lineSegmentRepository;
            this.lineSegmentFacilityRepository = lineSegmentFacilityRepository;
            this.locomotiveRepository = locomotiveRepository;
        }

        /**
         * Calcula o tempo de viagem entre duas estações usando uma locomotiva específica.
         * Encontra a rota mais rápida através da análise da rede ferroviária.
         *
         * @param stationId1 ID da estação de origem
         * @param stationId2 ID da estação de destino
         * @param locomotiveId ID da locomotiva a utilizar
         * @return tempo de viagem em minutos
         * @throws NoDirectConnectionException se não existir ligação entre as estações
         * @throws InvalidSpeedException se a locomotiva tiver velocidade inválida
         */
        public double calculateTravelTimeMinutes(Integer stationId1, Integer stationId2, Integer locomotiveId) {
            Facility station1 = validateStation(stationId1);
            Facility station2 = validateStation(stationId2);
            Locomotive locomotive = validateLocomotive(locomotiveId);

            Double locomotiveMaxSpeed = locomotive.getMaxSpeed();
            if (locomotiveMaxSpeed == null || locomotiveMaxSpeed <= 0) {
                throw new InvalidSpeedException("Locomotiva com velocidade máxima inválida: " + locomotiveId);
            }

            PathResult result = findShortestTimePath(station1.getFacilityId(), station2.getFacilityId(), locomotiveMaxSpeed);
            if (result == null) {
                throw new NoDirectConnectionException("Não existe ligação entre " + station1.getFacilityId() + " e " + station2.getFacilityId());
            }

            this.distance = result.totalDistanceKm;
            double totalHours = result.totalHours;
            this.effectiveSpeed = totalHours > 0 ? (result.totalDistanceKm / totalHours) : 0.0;
            return totalHours * 60.0;
        }

        /**
         * Representa o resultado de um cálculo de caminho entre estações.
         * Contém o tempo total e distância total da rota encontrada.
         */
        private static class PathResult {
            final double totalHours;
            final double totalDistanceKm;
            PathResult(double totalHours, double totalDistanceKm) {
                this.totalHours = totalHours;
                this.totalDistanceKm = totalDistanceKm;
            }
        }

        /**
         * Nó utilizado na fila de prioridade para análise de caminhos.
         * Mantém informação sobre uma estação e os custos acumulados até ela.
         */
        private static class QueueNode {
            final Integer facilityId;
            final double hoursSoFar;
            final double distanceSoFar;
            QueueNode(Integer facilityId, double hoursSoFar, double distanceSoFar) {
                this.facilityId = facilityId;
                this.hoursSoFar = hoursSoFar;
                this.distanceSoFar = distanceSoFar;
            }
        }

        /**
         * Representa uma ligação no grafo ferroviário.
         * Liga uma estação de destino ao segmento de linha correspondente.
         */
        private static class Edge {
            final Integer toFacilityId;
            final LineSegment segment;
            Edge(Integer toFacilityId, LineSegment segment) {
                this.toFacilityId = toFacilityId;
                this.segment = segment;
            }
        }

        /**
         * Encontra o caminho mais rápido entre duas estações ferroviárias.
         * Constrói grafo das ligações ferroviárias e calcula tempos baseados nas velocidades
         * efetivas (menor entre limite do segmento e velocidade máxima da locomotiva).
         *
         * @param startFacilityId ID da estação de partida
         * @param targetFacilityId ID da estação de chegada
         * @param locomotiveMaxSpeed velocidade máxima da locomotiva em km/h
         * @return resultado do caminho com tempo e distância totais, ou null se não existir caminho
         */
        private PathResult findShortestTimePath(Integer startFacilityId, Integer targetFacilityId, double locomotiveMaxSpeed) {
            if (startFacilityId == null || targetFacilityId == null) return null;
            java.util.Map<Integer, java.util.List<Edge>> graph = new java.util.HashMap<>();
            java.util.List<LineSegment> segments = lineSegmentRepository.getAllLineSegments();
            if (segments == null) segments = java.util.Collections.emptyList();

            for (LineSegment seg : segments) {
                if (seg == null) continue;
                Integer segId = seg.getSegmentId();
                if (segId == null) continue;
                java.util.List<LineSegmentFacility> facs = lineSegmentFacilityRepository.getLineSegmentFacilitiesBySegmentId(segId);
                if (facs == null || facs.isEmpty()) continue;
                java.util.List<Integer> facilityIds = new java.util.ArrayList<>();
                for (LineSegmentFacility lsf : facs) {
                    if (lsf == null || lsf.getFacility() == null || lsf.getFacility().getFacilityId() == null) continue;
                    facilityIds.add(lsf.getFacility().getFacilityId());
                }
                for (int i = 0; i < facilityIds.size(); i++) {
                    for (int j = 0; j < facilityIds.size(); j++) {
                        if (i == j) continue;
                        Integer a = facilityIds.get(i);
                        Integer b = facilityIds.get(j);
                        graph.computeIfAbsent(a, k -> new java.util.ArrayList<>()).add(new Edge(b, seg));
                    }
                }
            }

            java.util.PriorityQueue<QueueNode> pq = new java.util.PriorityQueue<>(java.util.Comparator.comparingDouble(n -> n.hoursSoFar));
            java.util.Map<Integer, Double> bestHours = new java.util.HashMap<>();
            pq.add(new QueueNode(startFacilityId, 0.0, 0.0));
            bestHours.put(startFacilityId, 0.0);

            while (!pq.isEmpty()) {
                QueueNode node = pq.poll();
                Integer cur = node.facilityId;
                double curHours = node.hoursSoFar;
                double curDistance = node.distanceSoFar;

                Double best = bestHours.get(cur);
                if (best != null && curHours > best + 1e-9) continue;

                if (cur.equals(targetFacilityId)) {
                    return new PathResult(curHours, curDistance);
                }

                java.util.List<Edge> neighbors = graph.getOrDefault(cur, java.util.Collections.emptyList());
                for (Edge e : neighbors) {
                    if (e == null || e.segment == null) continue;
                    double segDistanceKm = e.segment.getLengthKm() != null ? e.segment.getLengthKm() : 0.0;
                    if (segDistanceKm <= 0.0) continue;

                    Double segSpeedLimit = e.segment.getSpeedLimitKmh();
                    double speedKmh = (segSpeedLimit != null && segSpeedLimit > 0) ? Math.min(segSpeedLimit, locomotiveMaxSpeed) : locomotiveMaxSpeed;
                    if (speedKmh <= 0.0) continue;

                    double edgeHours = segDistanceKm / speedKmh;
                    double newHours = curHours + edgeHours;
                    double newDist = curDistance + segDistanceKm;

                    Double known = bestHours.get(e.toFacilityId);
                    if (known == null || newHours + 1e-9 < known) {
                        bestHours.put(e.toFacilityId, newHours);
                        pq.add(new QueueNode(e.toFacilityId, newHours, newDist));
                    }
                }
            }

            return null;
        }

        /**
         * Valida se uma instalação existe e é uma estação ferroviária.
         *
         * @param facilityId ID da instalação a validar
         * @return instalação validada
         * @throws NoDirectConnectionException se a instalação não existir ou não for estação
         */
        private Facility validateStation(Integer facilityId) {
            Facility facility = facilityRepository.getFacilityById(facilityId);
            if (facility == null) {
                throw new NoDirectConnectionException("Estação não encontrada: " + facilityId);
            }
            if (facility.getFacilityType() == null || facility.getFacilityType().getFacilityTypeId() != 2) {
                throw new NoDirectConnectionException("Instalação não é uma estação: " + facilityId);
            }
            return facility;
        }

        /**
         * Valida se uma locomotiva existe no repositório.
         *
         * @param locomotiveId ID da locomotiva a validar
         * @return locomotiva validada
         * @throws InvalidSpeedException se a locomotiva não existir
         */
        private Locomotive validateLocomotive(Integer locomotiveId) {
            Locomotive locomotive = locomotiveRepository.getLocomotiveById(locomotiveId);
            if (locomotive == null) {
                throw new InvalidSpeedException("Locomotiva não encontrada: " + locomotiveId);
            }
            return locomotive;
        }

    }

    /**
     * Exceção lançada quando não existe ligação ferroviária entre duas estações.
     */
    public static class NoDirectConnectionException extends RuntimeException {
        public NoDirectConnectionException(String message) {
            super(message);
        }
    }

    /**
     * Exceção lançada quando uma locomotiva tem velocidade inválida ou não existe.
     */
    public static class InvalidSpeedException extends RuntimeException {
        public InvalidSpeedException(String message) {
            super(message);
        }
    }
}
