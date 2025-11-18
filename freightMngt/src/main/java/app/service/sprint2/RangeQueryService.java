package app.service.sprint2;

import app.model.Station;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para consultas por intervalo em latitude/longitude usando KD-tree balanceado.
 * Filtros opcionais: isCity, isMainStation, country (use "ALL" ou null para aceitar todos).
 */
public class RangeQueryService {

    private KDNode root;
    private int distinctNodeCount = 0;

    private static class KDNode {
        final double key;
        final boolean isLatitude;
        final List<Station> bucket; // estações que partilham a mesma chave nesta dimensão
        KDNode left;
        KDNode right;
        int height;

        KDNode(double key, boolean isLatitude, List<Station> bucket) {
            this.key = key;
            this.isLatitude = isLatitude;
            this.bucket = bucket;
            this.height = 1;
        }

        void updateHeight() {
            int lh = (left == null) ? 0 : left.height;
            int rh = (right == null) ? 0 : right.height;
            this.height = 1 + Math.max(lh, rh);
        }
    }

    /**
     * Constrói o KD-tree balanceado a partir da lista de estações.
     * Esta operação deve ser chamada sempre que o conjunto de estações mudar.
     */
    public void buildIndex(List<Station> stations) {
        List<Station> filtered = stations.stream()
                .filter(s -> s.getLat() != null && s.getLon() != null)
                .collect(Collectors.toList());
        distinctNodeCount = 0;
        root = buildRecursive(filtered, true);
    }

    private KDNode buildRecursive(List<Station> stations, boolean useLatitude) {
        if (stations == null || stations.isEmpty()) return null;

        // obter chaves únicas e ordenadas para a dimensão atual
        List<Double> uniqueKeys = stations.stream()
                .map(s -> useLatitude ? s.getLat() : s.getLon())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        int medianIndex = uniqueKeys.size() / 2;
        double medianKey = uniqueKeys.get(medianIndex);

        List<Station> leftList = new ArrayList<>();
        List<Station> equalList = new ArrayList<>();
        List<Station> rightList = new ArrayList<>();

        for (Station s : stations) {
            double key = useLatitude ? s.getLat() : s.getLon();
            int cmp = Double.compare(key, medianKey);
            if (cmp < 0) leftList.add(s);
            else if (cmp == 0) equalList.add(s);
            else rightList.add(s);
        }

        // ordenar bucket por nome para comportamento estável
        equalList.sort(Comparator.comparing(Station::getName, String.CASE_INSENSITIVE_ORDER));

        KDNode node = new KDNode(medianKey, useLatitude, equalList);
        distinctNodeCount++;

        node.left = buildRecursive(leftList, !useLatitude);
        node.right = buildRecursive(rightList, !useLatitude);
        node.updateHeight();
        return node;
    }

    /**
     * Consulta por intervalo.
     *
     * @param latMin minimum latitude (inclusive)
     * @param latMax maximum latitude (inclusive)
     * @param lonMin minimum longitude (inclusive)
     * @param lonMax maximum longitude (inclusive)
     * @param isCity filter for isCity (null = ignore)
     * @param isMainStation filter for isMainStation (null = ignore)
     * @param country filter for country ("ALL" or null = ignore)
     * @return lista de estações que caem no retângulo e passam nos filtros
     */
    public List<Station> rangeQuery(double latMin, double latMax,
                                    double lonMin, double lonMax,
                                    Boolean isCity, Boolean isMainStation,
                                    String country) {
        List<Station> results = new ArrayList<>();
        rangeSearchRecursive(root, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, results);
        return results;
    }

    private void rangeSearchRecursive(KDNode node,
                                      double latMin, double latMax,
                                      double lonMin, double lonMax,
                                      Boolean isCity, Boolean isMainStation,
                                      String country,
                                      List<Station> out) {
        if (node == null) return;

        if (node.isLatitude) {
            if (node.key < latMin) {
                // toda subárvore esquerda fica fora
                rangeSearchRecursive(node.right, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
                return;
            }
            if (node.key > latMax) {
                rangeSearchRecursive(node.left, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
                return;
            }
            // nodo dentro do intervalo latitude: verificar bucket e ambas as subárvores
            addBucketIfMatches(node.bucket, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
            rangeSearchRecursive(node.left, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
            rangeSearchRecursive(node.right, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
        } else {
            // poda por longitude
            if (node.key < lonMin) {
                rangeSearchRecursive(node.right, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
                return;
            }
            if (node.key > lonMax) {
                rangeSearchRecursive(node.left, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
                return;
            }
            addBucketIfMatches(node.bucket, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
            rangeSearchRecursive(node.left, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
            rangeSearchRecursive(node.right, latMin, latMax, lonMin, lonMax, isCity, isMainStation, country, out);
        }
    }

    private void addBucketIfMatches(List<Station> bucket,
                                    double latMin, double latMax,
                                    double lonMin, double lonMax,
                                    Boolean isCity, Boolean isMainStation,
                                    String country,
                                    List<Station> out) {
        if (bucket == null || bucket.isEmpty()) return;
        for (Station s : bucket) {
            if (s.getLat() == null || s.getLon() == null) continue;
            double lat = s.getLat();
            double lon = s.getLon();
            if (lat < latMin || lat > latMax || lon < lonMin || lon > lonMax) continue;
            if (!matchesFilters(s, isCity, isMainStation, country)) continue;
            out.add(s);
        }
    }

    private boolean matchesFilters(Station s, Boolean isCity, Boolean isMainStation, String country) {
        if (isCity != null) {
            Boolean v = s.getIsCity();
            if (v == null || !v.equals(isCity)) return false;
        }
        if (isMainStation != null) {
            Boolean v = s.getIsMainStation();
            if (v == null || !v.equals(isMainStation)) return false;
        }
        if (country != null && !"ALL".equalsIgnoreCase(country)) {
            String c = s.getCountry();
            if (c == null) return false;
            if (!c.equalsIgnoreCase(country)) return false;
        }
        return true;
    }

    /**
     * Número de nós distintos (distintas chaves por dimensão) no KD-tree.
     */
    public int getDistinctNodeCount() {
        return distinctNodeCount;
    }

    /**
     * Altura aproximada do KD-tree (0 quando vazio).
     */
    public int getHeight() {
        return root == null ? 0 : root.height;
    }
}