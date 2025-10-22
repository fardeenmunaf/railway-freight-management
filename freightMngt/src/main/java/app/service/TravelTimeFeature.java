package app.service;

import app.model.*;
import app.repositories.*;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TravelTimeFeature {

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

    public static void loadMockedData(
            FacilityRepository facilityRepo,
            FacilityTypeRepository facilityTypeRepo,
            LineSegmentRepository lineSegmentRepo,
            LineSegmentFacilityRepository lineSegmentFacilityRepo,
            LocomotiveRepository locomotiveRepo) {

        // 1. Criar tipo "Station"
        FacilityType stationType = new FacilityType();
        stationType.setFacilityTypeId(2);
        stationType.setName("Station");
        facilityTypeRepo.addFacilityType(stationType);

        // 2. Criar Lisboa
        Facility lisboa = new Facility();
        lisboa.setFacilityId(1);
        lisboa.setName("Lisboa Santa Apolónia");
        lisboa.setFacilityType(stationType);
        facilityRepo.addFacility(lisboa);
        System.out.println("✓ Lisboa adicionada - ID: " + lisboa.getFacilityId());

        // 3. Verificar se foi guardada
        Facility test = facilityRepo.getFacilityById(1);
        System.out.println("✓ Lisboa recuperada: " + (test != null ? test.getName() : "NULL!"));

        // 4. Criar Porto
        Facility porto = new Facility();
        porto.setFacilityId(2);
        porto.setName("Porto Campanhã");
        porto.setFacilityType(stationType);
        facilityRepo.addFacility(porto);
        System.out.println("✓ Porto adicionado - ID: " + porto.getFacilityId());

        // 5. Criar Coimbra
        Facility coimbra = new Facility();
        coimbra.setFacilityId(3);
        coimbra.setName("Coimbra-B");
        coimbra.setFacilityType(stationType);
        facilityRepo.addFacility(coimbra);
        System.out.println("✓ Coimbra adicionada - ID: " + coimbra.getFacilityId());

        // 6. Criar segmento Lisboa → Porto
        LineSegment seg1 = new LineSegment();
        seg1.setSegmentId(1);
        seg1.setLengthKm(200.0);
        seg1.setSpeedLimitKmh(120.0);
        lineSegmentRepo.addLineSegment(seg1);

        // 7. Criar segmento Porto → Coimbra
        LineSegment seg2 = new LineSegment();
        seg2.setSegmentId(2);
        seg2.setLengthKm(120.0);
        seg2.setSpeedLimitKmh(100.0);
        lineSegmentRepo.addLineSegment(seg2);

        // 8. Ligar Lisboa ao segmento 1
        LineSegmentFacility lsf1 = new LineSegmentFacility();
        lsf1.setSegmentFacilityId(1);
        lsf1.setLineSegment(seg1);
        lsf1.setFacility(lisboa);
        lineSegmentFacilityRepo.addLineSegmentFacility(lsf1);

        // 9. Ligar Porto ao segmento 1
        LineSegmentFacility lsf2 = new LineSegmentFacility();
        lsf2.setSegmentFacilityId(2);
        lsf2.setLineSegment(seg1);
        lsf2.setFacility(porto);
        lineSegmentFacilityRepo.addLineSegmentFacility(lsf2);

        // 10. Ligar Porto ao segmento 2
        LineSegmentFacility lsf3 = new LineSegmentFacility();
        lsf3.setSegmentFacilityId(3);
        lsf3.setLineSegment(seg2);
        lsf3.setFacility(porto);
        lineSegmentFacilityRepo.addLineSegmentFacility(lsf3);

        // 11. Ligar Coimbra ao segmento 2
        LineSegmentFacility lsf4 = new LineSegmentFacility();
        lsf4.setSegmentFacilityId(4);
        lsf4.setLineSegment(seg2);
        lsf4.setFacility(coimbra);
        lineSegmentFacilityRepo.addLineSegmentFacility(lsf4);

        // 12. Criar locomotivas
        Locomotive loco1 = new Locomotive();
        loco1.setLocomotiveId(1);
        loco1.setMaxSpeed(160.0);
        locomotiveRepo.addLocomotive(loco1);

        Locomotive loco2 = new Locomotive();
        loco2.setLocomotiveId(2);
        loco2.setMaxSpeed(220.0);
        locomotiveRepo.addLocomotive(loco2);

        System.out.println("✓ Mocked data carregada!");
    }

    public static void calculateTravelTimeFromUserInput(Scanner scanner) {
        System.out.println("\n=== Calculate Travel Time ===");

        try {
            System.out.print("Enter origin station ID: ");
            int originId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter destination station ID: ");
            int destinationId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter locomotive ID: ");
            int locomotiveId = Integer.parseInt(scanner.nextLine());

            Repositories repositories = Repositories.getInstance();
            TravelTimeCalculator calculator = TravelTimeFeature.create(
                    repositories.getFacilityRepository(),
                    repositories.getLineSegmentRepository(),
                    repositories.getLineSegmentFacilityRepository(),
                    repositories.getLocomotiveRepository()
            );

            double travelTime = calculator.calculateTravelTimeMinutes(originId, destinationId, locomotiveId);

            System.out.println("\n--- Travel Details ---");
            System.out.println("Distance: " + calculator.getDistanceKm() + " km");
            System.out.println("Effective Speed: " + calculator.getEffectiveSpeedKmh() + " km/h");
            System.out.println("Estimated Travel Time: " + travelTime + " minutes");
            System.out.println("----------------------\n");

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numeric IDs.");
        } catch (NoDirectConnectionException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InvalidSpeedException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    public static class TravelTimeCalculator {
        private final FacilityRepository facilityRepository;
        private final LineSegmentRepository lineSegmentRepository;
        private final LineSegmentFacilityRepository lineSegmentFacilityRepository;
        private final LocomotiveRepository locomotiveRepository;

        private LineSegment currentSegment;
        private double effectiveSpeed;
        private double distance;

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

        public double calculateTravelTimeMinutes(Integer stationId1, Integer stationId2, Integer locomotiveId) {
            Facility station1 = validateStation(stationId1);
            Facility station2 = validateStation(stationId2);
            Locomotive locomotive = validateLocomotive(locomotiveId);

            LineSegment segment = findDirectConnection(station1, station2);
            this.currentSegment = segment;

            double distanceKm = segment.getLengthKm() != null ? segment.getLengthKm() : 0.0;
            this.distance = distanceKm;

            if (distanceKm == 0.0) {
                this.effectiveSpeed = 0.0;
                return 0.0;
            }

            Double locomotiveMaxSpeed = locomotive.getMaxSpeed();
            if (locomotiveMaxSpeed == null || locomotiveMaxSpeed <= 0) {
                throw new InvalidSpeedException("Locomotiva com velocidade máxima inválida: " + locomotiveId);
            }

            Double segmentSpeedLimit = segment.getSpeedLimitKmh();
            double speedKmh = segmentSpeedLimit != null && segmentSpeedLimit > 0
                    ? Math.min(segmentSpeedLimit, locomotiveMaxSpeed)
                    : locomotiveMaxSpeed;

            if (speedKmh <= 0) {
                throw new InvalidSpeedException("Velocidade efetiva inválida para o cálculo");
            }

            this.effectiveSpeed = speedKmh;

            double hours = distanceKm / speedKmh;
            return hours * 60.0;
        }

        public double getEffectiveSpeedKmh() {
            return effectiveSpeed;
        }

        public double getDistanceKm() {
            return distance;
        }

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

        private Locomotive validateLocomotive(Integer lomotiveId) {
            Locomotive locomotive = locomotiveRepository.getLocomotiveById(lomotiveId);
            if (locomotive == null) {
                throw new InvalidSpeedException("Locomotiva não encontrada: " + lomotiveId);
            }
            return locomotive;
        }

        private LineSegment findDirectConnection(Facility station1, Facility station2) {
            List<LineSegment> segments = lineSegmentRepository.getAllLineSegments();

            for (LineSegment segment : segments) {
                List<LineSegmentFacility> facilities = lineSegmentFacilityRepository.getLineSegmentFacilitiesBySegmentId(segment.getSegmentId());

                if (facilities.size() == 2) {
                    Facility f1 = facilities.get(0).getFacility();
                    Facility f2 = facilities.get(1).getFacility();

                    if ((f1.equals(station1) && f2.equals(station2)) ||
                            (f1.equals(station2) && f2.equals(station1))) {
                        return segment;
                    }
                }
            }

            throw new NoDirectConnectionException(
                    "Não existe ligação direta entre " + station1.getFacilityId() + " e " + station2.getFacilityId());
        }
    }

    public static class NoDirectConnectionException extends RuntimeException {
        public NoDirectConnectionException(String message) {
            super(message);
        }
    }

    public static class InvalidSpeedException extends RuntimeException {
        public InvalidSpeedException(String message) {
            super(message);
        }
    }
}
