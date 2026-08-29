package com.leaseapp.controller;

import com.leaseapp.dto.ApiResponse;
import com.leaseapp.dto.OrderDetailDto;
import com.leaseapp.service.OrderDetailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderDetailDto.Response> create(@Valid @RequestBody OrderDetailDto.CreateRequest req) {
        return ApiResponse.ok(orderDetailService.create(req));
    }

    @GetMapping
    public ApiResponse<List<OrderDetailDto.Response>> getAll(@RequestParam(required = false) UUID orderNumber) {
        List<OrderDetailDto.Response> details = orderDetailService.findAll(orderNumber);
        return ApiResponse.ok(details, details.size());
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailDto.Response> getById(@PathVariable UUID id) {
        return ApiResponse.ok(orderDetailService.findById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<OrderDetailDto.Response> update(@PathVariable UUID id, @Valid @RequestBody OrderDetailDto.UpdateRequest req) {
        return ApiResponse.ok(orderDetailService.update(id, req));
    }

    @PatchMapping("/{id}/return")
    public ApiResponse<OrderDetailDto.Response> returnMaterial(@PathVariable UUID id, @RequestBody(required = false) OrderDetailDto.ReturnRequest req) {
        OrderDetailDto.ReturnRequest body = req != null ? req : new OrderDetailDto.ReturnRequest();
        return ApiResponse.ok(orderDetailService.returnMaterial(id, body));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        orderDetailService.delete(id);
        return ApiResponse.message("Order detail deleted successfully");
    }
}
