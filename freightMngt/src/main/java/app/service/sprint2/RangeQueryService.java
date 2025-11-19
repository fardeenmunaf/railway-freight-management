package app.service.sprint2;

import app.model.Station;
import app.utils.Balanced2DTreeBuilder.Balanced2DTree;

import java.util.List;

public class RangeQueryService {

    private final Balanced2DTree tree;

    public RangeQueryService(Balanced2DTree tree) {
        this.tree = tree;
    }

    /**
     * Range query sobre a árvore 2D com filtros opcionais.
     */
    public List<Station> rangeQuery(double latMin, double latMax,
                                    double lonMin, double lonMax,
                                    Boolean isCity, Boolean isMainStation,
                                    String country) {

        if (tree == null)
            throw new IllegalStateException("2D Tree not initialized.");

        return tree.rangeSearch(latMin, latMax, lonMin, lonMax,
                isCity, isMainStation, country);
    }
}