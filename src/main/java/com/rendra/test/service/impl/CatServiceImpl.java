package com.rendra.test.service.impl;

import com.rendra.test.entity.Cat;
import com.rendra.test.dto.SuccessResponse;
import com.rendra.test.exception.NotFoundException;
import com.rendra.test.repository.CatRepository;
import com.rendra.test.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatServiceImpl implements CrudService<Cat, Long> {

    @Autowired
    private CatRepository catRepository;

    private static final String SUCCESS_MESSAGE = "Success";


    @Override
    public ResponseEntity<SuccessResponse<Cat>> create(Cat cat) {
        if (catRepository.existsByBreed(cat.getBreed())) {
            throw new NotFoundException("Cat with breed already exists");
        }

        Cat newCat = catRepository.save(cat);
        SuccessResponse<Cat> successResponse = new SuccessResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, newCat);
        return ResponseEntity.ok(successResponse);
    }

    @Override
    public ResponseEntity<SuccessResponse<Cat>> update(Long id, Cat cat) {
        Cat existingCat = catRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cat not found with id: " + id));

        existingCat.setBreed(cat.getBreed());
        existingCat.setSubBreed(cat.getSubBreed());
        Cat updatedCat = catRepository.save(existingCat);
        SuccessResponse<Cat> successResponse = new SuccessResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, updatedCat);
        return ResponseEntity.ok(successResponse);
    }

    @Override
    public void delete(Long id) {
        if (!catRepository.existsById(id)) {
            throw new NotFoundException("Cat not found with id: " + id);
        }
        catRepository.deleteById(id);
    }

    @Override
    public ResponseEntity<SuccessResponse<List<Cat>>> getAll() {
        List<Cat> cats = new ArrayList<>();
        catRepository.findAll().forEach(cats::add);

        if (cats.isEmpty()) {
            throw new NotFoundException("No cats found");
        }

        SuccessResponse<List<Cat>> successResponse = new SuccessResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, cats);
        return ResponseEntity.ok(successResponse);
    }
}
