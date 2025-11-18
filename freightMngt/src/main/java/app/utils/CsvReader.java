package app.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading CSV (Comma-Separated Values) files.
 * Provides static methods to parse CSV files into structured data collections.
 * The first line of the CSV file is treated as the header row containing column names.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class CsvReader {

    /**
     * Reads a CSV file and parses it into a list of maps.
     * Each map represents a row where keys are column headers and values are the corresponding cell values.
     * The first line of the file is treated as the header row.
     *
     * @param filePath the path to the CSV file to read
     * @param delimiter the delimiter character used to separate values (e.g., "," or ";")
     * @return a list of maps, where each map represents a row with header-value pairs
     */
    public static List<Map<String, String>> readCsv(String filePath, String delimiter) {
        List<Map<String, String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) return records;

            // Remove BOM (Byte Order Mark) if present
            if (line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }

            String[] headers = parseCsvLine(line, delimiter);

            while ((line = br.readLine()) != null) {
                String[] values = parseCsvLine(line, delimiter);
                Map<String, String> record = new HashMap<>();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    record.put(headers[i].trim(), values[i].trim());
                }
                records.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * Parses a CSV line respecting quoted fields.
     *
     * @param line the CSV line to parse
     * @param delimiter the delimiter character
     * @return array of field values
     */
    private static String[] parseCsvLine(String line, String delimiter) {
        List<String> result = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == delimiter.charAt(0) && !insideQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        result.add(currentField.toString());

        return result.toArray(new String[0]);
    }
}
