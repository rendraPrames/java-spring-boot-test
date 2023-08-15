package com.rendra.test.service;

import com.rendra.test.dto.DogApiResponse;
import com.rendra.test.dto.DogSubBreedResponse;
import com.rendra.test.entity.Dog;
import com.rendra.test.dto.SuccessResponse;
import com.rendra.test.exception.NotFoundException;
import com.rendra.test.repository.DogRepository;
import com.rendra.test.service.impl.DogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DogServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DogRepository dogRepository;

    @InjectMocks
    private DogServiceImpl dogService;
    private final List<String> breeds = new ArrayList<>(Arrays.asList("subBreed1", "breed"));

    private final String dogApiBaseUrl = "https://api.dogapi.com";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(dogService, "dogApiBaseUrl", dogApiBaseUrl);
        int breedSubBreedListTimeout = 5000;
        ReflectionTestUtils.setField(dogService, "breedSubBreedListTimeout", breedSubBreedListTimeout);
    }

    @Test
    void testGetSubBreeds_SuccessfulResponse() {
        // Mock the input data
        String breed = "breed";
        String url = dogApiBaseUrl + "/breed/" + breed + "/list";

        // Mock the behavior
        DogSubBreedResponse responseBody = new DogSubBreedResponse();
        responseBody.setSubBreeds(Collections.singletonList("subBreed"));
        ResponseEntity<DogSubBreedResponse> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Call the method being tested
        List<String> result = dogService.getSubBreeds(breed);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("subBreed", result.get(0));

        // Verify that the restTemplate.exchange() method was called
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetSubBreeds_EmptyResponse() {
        // Mock the input data
        String breed = "breed";
        String url = dogApiBaseUrl + "/breed/" + breed + "/list";

        // Mock the behavior
        ResponseEntity<DogSubBreedResponse> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Call the method being tested
        List<String> result = dogService.getSubBreeds(breed);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify that the restTemplate.exchange() method was called
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetSubBreeds_HttpClientErrorException() {
        // Mock the input data
        String breed = "breed";
        String url = dogApiBaseUrl + "/breed/" + breed + "/list";

        // Mock the behavior
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Call the method being tested and verify that DogApiException is thrown
        assertThrows(NotFoundException.class, () -> dogService.getSubBreeds(breed));

        // Verify that the restTemplate.exchange() method was called
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetAllBreeds_Success() {
        // Mock the response from the Dog API
        DogApiResponse apiResponse = new DogApiResponse();
        Map<String, List<String>> breedsMap = new HashMap<>();
        breedsMap.put("breed1", Arrays.asList("subBreed1", "subBreed2"));
        breedsMap.put("breed2", Arrays.asList("subBreed3", "subBreed4"));
        apiResponse.setMessage(breedsMap);
        ResponseEntity<DogApiResponse> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Call the method being tested
        Map<String, List<String>> result = dogService.getAllBreeds();

        // Verify the result
        assertEquals(breedsMap, result);
    }

    @Test
    void testGetAllBreeds_EmptyResponse() {
        // Mock the response from the Dog API
        ResponseEntity<DogApiResponse> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        // Call the method being tested
        Map<String, List<String>> result = dogService.getAllBreeds();

        // Verify the result
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreate_Success() {
        // Mock the dog object and repository behavior
        Dog dog = new Dog();
        dog.setBreed("breed1");
        when(dogRepository.existsByBreed(anyString())).thenReturn(false);
        when(dogRepository.save(any(Dog.class))).thenReturn(dog);

        // Call the method being tested
        ResponseEntity<SuccessResponse<Dog>> response = dogService.create(dog);

        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dog, response.getBody().getData());

        // Verify that the dogRepository.save() method was called
        verify(dogRepository, times(1)).save(dog);
    }

    @Test
    void testCreate_DogAlreadyExists() {
        // Mock the dog object and repository behavior
        Dog dog = new Dog();
        dog.setBreed("breed1");
        when(dogRepository.existsByBreed(anyString())).thenReturn(true);

        // Call the method being tested and verify that it throws the expected exception
        assertThrows(NotFoundException.class, () -> dogService.create(dog));

        // Verify that the dogRepository.save() method was not called
        verify(dogRepository, never()).save(any(Dog.class));
    }

    @Test
    void testUpdate_Success() {
        // Mock the input data
        Long id = 1L;
        Dog existingDog = new Dog();
        existingDog.setId(id);
        existingDog.setBreed("breed1");
        existingDog.setSubBreed(breeds);

        Dog updatedDog = new Dog();
        updatedDog.setId(id);
        updatedDog.setBreed("updatedBreed");
        updatedDog.setSubBreed(breeds);

        // Mock the behavior of the dogRepository.findById() method
        when(dogRepository.findById(id)).thenReturn(Optional.of(existingDog));

        // Mock the behavior of the dogRepository.save() method
        when(dogRepository.save(any(Dog.class))).thenReturn(updatedDog);

        // Call the method being tested
        ResponseEntity<SuccessResponse<Dog>> response = dogService.update(id, updatedDog);

        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedDog, response.getBody().getData());

        // Verify that the dogRepository.findById() and dogRepository.save() methods were called
        verify(dogRepository, times(1)).findById(id);
        verify(dogRepository, times(1)).save(existingDog);
    }

    @Test
    void testUpdate_DogNotFound() {
        // Mock the input data
        Long id = 1L;
        Dog updatedDog = new Dog();
        updatedDog.setId(id);
        updatedDog.setBreed("updatedBreed");
        updatedDog.setSubBreed(breeds);

        // Mock the behavior of the dogRepository.findById() method
        when(dogRepository.findById(id)).thenReturn(Optional.empty());

        // Call the method being tested and verify that it throws the expected exception
        assertThrows(NotFoundException.class, () -> dogService.update(id, updatedDog));

        // Verify that the dogRepository.findById() method was called
        verify(dogRepository, times(1)).findById(id);

        // Verify that the dogRepository.save() method was not called
        verify(dogRepository, never()).save(any(Dog.class));
    }


    @Test
    void testDelete() {
        // Mock the input data
        Long id = 1L;

        // Call the method being tested
        dogService.delete(id);

        // Verify that the dogRepository.deleteById() method was called
        verify(dogRepository, times(1)).deleteById(id);
    }

    @Test
    void testGetAll() {
        // Mock the behavior of the dogRepository.findAll() method
        List<Dog> dogs = new ArrayList<>();
        dogs.add(new Dog(1L, "breed1", breeds));
        dogs.add(new Dog(2L, "breed2", breeds));
        when(dogRepository.findAll()).thenReturn(dogs);

        // Call the method being tested
        ResponseEntity<SuccessResponse<List<Dog>>> response = dogService.getAll();

        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dogs, response.getBody().getData());

        // Verify that the dogRepository.findAll() method was called
        verify(dogRepository, times(1)).findAll();
    }
    @Test
    void testCallDogApi_HttpClientErrorException() {
        // Mock the input data
        String url = "https://api.dog.com/breeds/list/all";
        int timeout = 5000;

        // Mock the behavior of the restTemplate.exchange() method to throw HttpClientErrorException
        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                (ParameterizedTypeReference<Object>) any()
        )).thenThrow(HttpClientErrorException.class);

        // Call the method being tested and verify that it throws the expected exception
        assertThrows(NotFoundException.class, () -> dogService.callDogApi(url, timeout));

        // Verify that the restTemplate.exchange() method was called
        verify(restTemplate, times(1)).exchange(
                anyString(),
                any(),
                any(),
                (ParameterizedTypeReference<Object>) any()
        );
    }


}
