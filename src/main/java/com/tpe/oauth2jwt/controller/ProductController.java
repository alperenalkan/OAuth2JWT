package com.tpe.oauth2jwt.controller;

import com.tpe.oauth2jwt.dto.ProductRequest;
import com.tpe.oauth2jwt.dto.ProductResponse;
import com.tpe.oauth2jwt.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest,
            Authentication authentication) {
        // Debug: Log authentication details
        System.out.println("=== Product Creation Debug ===");
        System.out.println("Username: " + authentication.getName());
        System.out.println("Authorities: " + authentication.getAuthorities());
        System.out.println("Is Authenticated: " + authentication.isAuthenticated());
        System.out.println("==============================");
        
        String username = authentication.getName();
        ProductResponse response = productService.createProduct(productRequest, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            ProductResponse response = productService.updateProduct(id, productRequest, username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            productService.deleteProduct(id, username);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/my-products")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Authentication authentication) {
        String username = authentication.getName();
        List<ProductResponse> products = productService.getProductsByUser(username);
        return ResponseEntity.ok(products);
    }
}

