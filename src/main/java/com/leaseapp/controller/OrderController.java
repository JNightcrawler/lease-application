package com.leaseapp.controller;

import com.leaseapp.dto.ApiResponse;
import com.leaseapp.dto.OrderDto;
import com.leaseapp.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderDto.Response> create(@Valid @RequestBody OrderDto.CreateRequest req) {
        return ApiResponse.ok(orderService.create(req));
    }

    @GetMapping
    public ApiResponse<List<OrderDto.Response>> getAll(
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) Boolean isClosed) {
        List<OrderDto.Response> orders = orderService.findAll(mobileNumber, isClosed);
        return ApiResponse.ok(orders, orders.size());
    }

    @GetMapping("/{orderNumber}")
    public ApiResponse<OrderDto.Response> getById(@PathVariable UUID orderNumber) {
        return ApiResponse.ok(orderService.findById(orderNumber));
    }

    @PutMapping("/{orderNumber}")
    public ApiResponse<OrderDto.Response> update(@PathVariable UUID orderNumber, @RequestBody OrderDto.UpdateRequest req) {
        return ApiResponse.ok(orderService.update(orderNumber, req));
    }

    @PatchMapping("/{orderNumber}/close")
    public ApiResponse<OrderDto.Response> close(@PathVariable UUID orderNumber) {
        return ApiResponse.ok(orderService.close(orderNumber));
    }

    @DeleteMapping("/{orderNumber}")
    public ApiResponse<Void> delete(@PathVariable UUID orderNumber) {
        orderService.delete(orderNumber);
        return ApiResponse.message("Order deleted successfully");
    }
}
