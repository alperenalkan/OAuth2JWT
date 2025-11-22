package com.tpe.oauth2jwt.controller;

import com.tpe.oauth2jwt.dto.*;
import com.tpe.oauth2jwt.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Kullanıcı kaydı, girişi ve kimlik doğrulama işlemleri")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Kullanıcı kaydı",
            description = "Yeni kullanıcı kaydı oluşturur ve JWT token döner"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kullanıcı başarıyla kaydedildi",
                    content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek veya kullanıcı adı/email zaten kullanılıyor")
    })
    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            JwtAuthResponse response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Kullanıcı girişi",
            description = "Kullanıcı adı ve şifre ile giriş yapar ve JWT token döner"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Giriş başarılı",
                    content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Geçersiz kullanıcı adı veya şifre")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtAuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(
            summary = "Mevcut kullanıcı bilgileri",
            description = "Giriş yapmış kullanıcının bilgilerini döner",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı bilgileri başarıyla alındı"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli")
    })
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Authentication not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", authentication.getName());
            userInfo.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            userInfo.put("authenticated", authentication.isAuthenticated());
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error getting user info");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(
            summary = "Kullanıcı güncelleme",
            description = "Varolan kullanıcı güncellenir ve mesaj döner"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kullanıcı başarıyla güncellendi",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek böyle bir kullanici bulunamadi")
    })
    @PutMapping("update/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody RegisterRequest updateRequest, Authentication authentication) {
        try {
            Map<String, Object> response = authService.updateUserById(id, updateRequest, authentication);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @Operation(
            summary = "Kullanıcı rolü güncelleme",
            description = "Varolan kullanıcının rol(leri) güncellenir ve mesaj döner"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcı rolü başarıyla güncellendi",
                    content = @Content(schema = @Schema(implementation = UpdateRoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
            @ApiResponse(responseCode = "403", description = "Bu işlemi yapmaya yetkiniz yok"),
            @ApiResponse(responseCode = "404", description = "Böyle bir kullanıcı bulunamadı")
    })
    @PutMapping("role/{id}")
    public ResponseEntity<UpdateRoleResponse> updateRole(@PathVariable Long id,
                                                         @RequestBody @Valid UpdateRoleRequest request,
                                                         Authentication authentication) {
        UpdateRoleResponse response = authService.updateRoleById(id, request, authentication);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Kullanıcıları sayfalama ile listele",
            description = "Sadece ADMIN rolüne sahip kullanıcılar tüm kullanıcıları sayfalama ile listeleyebilir. Role parametresi ile filtreleme yapılabilir (ALL, ADMIN, USER)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kullanıcılar başarıyla listelendi",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Yetki yok (Sadece ADMIN)"),
            @ApiResponse(responseCode = "401", description = "Kimlik doğrulama gerekli"),
            @ApiResponse(responseCode = "400", description = "Geçersiz role parametresi")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllUserPage")
    public ResponseEntity<Page<RegisterResponse>> getUserByPage(
            @RequestParam(value = "role", defaultValue = "ALL", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = "firstName", required = false) String sortBy,
            @RequestParam(value = "order", defaultValue = "ASC", required = false) String orderStr
    ) {
                        // 1. Başlangıçta default değer: ASC (artan sıralama)
                Sort.Direction order = Sort.Direction.ASC;

                // 2. Eğer orderStr parametresi gönderilmişse (null değilse ve boş değilse)
                if (orderStr != null && !orderStr.isBlank()) {
                try {
                        // 3. String'i büyük harfe çevirip enum'a dönüştür
                        // Örnek: "asc" -> "ASC" -> Sort.Direction.ASC
                        // Örnek: "desc" -> "DESC" -> Sort.Direction.DESC
                        order = Sort.Direction.valueOf(orderStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                        // 4. Eğer geçersiz bir değer gelirse (örn: "xyz")
                        //    Hata fırlatmak yerine default olarak ASC kullan
                        order = Sort.Direction.ASC;
                }
                }
        
        Page<RegisterResponse> adminsOrUsers =
                authService.getUsersByPage(page, size, sortBy, order, role);

        return new ResponseEntity<>(adminsOrUsers, HttpStatus.OK);
    }




}

