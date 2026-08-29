package com.leaseapp.controller;

import com.leaseapp.dto.ApiResponse;
import com.leaseapp.dto.OrderDto;
import com.leaseapp.service.OrderService;
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
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "APIs for managing customer rental orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Order", description = "Creates a new rental order for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<OrderDto.Response> create(@Valid @RequestBody OrderDto.CreateRequest req) {
        return ApiResponse.ok(orderService.create(req));
    }

    @GetMapping
    @Operation(summary = "Get All Orders", description = "Retrieves all orders with optional filtering by mobile number and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<List<OrderDto.Response>> getAll(
            @Parameter(description = "Filter by customer mobile number", required = false)
            @RequestParam(required = false) String mobileNumber,
            @Parameter(description = "Filter by order status (true=closed, false=open)", required = false)
            @RequestParam(required = false) Boolean isClosed) {
        List<OrderDto.Response> orders = orderService.findAll(mobileNumber, isClosed);
        return ApiResponse.ok(orders, orders.size());
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get Order by Number", description = "Retrieves a specific order and all its line items by order number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found and returned"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<OrderDto.Response> getById(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable UUID orderNumber) {
        return ApiResponse.ok(orderService.findById(orderNumber));
    }

    @PutMapping("/{orderNumber}")
    @Operation(summary = "Update Order", description = "Updates an existing order's details (mobile number, approximate return date)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<OrderDto.Response> update(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable UUID orderNumber,
            @RequestBody OrderDto.UpdateRequest req) {
        return ApiResponse.ok(orderService.update(orderNumber, req));
    }

    @PatchMapping("/{orderNumber}/close")
    @Operation(summary = "Close Order", description = "Marks an order as closed/completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order closed successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<OrderDto.Response> close(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable UUID orderNumber) {
        return ApiResponse.ok(orderService.close(orderNumber));
    }

    @DeleteMapping("/{orderNumber}")
    @Operation(summary = "Delete Order", description = "Deletes an order and all its associated line items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ApiResponse<Void> delete(
            @Parameter(description = "Order UUID", required = true)
            @PathVariable UUID orderNumber) {
        orderService.delete(orderNumber);
        return ApiResponse.message("Order deleted successfully");
    }
}
