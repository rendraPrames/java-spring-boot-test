package com.rendra.test.service;


import com.rendra.test.dto.SuccessResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CrudService<T, U> {

    ResponseEntity<SuccessResponse<T>> create(T entity);

    ResponseEntity<SuccessResponse<T>> update(U id, T entity);

    void delete(U id);

    ResponseEntity<SuccessResponse<List<T>>> getAll();
}