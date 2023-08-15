package com.rendra.test.service.impl;

import com.rendra.test.dto.DogApiResponse;
import com.rendra.test.dto.DogSubBreedResponse;
import com.rendra.test.entity.Dog;
import com.rendra.test.dto.SuccessResponse;
import com.rendra.test.exception.NotFoundException;
import com.rendra.test.repository.DogRepository;
import com.rendra.test.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DogServiceImpl implements CrudService<Dog, Long> {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DogRepository dogRepository;

    @Value("${dog.api.base-url}")
    private String dogApiBaseUrl;

    @Value("${dog.api.timeout.breeds-list-all}")
    private int breedsListAllTimeout;

    @Value("${dog.api.timeout.breed-sub-breed-list}")
    private int breedSubBreedListTimeout;

    private static final String SUCCESS_MESSAGE = "Success";

    private ClientHttpRequestFactory getTimeoutRequestFactory(int timeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);
        return requestFactory;
    }

    public Map<String, List<String>> getAllBreeds() {
        String url = dogApiBaseUrl + "/breeds/list/all";
        ResponseEntity<DogApiResponse> response = callDogApi(url, breedsListAllTimeout);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, List<String>> breedsMap = Objects.requireNonNull(response.getBody()).getMessage();
            return breedsMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
        }

        return Collections.emptyMap();
    }

    public List<String> getSubBreeds(String breed) {
        String url = UriComponentsBuilder.fromUriString(dogApiBaseUrl)
                .pathSegment("breed", breed, "list")
                .toUriString();

        restTemplate.setRequestFactory(getTimeoutRequestFactory(breedSubBreedListTimeout));

        try {
            ResponseEntity<DogSubBreedResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                DogSubBreedResponse responseBody = response.getBody();
                if (responseBody != null) {
                    return responseBody.getSubBreeds();
                }
            }
        } catch (HttpClientErrorException e) {
            throw new NotFoundException("Failed to retrieve sub-breeds for breed " + breed + " from Dog API.", e);
        }

        return Collections.emptyList();
    }

    @Override
    public ResponseEntity<SuccessResponse<Dog>> create(Dog dog) {
        if (dogRepository.existsByBreed(dog.getBreed())) {
            throw new NotFoundException("Dog already exists");
        }

        Dog newDog = dogRepository.save(dog);
        SuccessResponse<Dog> successResponse = new SuccessResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, newDog);
        return ResponseEntity.ok(successResponse);
    }


    @Override
    public ResponseEntity<SuccessResponse<Dog>> update(Long id, Dog dog) {
        Dog existingDog = dogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dog not found with id: " + id));

        existingDog.setBreed(dog.getBreed());
        existingDog.setSubBreed(dog.getSubBreed());
        Dog newDog = dogRepository.save(existingDog);
        SuccessResponse<Dog> successResponse = new SuccessResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, newDog);
        return ResponseEntity.ok(successResponse);
    }

    @Override
    public void delete(Long id) {
        dogRepository.deleteById(id);
    }

    @Override
    public ResponseEntity<SuccessResponse<List<Dog>>> getAll() {
        List<Dog> dogs = new ArrayList<>();
        dogRepository.findAll().forEach(dogs::add);
        SuccessResponse<List<Dog>> successResponse = new SuccessResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, dogs);
        return ResponseEntity.ok(successResponse);
    }

    public ResponseEntity<DogApiResponse> callDogApi(String url, int timeout) {
        restTemplate.setRequestFactory(getTimeoutRequestFactory(timeout));
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
        } catch (HttpClientErrorException e) {
            throw new NotFoundException("Failed to retrieve data from Dog API.", e);
        }
    }

}