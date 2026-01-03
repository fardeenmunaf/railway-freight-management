package app.test;

import app.view.MSTInfrastructurePlannerDemo;

/**
 * Test runner for USEI12 - Minimal Backbone Network (Belgian Railway System).
 *
 * Verifies the MST demonstration works correctly with Belgian stations.
 * Run with: java -cp target/classes app.test.MSTExampleTest
 */
public class MSTExampleTest {
    public static void main(String[] args) {
        try {
            System.out.println("🧪 Testing USEI12: Minimal Backbone Network - Belgian Railway System...\n");

            MSTInfrastructurePlannerDemo demo = new MSTInfrastructurePlannerDemo();

            // Test the Belgian railway network scenario
            demo.demonstrateConnectedNetwork();

            System.out.println("\n✅ Belgian railway MST test completed successfully!");
            System.out.println("   The belgian_railway_mst.dot file should be generated with:");
            System.out.println("   - All Belgian railway stations from stations.csv");
            System.out.println("   - MST edges connecting all reachable stations");
            System.out.println("   - XY coordinates based on actual station locations");
            System.out.println("   - Compatible with GraphViz neato for SVG generation");
            System.out.println("\n   Reference: https://opendata.infrabel.be/explore/dataset/station_to_station/");

        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
