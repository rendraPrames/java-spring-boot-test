package com.rendra.test.repository;

import com.rendra.test.entity.Dog;
import org.springframework.data.repository.CrudRepository;

public interface DogRepository extends CrudRepository<Dog, Long> {
    boolean existsByBreed(String breed);
}
