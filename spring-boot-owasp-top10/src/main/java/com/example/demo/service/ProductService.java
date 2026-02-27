package com.example.demo.service;

// MELHORIA - Service layer separada para lógica de negócio
// MELHORIA - Utiliza DTOs e mappers para segurança e validação

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequestDTO productRequest) {
        return productRepository.findById(id)
                .map(p -> {
                    productMapper.updateEntity(p, productRequest);
                    return productRepository.save(p);
                })
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
