package com.leaseapp.util;

import com.leaseapp.dto.MaterialDto;
import com.leaseapp.dto.OrderDetailDto;
import com.leaseapp.dto.OrderDto;
import com.leaseapp.entity.Material;
import com.leaseapp.entity.Order;
import com.leaseapp.entity.OrderDetail;

import java.util.List;

public final class Mapper {

    private Mapper() {
    }

    public static MaterialDto.Response toResponse(Material m) {
        MaterialDto.Response r = new MaterialDto.Response();
        r.id = m.getId();
        r.materialName = m.getMaterialName();
        r.noOfStocksAvailable = m.getNoOfStocksAvailable();
        r.totalStocks = m.getTotalStocks();
        r.costPerDay = m.getCostPerDay();
        r.createdAt = m.getCreatedAt();
        r.updatedAt = m.getUpdatedAt();
        return r;
    }

    public static OrderDetailDto.Response toResponse(OrderDetail od) {
        OrderDetailDto.Response r = new OrderDetailDto.Response();
        r.id = od.getId();
        r.orderNumber = od.getOrderNumber();
        r.materialId = od.getMaterialId();
        r.materialName = od.getMaterialName();
        r.noOfMaterialRequired = od.getNoOfMaterialRequired();
        r.lentTimestamp = od.getLentTimestamp();
        r.returnTimestamp = od.getReturnTimestamp();
        r.cost = od.getCost();
        return r;
    }

    public static OrderDto.Response toResponse(Order o, List<OrderDetail> details) {
        OrderDto.Response r = new OrderDto.Response();
        r.orderNumber = o.getOrderNumber();
        r.mobileNumber = o.getMobileNumber();
        r.createTimestamp = o.getCreateTimestamp();
        r.closingTimestamp = o.getClosingTimestamp();
        r.isClosed = o.isClosed();
        r.approximateDateToReturn = o.getApproximateDateToReturn();
        if (details != null) {
            r.orderDetails = details.stream().map(Mapper::toResponse).toList();
        }
        return r;
    }
}
