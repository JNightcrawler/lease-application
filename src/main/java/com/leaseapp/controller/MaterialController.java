package com.leaseapp.controller;

import com.leaseapp.dto.MaterialDto;
import com.leaseapp.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/materials")
@Tag(name = "Materials", description = "APIs for managing rental materials/inventory")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Material", description = "Creates a new material/inventory item for rental")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Material created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public com.leaseapp.dto.ApiResponse<MaterialDto.Response> create(@Valid @RequestBody MaterialDto.CreateRequest req) {
        return com.leaseapp.dto.ApiResponse.ok(materialService.create(req));
    }

    @GetMapping
    @Operation(summary = "Get All Materials", description = "Retrieves all materials with their stock and rental cost information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Materials retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public com.leaseapp.dto.ApiResponse<List<MaterialDto.Response>> getAll() {
        List<MaterialDto.Response> materials = materialService.findAll();
        return com.leaseapp.dto.ApiResponse.ok(materials, materials.size());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Material by ID", description = "Retrieves a specific material by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material found and returned"),
            @ApiResponse(responseCode = "404", description = "Material not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public com.leaseapp.dto.ApiResponse<MaterialDto.Response> getById(
            @Parameter(description = "Material UUID", required = true)
            @PathVariable UUID id) {
        return com.leaseapp.dto.ApiResponse.ok(materialService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Material", description = "Updates an existing material's details (name, stock, cost)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Material not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public com.leaseapp.dto.ApiResponse<MaterialDto.Response> update(
            @Parameter(description = "Material UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody MaterialDto.UpdateRequest req) {
        return com.leaseapp.dto.ApiResponse.ok(materialService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Material", description = "Deletes a material from the inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Material not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public com.leaseapp.dto.ApiResponse<Void> delete(
            @Parameter(description = "Material UUID", required = true)
            @PathVariable UUID id) {
        materialService.delete(id);
        return com.leaseapp.dto.ApiResponse.message("Material deleted successfully");
    }
}
