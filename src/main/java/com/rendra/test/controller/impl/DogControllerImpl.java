package com.rendra.test.controller.impl;

import com.rendra.test.controller.CrudController;
import com.rendra.test.entity.Dog;
import com.rendra.test.dto.SuccessResponse;
import com.rendra.test.service.impl.DogServiceImpl;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dogs")
@OpenAPIDefinition(info = @Info(title = "My API", version = "1.0"))
public class DogControllerImpl implements CrudController<Dog, Long> {

    @Autowired
    private DogServiceImpl crudService;

    @GetMapping("/breeds")
    @Operation(summary = "Get all breeds")
    public ResponseEntity<Map<String, List<String>>> getAllBreeds() {
        Map<String, List<String>> breeds = crudService.getAllBreeds();
        return ResponseEntity.ok(breeds);
    }

    @Override
    public ResponseEntity<SuccessResponse<Dog>> create(@Valid @RequestBody Dog dog) {
        return crudService.create(dog);
    }

    @Override
    public ResponseEntity<SuccessResponse<Dog>> update(@PathVariable Long id, @Valid @RequestBody Dog dog) {
        return crudService.update(id, dog);
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        crudService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SuccessResponse<List<Dog>>> getAll() {
        return crudService.getAll();
    }

}
