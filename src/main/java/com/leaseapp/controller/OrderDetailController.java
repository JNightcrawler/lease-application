package com.leaseapp.controller;

import com.leaseapp.dto.ApiResponse;
import com.leaseapp.dto.OrderDetailDto;
import com.leaseapp.service.OrderDetailService;
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
@RequestMapping("/api/order-details")
@Tag(name = "Order Details", description = "APIs for managing order line items (materials in an order)")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add Material to Order", description = "Adds a material line item to an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order detail created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient stock"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<OrderDetailDto.Response> create(@Valid @RequestBody OrderDetailDto.CreateRequest req) {
        return ApiResponse.ok(orderDetailService.create(req));
    }

    @GetMapping
    @Operation(summary = "Get Order Details", description = "Retrieves line items from an order, optionally filtered by order number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<List<OrderDetailDto.Response>> getAll(
            @Parameter(description = "Filter by Order UUID", required = false)
            @RequestParam(required = false) UUID orderNumber) {
        List<OrderDetailDto.Response> details = orderDetailService.findAll(orderNumber);
        return ApiResponse.ok(details, details.size());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Order Detail by ID", description = "Retrieves a specific order line item by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order detail found and returned"),
            @ApiResponse(responseCode = "404", description = "Order detail not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<OrderDetailDto.Response> getById(
            @Parameter(description = "Order Detail UUID", required = true)
            @PathVariable UUID id) {
        return ApiResponse.ok(orderDetailService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Order Detail", description = "Updates a line item (quantity, due date) in an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order detail updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Order detail not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<OrderDetailDto.Response> update(
            @Parameter(description = "Order Detail UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody OrderDetailDto.UpdateRequest req) {
        return ApiResponse.ok(orderDetailService.update(id, req));
    }

    @PatchMapping("/{id}/return")
    @Operation(summary = "Return Material", description = "Marks a material as returned from the order and updates stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material returned successfully"),
            @ApiResponse(responseCode = "404", description = "Order detail not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<OrderDetailDto.Response> returnMaterial(
            @Parameter(description = "Order Detail UUID", required = true)
            @PathVariable UUID id,
            @RequestBody(required = false) OrderDetailDto.ReturnRequest req) {
        OrderDetailDto.ReturnRequest body = req != null ? req : new OrderDetailDto.ReturnRequest();
        return ApiResponse.ok(orderDetailService.returnMaterial(id, body));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Order Detail", description = "Removes a line item from an order and returns the material to stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order detail deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order detail not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<Void> delete(
            @Parameter(description = "Order Detail UUID", required = true)
            @PathVariable UUID id) {
        orderDetailService.delete(id);
        return ApiResponse.message("Order detail deleted successfully");
    }
}
