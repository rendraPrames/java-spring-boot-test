package com.rendra.test.repository;

import com.rendra.test.entity.Cat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatRepository extends CrudRepository<Cat, Long> {
    boolean existsByBreed(String breed);
}
