package app.repositories;

import app.model.FacilityFeatureType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FacilityFeatureTypeRepositoryTest {

    private FacilityFeatureTypeRepository repository;
    private FacilityFeatureType featureType1;
    private FacilityFeatureType featureType2;
    private FacilityFeatureType featureType3;

    @Before
    public void setUp() {
        repository = new FacilityFeatureTypeRepository();

        featureType1 = new FacilityFeatureType(1, "Loading Dock");
        featureType2 = new FacilityFeatureType(2, "Unloading Dock");
        featureType3 = new FacilityFeatureType(3, "Storage Area");
    }

    @Test
    public void testAddFacilityFeatureType() {
        repository.addFacilityFeatureType(featureType1);

        List<FacilityFeatureType> featureTypes = repository.getAllFacilityFeatureTypes();
        assertEquals(1, featureTypes.size());
        assertTrue(featureTypes.contains(featureType1));
    }

    @Test
    public void testAddFacilityFeatureTypeWithNull() {
        repository.addFacilityFeatureType(null);

        List<FacilityFeatureType> featureTypes = repository.getAllFacilityFeatureTypes();
        assertEquals(0, featureTypes.size());
    }

    @Test
    public void testAddDuplicateFacilityFeatureType() {
        repository.addFacilityFeatureType(featureType1);
        repository.addFacilityFeatureType(featureType1);

        List<FacilityFeatureType> featureTypes = repository.getAllFacilityFeatureTypes();
        assertEquals(1, featureTypes.size());
    }

    @Test
    public void testGetAllFacilityFeatureTypes() {
        repository.addFacilityFeatureType(featureType1);
        repository.addFacilityFeatureType(featureType2);
        repository.addFacilityFeatureType(featureType3);

        List<FacilityFeatureType> featureTypes = repository.getAllFacilityFeatureTypes();

        assertEquals(3, featureTypes.size());
        assertTrue(featureTypes.contains(featureType1));
        assertTrue(featureTypes.contains(featureType2));
        assertTrue(featureTypes.contains(featureType3));
    }

    @Test
    public void testGetAllFacilityFeatureTypesEmpty() {
        List<FacilityFeatureType> featureTypes = repository.getAllFacilityFeatureTypes();

        assertNotNull(featureTypes);
        assertEquals(0, featureTypes.size());
    }

    @Test
    public void testGetFacilityFeatureTypeByIdFound() {
        repository.addFacilityFeatureType(featureType1);
        repository.addFacilityFeatureType(featureType2);

        FacilityFeatureType result = repository.getFacilityFeatureTypeById(1);

        assertNotNull(result);
        assertEquals(featureType1, result);
    }

    @Test
    public void testGetFacilityFeatureTypeByIdNotFound() {
        repository.addFacilityFeatureType(featureType1);

        FacilityFeatureType result = repository.getFacilityFeatureTypeById(999);

        assertNull(result);
    }

    @Test
    public void testUpdateFacilityFeatureTypeSuccess() {
        repository.addFacilityFeatureType(featureType1);

        FacilityFeatureType updatedFeatureType = new FacilityFeatureType(1, "Updated Loading Dock");

        boolean result = repository.updateFacilityFeatureType(1, updatedFeatureType);

        assertTrue(result);
        FacilityFeatureType retrievedFeatureType = repository.getFacilityFeatureTypeById(1);
        assertNotNull(retrievedFeatureType);
        assertEquals("Updated Loading Dock", retrievedFeatureType.getName());
    }

    @Test
    public void testUpdateFacilityFeatureTypeNotFound() {
        repository.addFacilityFeatureType(featureType1);

        boolean result = repository.updateFacilityFeatureType(999, featureType2);

        assertFalse(result);
    }

    @Test
    public void testDeleteFacilityFeatureTypeSuccess() {
        repository.addFacilityFeatureType(featureType1);
        repository.addFacilityFeatureType(featureType2);
        repository.addFacilityFeatureType(featureType3);

        boolean result = repository.deleteFacilityFeatureType(2);

        assertTrue(result);
        assertEquals(2, repository.getAllFacilityFeatureTypes().size());
        assertNull(repository.getFacilityFeatureTypeById(2));
    }

    @Test
    public void testDeleteFacilityFeatureTypeNotFound() {
        repository.addFacilityFeatureType(featureType1);

        boolean result = repository.deleteFacilityFeatureType(999);

        assertFalse(result);
        assertEquals(1, repository.getAllFacilityFeatureTypes().size());
    }

    @Test
    public void testGetNextFeatureTypeId() {
        repository.addFacilityFeatureType(featureType1);
        repository.addFacilityFeatureType(featureType2);

        Integer nextId = repository.getNextFeatureTypeId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextFeatureTypeIdEmpty() {
        Integer nextId = repository.getNextFeatureTypeId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testClear() {
        repository.addFacilityFeatureType(featureType1);
        repository.addFacilityFeatureType(featureType2);
        repository.addFacilityFeatureType(featureType3);
        assertEquals(3, repository.getAllFacilityFeatureTypes().size());

        repository.clear();

        assertEquals(0, repository.getAllFacilityFeatureTypes().size());
    }

    @Test
    public void testConstructor() {
        FacilityFeatureTypeRepository newRepository = new FacilityFeatureTypeRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllFacilityFeatureTypes());
    }
}

