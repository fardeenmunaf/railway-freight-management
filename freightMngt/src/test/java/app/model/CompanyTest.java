package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Company model.
 * Tests constructors, getters, setters, and business logic.
 */
public class CompanyTest {

    private Company company;

    @Before
    public void setUp() {
        company = new Company();
    }

    @Test
    public void testDefaultConstructor() {
        Company defaultCompany = new Company();

        assertNull("Company ID should be null", defaultCompany.getCompanyId());
        assertNull("Name should be null", defaultCompany.getName());
        assertNull("Short name should be null", defaultCompany.getShortName());
        assertNull("VAT should be null", defaultCompany.getVat());
    }

    @Test
    public void testParameterizedConstructor() {
        Company paramCompany = new Company(1, "ACME Corporation", "ACME", "PT123456789");

        assertEquals("Company ID should be 1", Integer.valueOf(1), paramCompany.getCompanyId());
        assertEquals("Name should be 'ACME Corporation'", "ACME Corporation", paramCompany.getName());
        assertEquals("Short name should be 'ACME'", "ACME", paramCompany.getShortName());
        assertEquals("VAT should be 'PT123456789'", "PT123456789", paramCompany.getVat());
    }

    @Test
    public void testSetAndGetCompanyId() {
        company.setCompanyId(10);

        assertEquals("Company ID should be 10", Integer.valueOf(10), company.getCompanyId());
    }

    @Test
    public void testSetAndGetName() {
        company.setName("Global Logistics Inc");

        assertEquals("Name should be 'Global Logistics Inc'", "Global Logistics Inc", company.getName());
    }

    @Test
    public void testSetAndGetShortName() {
        company.setShortName("GLI");

        assertEquals("Short name should be 'GLI'", "GLI", company.getShortName());
    }

    @Test
    public void testSetAndGetVat() {
        company.setVat("PT987654321");

        assertEquals("VAT should be 'PT987654321'", "PT987654321", company.getVat());
    }

    @Test
    public void testSetCompanyIdToNull() {
        company.setCompanyId(5);
        company.setCompanyId(null);

        assertNull("Company ID should be null", company.getCompanyId());
    }

    @Test
    public void testSetNameToNull() {
        company.setName("Test Company");
        company.setName(null);

        assertNull("Name should be null", company.getName());
    }

    @Test
    public void testSetShortNameToNull() {
        company.setShortName("TC");
        company.setShortName(null);

        assertNull("Short name should be null", company.getShortName());
    }

    @Test
    public void testSetVatToNull() {
        company.setVat("PT111111111");
        company.setVat(null);

        assertNull("VAT should be null", company.getVat());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        company.setCompanyId(3);
        company.setName("Transport Solutions Ltd");
        company.setShortName("TSL");
        company.setVat("PT555555555");

        assertEquals("Company ID should be 3", Integer.valueOf(3), company.getCompanyId());
        assertEquals("Name should be 'Transport Solutions Ltd'", "Transport Solutions Ltd", company.getName());
        assertEquals("Short name should be 'TSL'", "TSL", company.getShortName());
        assertEquals("VAT should be 'PT555555555'", "PT555555555", company.getVat());
    }

    @Test
    public void testConstructorWithNullValues() {
        Company nullCompany = new Company(null, null, null, null);

        assertNull("Company ID should be null", nullCompany.getCompanyId());
        assertNull("Name should be null", nullCompany.getName());
        assertNull("Short name should be null", nullCompany.getShortName());
        assertNull("VAT should be null", nullCompany.getVat());
    }

    @Test
    public void testConstructorWithEmptyStrings() {
        Company emptyCompany = new Company(1, "", "", "");

        assertEquals("Company ID should be 1", Integer.valueOf(1), emptyCompany.getCompanyId());
        assertEquals("Name should be empty string", "", emptyCompany.getName());
        assertEquals("Short name should be empty string", "", emptyCompany.getShortName());
        assertEquals("VAT should be empty string", "", emptyCompany.getVat());
    }

    @Test
    public void testToString() {
        company.setCompanyId(10);
        company.setName("Test Corp");
        company.setShortName("TC");
        company.setVat("PT123456789");

        String result = company.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain companyId", result.contains("companyId=10"));
        assertTrue("toString should contain name", result.contains("name='Test Corp'"));
        assertTrue("toString should contain shortName", result.contains("shortName='TC'"));
        assertTrue("toString should contain vat", result.contains("vat='PT123456789'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = company.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Company{'", result.contains("Company{"));
    }
}

