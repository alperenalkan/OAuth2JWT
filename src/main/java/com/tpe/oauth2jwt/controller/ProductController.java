package com.tpe.oauth2jwt.controller;

import com.tpe.oauth2jwt.dto.ProductRequest;
import com.tpe.oauth2jwt.dto.ProductResponse;
import com.tpe.oauth2jwt.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Products", description = "Ürün CRUD işlemleri")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(
            summary = "Yeni ürün oluştur",
            description = "Sadece ADMIN rolüne sahip kullanıcılar yeni ürün oluşturabilir",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ürün başarıyla oluşturuldu",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "403", description = "Yetki yok (Sadece ADMIN)"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli")
    })
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

    @Operation(
            summary = "Tüm ürünleri listele",
            description = "Tüm ürünleri listeler (Kimlik doğrulama gerekli)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürünler başarıyla listelendi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Ürün detayı",
            description = "ID'ye göre ürün detayını getirir",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürün bulundu",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ürün bulunamadı"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Ürün güncelle",
            description = "Ürün bilgilerini günceller. Admin herhangi bir ürünü, User sadece kendi ürünlerini güncelleyebilir",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürün başarıyla güncellendi",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "403", description = "Yetki yok (Sadece kendi ürününüzü güncelleyebilirsiniz)"),
            @ApiResponse(responseCode = "404", description = "Ürün bulunamadı"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli")
    })
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

    @Operation(
            summary = "Ürün sil",
            description = "Sadece ADMIN rolüne sahip kullanıcılar ürün silebilir",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ürün başarıyla silindi"),
            @ApiResponse(responseCode = "403", description = "Yetki yok (Sadece ADMIN)"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli")
    })
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

    @Operation(
            summary = "Kendi ürünlerimi listele",
            description = "Giriş yapmış kullanıcının kendi oluşturduğu ürünleri listeler",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürünler başarıyla listelendi"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli")
    })
    @GetMapping("/my-products")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Authentication authentication) {
        String username = authentication.getName();
        List<ProductResponse> products = productService.getProductsByUser(username);
        return ResponseEntity.ok(products);
    }
}

