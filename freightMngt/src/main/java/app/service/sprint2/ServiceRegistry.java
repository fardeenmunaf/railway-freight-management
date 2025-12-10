package app.service.sprint2;

/**
 * Simple registry for shared services used by console views.
 * Holds a singleton StationService instance, so
 * indexes and spatial trees are built once and reused across views.
 */
public final class ServiceRegistry {

    private static StationService stationService;

    private ServiceRegistry() {
        // utility class; no instances
    }

    /**
     * Returns a shared StationService instance, building its indexes
     * on first access.
     */
    public static StationService getStationService() {
        if (stationService == null) {
            stationService = new StationService();
            stationService.buildIndexes();
        }
        return stationService;
    }
}

