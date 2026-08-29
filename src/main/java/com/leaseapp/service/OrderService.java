package com.leaseapp.service;

import com.leaseapp.dto.OrderDto;
import com.leaseapp.entity.Order;
import com.leaseapp.entity.OrderDetail;
import com.leaseapp.exception.ApiException;
import com.leaseapp.repository.OrderDetailRepository;
import com.leaseapp.repository.OrderRepository;
import com.leaseapp.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public OrderDto.Response create(OrderDto.CreateRequest req) {
        Order order = new Order();
        order.setMobileNumber(req.mobileNumber);
        order.setApproximateDateToReturn(req.approximateDateToReturn);

        Order saved = orderRepository.save(order);
        return Mapper.toResponse(saved, List.of());
    }

    @Transactional(readOnly = true)
    public List<OrderDto.Response> findAll(String mobileNumber, Boolean isClosed) {
        List<Order> orders;
        if (mobileNumber != null) {
            orders = orderRepository.findByMobileNumber(mobileNumber);
        } else if (isClosed != null) {
            orders = orderRepository.findByIsClosed(isClosed);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(o -> Mapper.toResponse(o, orderDetailRepository.findByOrderNumber(o.getOrderNumber())))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDto.Response findById(UUID orderNumber) {
        Order order = getOrThrow(orderNumber);
        List<OrderDetail> details = orderDetailRepository.findByOrderNumber(orderNumber);
        return Mapper.toResponse(order, details);
    }

    public OrderDto.Response update(UUID orderNumber, OrderDto.UpdateRequest req) {
        Order order = getOrThrow(orderNumber);
        if (order.isClosed()) {
            throw ApiException.badRequest("Cannot edit a closed order");
        }

        if (req.mobileNumber != null) order.setMobileNumber(req.mobileNumber);
        if (req.approximateDateToReturn != null) order.setApproximateDateToReturn(req.approximateDateToReturn);

        List<OrderDetail> details = orderDetailRepository.findByOrderNumber(orderNumber);
        return Mapper.toResponse(order, details);
    }

    // All materials in the order must be returned before it can be closed.
    public OrderDto.Response close(UUID orderNumber) {
        Order order = orderRepository.findWithLockByOrderNumber(orderNumber)
                .orElseThrow(() -> ApiException.notFound("Order not found: " + orderNumber));

        if (order.isClosed()) {
            throw ApiException.badRequest("Order is already closed");
        }

        List<OrderDetail> pending = orderDetailRepository.findByOrderNumberAndReturnTimestampIsNull(orderNumber);
        if (!pending.isEmpty()) {
            throw ApiException.badRequest(
                    pending.size() + " material(s) not yet returned. Return them before closing the order.");
        }

        order.setClosed(true);
        order.setClosingTimestamp(OffsetDateTime.now());

        List<OrderDetail> details = orderDetailRepository.findByOrderNumber(orderNumber);
        return Mapper.toResponse(order, details);
    }

    public void delete(UUID orderNumber) {
        Order order = getOrThrow(orderNumber);
        List<OrderDetail> active = orderDetailRepository.findByOrderNumberAndReturnTimestampIsNull(orderNumber);
        if (!active.isEmpty()) {
            throw ApiException.badRequest("Cannot delete order with materials still lent out. Return them first.");
        }
        orderRepository.delete(order);
    }

    private Order getOrThrow(UUID orderNumber) {
        return orderRepository.findById(orderNumber)
                .orElseThrow(() -> ApiException.notFound("Order not found: " + orderNumber));
    }
}
