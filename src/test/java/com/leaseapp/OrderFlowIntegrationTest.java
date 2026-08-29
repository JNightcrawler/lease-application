package com.leaseapp;

import com.leaseapp.dto.MaterialDto;
import com.leaseapp.dto.OrderDetailDto;
import com.leaseapp.dto.OrderDto;
import com.leaseapp.exception.ApiException;
import com.leaseapp.service.MaterialService;
import com.leaseapp.service.OrderDetailService;
import com.leaseapp.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderFlowIntegrationTest {

    @Autowired
    private MaterialService materialService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Test
    void lendingReservesStockAndReturningRestocks() {
        MaterialDto.CreateRequest materialReq = new MaterialDto.CreateRequest();
        materialReq.materialName = "Test Ladder";
        materialReq.totalStocks = 5;
        materialReq.costPerDay = new BigDecimal("100.00");
        MaterialDto.Response material = materialService.create(materialReq);
        assertEquals(5, material.noOfStocksAvailable);

        OrderDto.CreateRequest orderReq = new OrderDto.CreateRequest();
        orderReq.mobileNumber = "9876543210";
        OrderDto.Response order = orderService.create(orderReq);

        OrderDetailDto.CreateRequest detailReq = new OrderDetailDto.CreateRequest();
        detailReq.orderNumber = order.orderNumber;
        detailReq.materialId = material.id;
        detailReq.noOfMaterialRequired = 3;
        OrderDetailDto.Response detail = orderDetailService.create(detailReq);

        // Stock should be reserved
        assertEquals(2, materialService.findById(material.id).noOfStocksAvailable);

        // Closing before return should fail
        assertThrows(ApiException.class, () -> orderService.close(order.orderNumber));

        // Return the material
        OrderDetailDto.ReturnRequest returnReq = new OrderDetailDto.ReturnRequest();
        orderDetailService.returnMaterial(detail.id, returnReq);

        // Stock restored
        assertEquals(5, materialService.findById(material.id).noOfStocksAvailable);

        // Now closing should succeed
        OrderDto.Response closed = orderService.close(order.orderNumber);
        assertTrue(closed.isClosed);
    }

    @Test
    void lendingMoreThanAvailableStockIsRejected() {
        MaterialDto.CreateRequest materialReq = new MaterialDto.CreateRequest();
        materialReq.materialName = "Scarce Drill";
        materialReq.totalStocks = 1;
        materialReq.costPerDay = new BigDecimal("50.00");
        MaterialDto.Response material = materialService.create(materialReq);

        OrderDto.CreateRequest orderReq = new OrderDto.CreateRequest();
        orderReq.mobileNumber = "9123456789";
        OrderDto.Response order = orderService.create(orderReq);

        OrderDetailDto.CreateRequest detailReq = new OrderDetailDto.CreateRequest();
        detailReq.orderNumber = order.orderNumber;
        detailReq.materialId = material.id;
        detailReq.noOfMaterialRequired = 2; // more than available

        assertThrows(ApiException.class, () -> orderDetailService.create(detailReq));
    }
}
