package app.test;

import app.examples.MSTInfrastructurePlannerExample;

/**
 * Simple test runner to verify the MST example works without errors.
 * Run with: java -cp target/classes app.test.MSTExampleTest
 */
public class MSTExampleTest {
    public static void main(String[] args) {
        try {
            System.out.println("🧪 Testing MST Infrastructure Planner Example...");

            MSTInfrastructurePlannerExample example = new MSTInfrastructurePlannerExample();

            // Test just the first scenario to verify directory creation works
            example.demonstrateConnectedNetwork();

            System.out.println("\n✅ MST example test completed successfully!");
            System.out.println("   The portuguese_railway_mst.dot file should now be correct with 5 unique edges.");

        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
