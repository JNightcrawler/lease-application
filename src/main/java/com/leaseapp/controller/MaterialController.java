package com.leaseapp.controller;

import com.leaseapp.dto.ApiResponse;
import com.leaseapp.dto.MaterialDto;
import com.leaseapp.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MaterialDto.Response> create(@Valid @RequestBody MaterialDto.CreateRequest req) {
        return ApiResponse.ok(materialService.create(req));
    }

    @GetMapping
    public ApiResponse<List<MaterialDto.Response>> getAll() {
        List<MaterialDto.Response> materials = materialService.findAll();
        return ApiResponse.ok(materials, materials.size());
    }

    @GetMapping("/{id}")
    public ApiResponse<MaterialDto.Response> getById(@PathVariable UUID id) {
        return ApiResponse.ok(materialService.findById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<MaterialDto.Response> update(@PathVariable UUID id, @Valid @RequestBody MaterialDto.UpdateRequest req) {
        return ApiResponse.ok(materialService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        materialService.delete(id);
        return ApiResponse.message("Material deleted successfully");
    }
}
