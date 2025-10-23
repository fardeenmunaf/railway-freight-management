package app.controller;

import app.model.Company;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CompanyControllerTest {

    private CompanyController controller;
    private Company company1;
    private Company company2;

    @Before
    public void setUp() {
        controller = new CompanyController();
        controller.clearAllCompanies();
        company1 = new Company(1, "Company A", "CA", "123456789");
        company2 = new Company(2, "Company B", "CB", "987654321");
    }

    @Test
    public void testCreateCompany() {
        Company result = controller.createCompany(company1);

        assertNotNull(result);
        assertEquals(company1, result);
        assertEquals(1, controller.getAllCompanies().size());
    }

    @Test
    public void testCreateCompanyWithNull() {
        Company result = controller.createCompany(null);

        assertNull(result);
        assertEquals(0, controller.getAllCompanies().size());
    }

    @Test
    public void testGetAllCompanies() {
        controller.createCompany(company1);
        controller.createCompany(company2);

        List<Company> companies = controller.getAllCompanies();

        assertEquals(2, companies.size());
        assertTrue(companies.contains(company1));
        assertTrue(companies.contains(company2));
    }

    @Test
    public void testGetAllCompaniesEmpty() {
        List<Company> companies = controller.getAllCompanies();

        assertNotNull(companies);
        assertEquals(0, companies.size());
    }

    @Test
    public void testGetCompanyByIdFound() {
        controller.createCompany(company1);
        controller.createCompany(company2);

        Optional<Company> result = controller.getCompanyById(1);

        assertTrue(result.isPresent());
        assertEquals(company1, result.get());
    }

    @Test
    public void testGetCompanyByIdNotFound() {
        controller.createCompany(company1);

        Optional<Company> result = controller.getCompanyById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetCompanyByNameFound() {
        controller.createCompany(company1);
        controller.createCompany(company2);

        Optional<Company> result = controller.getCompanyByName("Company A");

        assertTrue(result.isPresent());
        assertEquals(company1, result.get());
    }

    @Test
    public void testGetCompanyByNameNotFound() {
        controller.createCompany(company1);

        Optional<Company> result = controller.getCompanyByName("Non-existent Company");

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateCompanySuccess() {
        controller.createCompany(company1);
        Company updatedCompany = new Company(1, "Company A Updated", "CAU", "111111111");

        boolean result = controller.updateCompany(1, updatedCompany);

        assertTrue(result);
        Optional<Company> retrievedCompany = controller.getCompanyById(1);
        assertTrue(retrievedCompany.isPresent());
        assertEquals("Company A Updated", retrievedCompany.get().getName());
        assertEquals("CAU", retrievedCompany.get().getShortName());
        assertEquals("111111111", retrievedCompany.get().getVat());
    }

    @Test
    public void testUpdateCompanyNotFound() {
        controller.createCompany(company1);
        Company updatedCompany = new Company(999, "Non-existent Company", "NEC", "000000000");

        boolean result = controller.updateCompany(999, updatedCompany);

        assertFalse(result);
    }

    @Test
    public void testDeleteCompanySuccess() {
        controller.createCompany(company1);
        controller.createCompany(company2);

        boolean result = controller.deleteCompany(1);

        assertTrue(result);
        assertEquals(1, controller.getAllCompanies().size());
        assertFalse(controller.getCompanyById(1).isPresent());
    }

    @Test
    public void testDeleteCompanyNotFound() {
        controller.createCompany(company1);

        boolean result = controller.deleteCompany(999);

        assertFalse(result);
        assertEquals(1, controller.getAllCompanies().size());
    }

    @Test
    public void testGetNextCompanyId() {
        controller.createCompany(company1);
        controller.createCompany(company2);

        Integer nextId = controller.getNextCompanyId();

        assertNotNull(nextId);
        assertTrue(nextId > 2);
    }

    @Test
    public void testContainsNameTrue() {
        controller.createCompany(company1);

        boolean result = controller.containsName("Company A");

        assertTrue(result);
    }

    @Test
    public void testContainsNameFalse() {
        controller.createCompany(company1);

        boolean result = controller.containsName("Non-existent Company");

        assertFalse(result);
    }

    @Test
    public void testClearAllCompanies() {
        controller.createCompany(company1);
        controller.createCompany(company2);
        assertEquals(2, controller.getAllCompanies().size());

        controller.clearAllCompanies();

        assertEquals(0, controller.getAllCompanies().size());
    }

    @Test
    public void testConstructor() {
        CompanyController newController = new CompanyController();

        assertNotNull(newController);
        assertNotNull(newController.getAllCompanies());
    }
}

