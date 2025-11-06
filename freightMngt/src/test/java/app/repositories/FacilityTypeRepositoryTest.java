package app.repositories;

import app.model.FacilityType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FacilityTypeRepositoryTest {

    private FacilityTypeRepository repository;
    private FacilityType facilityType1;
    private FacilityType facilityType2;
    private FacilityType facilityType3;

    @Before
    public void setUp() {
        repository = new FacilityTypeRepository();

        facilityType1 = new FacilityType(1, "Warehouse");
        facilityType2 = new FacilityType(2, "Distribution Center");
        facilityType3 = new FacilityType(3, "Terminal");
    }

    @Test
    public void testAddFacilityType() {
        repository.addFacilityType(facilityType1);

        List<FacilityType> facilityTypes = repository.getAllFacilityTypes();
        assertEquals(1, facilityTypes.size());
        assertTrue(facilityTypes.contains(facilityType1));
    }

    @Test
    public void testAddFacilityTypeWithNull() {
        repository.addFacilityType(null);

        List<FacilityType> facilityTypes = repository.getAllFacilityTypes();
        assertEquals(0, facilityTypes.size());
    }

    @Test
    public void testAddDuplicateFacilityType() {
        repository.addFacilityType(facilityType1);
        repository.addFacilityType(facilityType1);

        List<FacilityType> facilityTypes = repository.getAllFacilityTypes();
        assertEquals(1, facilityTypes.size());
    }

    @Test
    public void testGetAllFacilityTypes() {
        repository.addFacilityType(facilityType1);
        repository.addFacilityType(facilityType2);
        repository.addFacilityType(facilityType3);

        List<FacilityType> facilityTypes = repository.getAllFacilityTypes();

        assertEquals(3, facilityTypes.size());
        assertTrue(facilityTypes.contains(facilityType1));
        assertTrue(facilityTypes.contains(facilityType2));
        assertTrue(facilityTypes.contains(facilityType3));
    }

    @Test
    public void testGetAllFacilityTypesEmpty() {
        List<FacilityType> facilityTypes = repository.getAllFacilityTypes();

        assertNotNull(facilityTypes);
        assertEquals(0, facilityTypes.size());
    }

    @Test
    public void testGetFacilityTypeByIdFound() {
        repository.addFacilityType(facilityType1);
        repository.addFacilityType(facilityType2);

        FacilityType result = repository.getFacilityTypeById(1);

        assertNotNull(result);
        assertEquals(facilityType1, result);
    }

    @Test
    public void testGetFacilityTypeByIdNotFound() {
        repository.addFacilityType(facilityType1);

        FacilityType result = repository.getFacilityTypeById(999);

        assertNull(result);
    }

    @Test
    public void testUpdateFacilityTypeSuccess() {
        repository.addFacilityType(facilityType1);

        FacilityType updatedFacilityType = new FacilityType(1, "Updated Warehouse");

        boolean result = repository.updateFacilityType(1, updatedFacilityType);

        assertTrue(result);
        FacilityType retrievedFacilityType = repository.getFacilityTypeById(1);
        assertNotNull(retrievedFacilityType);
        assertEquals("Updated Warehouse", retrievedFacilityType.getName());
    }

    @Test
    public void testUpdateFacilityTypeNotFound() {
        repository.addFacilityType(facilityType1);

        boolean result = repository.updateFacilityType(999, facilityType2);

        assertFalse(result);
    }

    @Test
    public void testDeleteFacilityTypeSuccess() {
        repository.addFacilityType(facilityType1);
        repository.addFacilityType(facilityType2);
        repository.addFacilityType(facilityType3);

        boolean result = repository.deleteFacilityType(2);

        assertTrue(result);
        assertEquals(2, repository.getAllFacilityTypes().size());
        assertNull(repository.getFacilityTypeById(2));
    }

    @Test
    public void testDeleteFacilityTypeNotFound() {
        repository.addFacilityType(facilityType1);

        boolean result = repository.deleteFacilityType(999);

        assertFalse(result);
        assertEquals(1, repository.getAllFacilityTypes().size());
    }

    @Test
    public void testGetNextFacilityTypeId() {
        repository.addFacilityType(facilityType1);
        repository.addFacilityType(facilityType2);

        Integer nextId = repository.getNextFacilityTypeId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextFacilityTypeIdEmpty() {
        Integer nextId = repository.getNextFacilityTypeId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testClear() {
        repository.addFacilityType(facilityType1);
        repository.addFacilityType(facilityType2);
        repository.addFacilityType(facilityType3);
        assertEquals(3, repository.getAllFacilityTypes().size());

        repository.clear();

        assertEquals(0, repository.getAllFacilityTypes().size());
    }

    @Test
    public void testConstructor() {
        FacilityTypeRepository newRepository = new FacilityTypeRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllFacilityTypes());
    }

    @Test
    public void testGetFacilityTypeByName() {
        repository.addFacilityType(facilityType1);
        repository.addFacilityType(facilityType2);

        FacilityType result = repository.getFacilityTypeByName("Warehouse");

        assertNotNull(result);
        assertEquals(facilityType1, result);
    }

    @Test
    public void testGetFacilityTypeByNameCaseInsensitive() {
        repository.addFacilityType(facilityType1);

        FacilityType result = repository.getFacilityTypeByName("WAREHOUSE");

        assertNotNull(result);
        assertEquals("Warehouse", result.getName());
    }

    @Test
    public void testGetFacilityTypeByNameNotFound() {
        repository.addFacilityType(facilityType1);

        FacilityType result = repository.getFacilityTypeByName("Nonexistent");

        assertNull(result);
    }

    @Test
    public void testGetFacilityTypeByNameWithNull() {
        FacilityType result = repository.getFacilityTypeByName(null);

        assertNull(result);
    }
}

