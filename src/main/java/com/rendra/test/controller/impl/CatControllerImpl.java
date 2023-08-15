package com.rendra.test.controller.impl;

import com.rendra.test.controller.CrudController;
import com.rendra.test.entity.Cat;
import com.rendra.test.dto.SuccessResponse;
import com.rendra.test.service.CrudService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cats")
@OpenAPIDefinition(info = @Info(title = "My API", version = "1.0"))
public class CatControllerImpl implements CrudController<Cat, Long> {

    @Autowired
    private CrudService<Cat, Long> crudService;

    @Override
    public ResponseEntity<SuccessResponse<Cat>> create(Cat entity) {
        return crudService.create(entity);
    }

    @Override
    public ResponseEntity<SuccessResponse<Cat>> update(Long id, Cat entity) {
        return crudService.update(id, entity);
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        crudService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SuccessResponse<List<Cat>>> getAll() {
        return crudService.getAll();
    }
}
