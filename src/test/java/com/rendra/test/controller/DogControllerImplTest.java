package com.rendra.test.controller;

import com.rendra.test.controller.impl.DogControllerImpl;
import com.rendra.test.entity.Dog;
import com.rendra.test.dto.SuccessResponse;
import com.rendra.test.service.impl.DogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class DogControllerImplTest {

    @Mock
    private DogServiceImpl crudService;

    @InjectMocks
    private DogControllerImpl dogController;

    private final List<String> breeds = new ArrayList<>(Arrays.asList("subBreed1", "breed"));

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBreeds() {
        // Mock the behavior of the crudService.getAllBreeds() method
        Map<String, List<String>> breeds = Collections.singletonMap("breed", Collections.singletonList("subBreed"));
        when(crudService.getAllBreeds()).thenReturn(breeds);

        // Call the method being tested
        ResponseEntity<Map<String, List<String>>> response = dogController.getAllBreeds();

        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(breeds, response.getBody());

        // Verify that the crudService.getAllBreeds() method was called
        verify(crudService, times(1)).getAllBreeds();
    }

    @Test
    void testGetSubBreeds() {
        // Mock the input data
        String breed = "breed";

        // Mock the behavior of the crudService.getSubBreeds() method
        List<String> subBreeds = Collections.singletonList("subBreed");
        when(crudService.getSubBreeds(breed)).thenReturn(subBreeds);

        // Call the method being tested
        List<String> result = dogController.getSubBreeds(breed);

        // Verify the result
        assertNotNull(result);
        assertEquals(subBreeds, result);

        // Verify that the crudService.getSubBreeds() method was called
        verify(crudService, times(1)).getSubBreeds(breed);
    }

    @Test
    void testCreate() {
        // Mock the input data
        Dog dog = new Dog();
        dog.setBreed("breed");
        dog.setSubBreed(breeds);

        // Mock the behavior of the crudService.create() method
        ResponseEntity<SuccessResponse<Dog>> expectedResponse = ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "Success", dog));
        when(crudService.create(dog)).thenReturn(expectedResponse);

        // Call the method being tested
        ResponseEntity<SuccessResponse<Dog>> response = dogController.create(dog);

        // Verify the result
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        // Verify that the crudService.create() method was called
        verify(crudService, times(1)).create(dog);
    }

    @Test
    void testUpdate() {
        // Mock the input data
        Long id = 1L;
        Dog dog = new Dog();
        dog.setBreed("breed");
        dog.setSubBreed(breeds);

        // Mock the behavior of the crudService.update() method
        ResponseEntity<SuccessResponse<Dog>> expectedResponse = ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "Success", dog));
        when(crudService.update(id, dog)).thenReturn(expectedResponse);

        // Call the method being tested
        ResponseEntity<SuccessResponse<Dog>> response = dogController.update(id, dog);

        // Verify the result
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        // Verify that the crudService.update() method was called
        verify(crudService, times(1)).update(id, dog);
    }

    @Test
    void testDelete() {
        // Mock the input data
        Long id = 1L;

        // Call the method being tested
        ResponseEntity<Void> response = dogController.delete(id);

        // Verify the result
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify that the crudService.delete() method was called
        verify(crudService, times(1)).delete(id);
    }

    @Test
    void testGetAll() {
        // Mock the behavior of the crudService.getAll() method
        List<Dog> dogs = Collections.singletonList(new Dog());
        ResponseEntity<SuccessResponse<List<Dog>>> expectedResponse = ResponseEntity.ok(new SuccessResponse<>(HttpStatus.OK.value(), "Success", dogs));
        when(crudService.getAll()).thenReturn(expectedResponse);

        // Call the method being tested
        ResponseEntity<SuccessResponse<List<Dog>>> response = dogController.getAll();

        // Verify the result
        assertNotNull(response);
        assertEquals(expectedResponse, response);

        // Verify that the crudService.getAll() method was called
        verify(crudService, times(1)).getAll();
    }
}
