package com.leaseapp.service;

import com.leaseapp.dto.MaterialDto;
import com.leaseapp.entity.Material;
import com.leaseapp.exception.ApiException;
import com.leaseapp.repository.MaterialRepository;
import com.leaseapp.util.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public MaterialDto.Response create(MaterialDto.CreateRequest req) {
        if (materialRepository.existsByMaterialNameIgnoreCase(req.materialName)) {
            throw ApiException.conflict("A material named \"" + req.materialName + "\" already exists");
        }

        int available = req.noOfStocksAvailable != null ? req.noOfStocksAvailable : req.totalStocks;
        if (available > req.totalStocks) {
            throw ApiException.badRequest("noOfStocksAvailable cannot exceed totalStocks");
        }

        Material material = new Material();
        material.setMaterialName(req.materialName);
        material.setTotalStocks(req.totalStocks);
        material.setNoOfStocksAvailable(available);
        material.setCostPerDay(req.costPerDay);

        return Mapper.toResponse(materialRepository.save(material));
    }

    @Transactional(readOnly = true)
    public List<MaterialDto.Response> findAll() {
        return materialRepository.findAll().stream().map(Mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MaterialDto.Response findById(UUID id) {
        return Mapper.toResponse(getOrThrow(id));
    }

    public MaterialDto.Response update(UUID id, MaterialDto.UpdateRequest req) {
        Material material = getOrThrow(id);

        if (req.materialName != null) material.setMaterialName(req.materialName);
        if (req.totalStocks != null) material.setTotalStocks(req.totalStocks);
        if (req.noOfStocksAvailable != null) material.setNoOfStocksAvailable(req.noOfStocksAvailable);
        if (req.costPerDay != null) material.setCostPerDay(req.costPerDay);

        if (material.getNoOfStocksAvailable() > material.getTotalStocks()) {
            throw ApiException.badRequest("noOfStocksAvailable cannot exceed totalStocks");
        }

        return Mapper.toResponse(material);
    }

    public void delete(UUID id) {
        Material material = getOrThrow(id);
        materialRepository.delete(material);
    }

    private Material getOrThrow(UUID id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Material not found: " + id));
    }
}
