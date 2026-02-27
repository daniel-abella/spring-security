package com.example.demo.controller;

// MELHORIA - Uso de DTOs com validação para segurança contra injeção e mass assignment
// MELHORIA - Logging estruturado para auditoria de operações CRUD
// MELHORIA - Retorno de DTOs ao invés de entidades evita exposição de estrutura interna

import com.example.demo.dto.ProductRequestDTO;
import com.example.demo.dto.ProductResponseDTO;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final ProductMapper productMapper;

    // @PermitAll: indica que o método pode ser invocado por qualquer chamador.
    // Observação: para permitir acesso HTTP não autenticado, também é necessário liberar o endpoint em HttpSecurity.
    @GetMapping
    @PermitAll
    public List<ProductResponseDTO> list() {
        logger.debug("Public access to product list");
        return productService.findAll().stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // @PreAuthorize: avaliado ANTES da execução do método. Use SpEL para checar roles, authorities ou parâmetros do método.
    // Exemplo: apenas usuários com role ADMIN podem criar produtos.
    // MELHORIA - @Valid ativa validação de preço positivo, nome válido, etc.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO productRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Admin '{}' creating new product '{}'", auth.getName(), productRequest.getName());
        
        Product product = productMapper.toEntity(productRequest);
        Product saved = productService.save(product);
        
        logger.info("Product '{}' (ID: {}) created successfully", saved.getName(), saved.getId());
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
                .body(productMapper.toResponseDTO(saved));
    }

    // @PreAuthorize com verificação de parâmetro: permite ADMIN ou qualquer chamador quando #id == 1
    // (exemplo de SpEL usando argumentos do método)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == 1")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        logger.debug("Request to get product with ID: {}", id);
        return productService.findById(id)
                .map(productMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Product with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // @PostAuthorize: avaliado APÓS a execução do método. 'returnObject' refere-se ao valor retornado.
    // Exemplo: pode ser usado para tomar decisões com base no objeto retornado.
    @GetMapping("/post/{id}")
    @PostAuthorize("returnObject != null")
    public ResponseEntity<ProductResponseDTO> postChecked(@PathVariable Long id) {
        return productService.findById(id)
                .map(productMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // @Secured: anotação clássica do Spring. Verifica roles (use o prefixo ROLE_).
    // MELHORIA - Logging de operações de atualização para auditoria
    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Admin '{}' updating product with ID: {}", auth.getName(), id);
        
        Product updated = productService.update(id, productRequest);
        
        logger.info("Product '{}' (ID: {}) updated successfully", updated.getName(), updated.getId());
        return ResponseEntity.ok(productMapper.toResponseDTO(updated));
    }

    // @RolesAllowed: anotação JSR-250; similar ao @Secured mas usa a semântica JSR-250.
    // MELHORIA - Logging de exclusões para rastreamento e auditoria
    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.warn("Admin '{}' deleting product with ID: {}", auth.getName(), id);
        
        productService.delete(id);
        
        logger.info("Product with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    // @DenyAll: demonstra um método que nunca deve ser chamado (sempre negado).
    @GetMapping("/forbidden")
    @DenyAll
    public void forbidden() {
        // This endpoint will always be denied by method security.
    }
}
