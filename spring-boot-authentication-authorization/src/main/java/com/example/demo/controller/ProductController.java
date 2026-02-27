package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    // @PermitAll: indica que o método pode ser invocado por qualquer chamador.
    // Observação: para permitir acesso HTTP não autenticado, também é necessário liberar o endpoint em HttpSecurity.
    @GetMapping
    @PermitAll
    public List<Product> list() {
        return productService.findAll();
    }

    // @PreAuthorize: avaliado ANTES da execução do método. Use SpEL para checar roles, authorities ou parâmetros do método.
    // Exemplo: apenas usuários com role ADMIN podem criar produtos.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId())).body(saved);
    }

    // @PreAuthorize com verificação de parâmetro: permite ADMIN ou qualquer chamador quando #id == 1
    // (exemplo de SpEL usando argumentos do método)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == 1")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // @PostAuthorize: avaliado APÓS a execução do método. 'returnObject' refere-se ao valor retornado.
    // Exemplo: pode ser usado para tomar decisões com base no objeto retornado.
    @GetMapping("/post/{id}")
    @PostAuthorize("returnObject != null")
    public ResponseEntity<Product> postChecked(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // @Secured: anotação clássica do Spring. Verifica roles (use o prefixo ROLE_).
    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productService.update(id, product);
        return ResponseEntity.ok(updated);
    }

    // @RolesAllowed: anotação JSR-250; similar ao @Secured mas usa a semântica JSR-250.
    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // @DenyAll: demonstra um método que nunca deve ser chamado (sempre negado).
    @GetMapping("/forbidden")
    @DenyAll
    public void forbidden() {
        // This endpoint will always be denied by method security.
    }
}
