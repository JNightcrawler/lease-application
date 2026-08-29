package com.leaseapp.service;

import com.leaseapp.dto.OrderDetailDto;
import com.leaseapp.entity.Material;
import com.leaseapp.entity.Order;
import com.leaseapp.entity.OrderDetail;
import com.leaseapp.exception.ApiException;
import com.leaseapp.repository.MaterialRepository;
import com.leaseapp.repository.OrderDetailRepository;
import com.leaseapp.repository.OrderRepository;
import com.leaseapp.util.CostCalculator;
import com.leaseapp.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final MaterialRepository materialRepository;

    public OrderDetailService(OrderDetailRepository orderDetailRepository,
                               OrderRepository orderRepository,
                               MaterialRepository materialRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.materialRepository = materialRepository;
    }

    // Lend a material against an order: validates & reserves stock, computes a provisional cost.
    public OrderDetailDto.Response create(OrderDetailDto.CreateRequest req) {
        Order order = orderRepository.findWithLockByOrderNumber(req.orderNumber)
                .orElseThrow(() -> ApiException.notFound("Order not found: " + req.orderNumber));

        if (order.isClosed()) {
            throw ApiException.badRequest("Cannot add materials to a closed order");
        }

        Material material = materialRepository.findWithLockById(req.materialId)
                .orElseThrow(() -> ApiException.notFound("Material not found: " + req.materialId));

        if (material.getNoOfStocksAvailable() < req.noOfMaterialRequired) {
            throw ApiException.badRequest(String.format(
                    "Insufficient stock for \"%s\". Available: %d, Requested: %d",
                    material.getMaterialName(), material.getNoOfStocksAvailable(), req.noOfMaterialRequired));
        }

        // Reserve stock
        material.setNoOfStocksAvailable(material.getNoOfStocksAvailable() - req.noOfMaterialRequired);

        OffsetDateTime lentAt = req.lentTimestamp != null ? req.lentTimestamp : OffsetDateTime.now();

        // Provisional cost estimated against the order's approximateDateToReturn (if set),
        // otherwise a 1-day minimum applies. Finalized when the material is actually returned.
        OffsetDateTime provisionalEnd = order.getApproximateDateToReturn() != null
                ? order.getApproximateDateToReturn() : lentAt;
        BigDecimal provisionalCost = CostCalculator.calculate(
                material.getCostPerDay(), req.noOfMaterialRequired, lentAt, provisionalEnd);

        OrderDetail detail = new OrderDetail();
        detail.setOrderNumber(req.orderNumber);
        detail.setMaterialId(req.materialId);
        detail.setMaterialName(material.getMaterialName());
        detail.setNoOfMaterialRequired(req.noOfMaterialRequired);
        detail.setLentTimestamp(lentAt);
        detail.setCost(provisionalCost);

        return Mapper.toResponse(orderDetailRepository.save(detail));
    }

    @Transactional(readOnly = true)
    public List<OrderDetailDto.Response> findAll(UUID orderNumber) {
        List<OrderDetail> details = orderNumber != null
                ? orderDetailRepository.findByOrderNumber(orderNumber)
                : orderDetailRepository.findAll();
        return details.stream().map(Mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailDto.Response findById(UUID id) {
        return Mapper.toResponse(getOrThrow(id));
    }

    // Adjust quantity before the item is returned; re-reserves/releases stock as needed.
    public OrderDetailDto.Response update(UUID id, OrderDetailDto.UpdateRequest req) {
        OrderDetail detail = orderDetailRepository.findWithLockById(id)
                .orElseThrow(() -> ApiException.notFound("Order detail not found: " + id));

        if (detail.getReturnTimestamp() != null) {
            throw ApiException.badRequest("Cannot edit an already-returned line item");
        }

        if (req.noOfMaterialRequired != null && !req.noOfMaterialRequired.equals(detail.getNoOfMaterialRequired())) {
            Material material = materialRepository.findWithLockById(detail.getMaterialId())
                    .orElseThrow(() -> ApiException.notFound("Material not found: " + detail.getMaterialId()));

            int delta = req.noOfMaterialRequired - detail.getNoOfMaterialRequired(); // positive = need more stock
            if (delta > 0 && material.getNoOfStocksAvailable() < delta) {
                throw ApiException.badRequest(
                        "Insufficient stock for \"" + material.getMaterialName() + "\" to increase quantity");
            }

            material.setNoOfStocksAvailable(material.getNoOfStocksAvailable() - delta);
            detail.setNoOfMaterialRequired(req.noOfMaterialRequired);
        }

        return Mapper.toResponse(detail);
    }

    // Marks the item returned, finalizes cost based on actual duration, and restocks the material.
    public OrderDetailDto.Response returnMaterial(UUID id, OrderDetailDto.ReturnRequest req) {
        OrderDetail detail = orderDetailRepository.findWithLockById(id)
                .orElseThrow(() -> ApiException.notFound("Order detail not found: " + id));

        if (detail.getReturnTimestamp() != null) {
            throw ApiException.badRequest("Material already returned");
        }

        Material material = materialRepository.findWithLockById(detail.getMaterialId())
                .orElseThrow(() -> ApiException.notFound("Material not found: " + detail.getMaterialId()));

        OffsetDateTime returnedAt = req.returnTimestamp != null ? req.returnTimestamp : OffsetDateTime.now();
        if (returnedAt.isBefore(detail.getLentTimestamp())) {
            throw ApiException.badRequest("returnTimestamp cannot be before lentTimestamp");
        }

        BigDecimal finalCost = CostCalculator.calculate(
                material.getCostPerDay(), detail.getNoOfMaterialRequired(), detail.getLentTimestamp(), returnedAt);

        detail.setReturnTimestamp(returnedAt);
        detail.setCost(finalCost);

        // Restock
        material.setNoOfStocksAvailable(material.getNoOfStocksAvailable() + detail.getNoOfMaterialRequired());

        return Mapper.toResponse(detail);
    }

    // Removes a line item. If the material was never returned, its reserved stock is released first.
    public void delete(UUID id) {
        OrderDetail detail = orderDetailRepository.findWithLockById(id)
                .orElseThrow(() -> ApiException.notFound("Order detail not found: " + id));

        if (detail.getReturnTimestamp() == null) {
            materialRepository.findWithLockById(detail.getMaterialId()).ifPresent(material ->
                    material.setNoOfStocksAvailable(material.getNoOfStocksAvailable() + detail.getNoOfMaterialRequired()));
        }

        orderDetailRepository.delete(detail);
    }

    private OrderDetail getOrThrow(UUID id) {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Order detail not found: " + id));
    }
}
