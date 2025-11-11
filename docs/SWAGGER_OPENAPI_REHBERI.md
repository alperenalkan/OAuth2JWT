# Swagger/OpenAPI Rehberi - OAuth2JWT Projesi

## 🎯 Swagger/OpenAPI Nedir?

Swagger/OpenAPI, REST API'lerinizi dokümante etmek ve test etmek için kullanılan bir araçtır.

**Faydaları:**
- ✅ API dokümantasyonu otomatik oluşturulur
- ✅ API'yi test etmek için UI sağlar
- ✅ Frontend geliştiriciler için kolaylık
- ✅ Piyasada standart

---

## 📦 Kurulum

### 1. Dependency Eklendi

**pom.xml:**
```xml
<!-- Swagger/OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

---

### 2. Swagger Configuration

**SwaggerConfig.java:**
```java
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OAuth2JWT REST API")
                        .version("1.0.0")
                        .description("OAuth2 ve JWT kullanarak güvenli REST API işlemleri"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
}
```

---

### 3. Security Configuration Güncellendi

**SecurityConfig.java:**
```java
.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
```

**Neden:**
- Swagger UI'ya herkesin erişebilmesi için
- API dokümantasyonunu görmek için

---

### 4. Application Properties

**application.properties:**
```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
```

---

## 🚀 Kullanım

### Swagger UI'ya Erişim

**URL:**
```
http://localhost:8080/swagger-ui.html
```

**Veya:**
```
http://localhost:8080/swagger-ui/index.html
```

---

### API Dokümantasyonu

**OpenAPI JSON:**
```
http://localhost:8080/v3/api-docs
```

---

## 📋 Swagger UI Özellikleri

### 1. API Endpoint'leri Görüntüleme

- Tüm endpoint'ler listelenir
- Her endpoint için açıklama
- Request/Response örnekleri

---

### 2. API Test Etme

**"Try it out" Butonu:**
1. Endpoint'e tıklayın
2. "Try it out" butonuna tıklayın
3. Parametreleri doldurun
4. "Execute" butonuna tıklayın
5. Sonucu görün

---

### 3. JWT Token ile Test

**Bearer Token Ekleme:**
1. Sağ üstte "Authorize" butonuna tıklayın
2. Token'ı girin: `Bearer <your-token>`
3. "Authorize" butonuna tıklayın
4. Artık tüm endpoint'leri test edebilirsiniz

**Örnek:**
```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📝 Controller Annotation'ları

### @Tag

**Kullanım:**
```java
@Tag(name = "Authentication", description = "Kullanıcı kaydı, girişi ve kimlik doğrulama işlemleri")
public class AuthController {
    // ...
}
```

**Fayda:**
- Endpoint'ler gruplandırılır
- Swagger UI'da kategorize edilir

---

### @Operation

**Kullanım:**
```java
@Operation(
    summary = "Kullanıcı kaydı",
    description = "Yeni kullanıcı kaydı oluşturur ve JWT token döner"
)
@PostMapping("/register")
public ResponseEntity<JwtAuthResponse> register(...) {
    // ...
}
```

**Fayda:**
- Endpoint açıklaması
- Swagger UI'da görünür

---

### @ApiResponses

**Kullanım:**
```java
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Kullanıcı başarıyla kaydedildi",
            content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))),
    @ApiResponse(responseCode = "400", description = "Geçersiz istek")
})
```

**Fayda:**
- Olası response'lar gösterilir
- Hata durumları açıklanır

---

### @SecurityRequirement

**Kullanım:**
```java
@Operation(
    summary = "Ürün oluştur",
    security = @SecurityRequirement(name = "Bearer Authentication")
)
```

**Fayda:**
- JWT token gerektiğini gösterir
- Swagger UI'da "Authorize" butonu görünür

---

## 🎯 Pratik Örnekler

### Örnek 1: Register Endpoint

**Swagger UI'da:**
1. "Authentication" tag'ine tıklayın
2. "POST /api/auth/register" endpoint'ini bulun
3. "Try it out" butonuna tıklayın
4. Request body'yi doldurun:
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User"
}
```
5. "Execute" butonuna tıklayın
6. Response'u görün

---

### Örnek 2: Product Create (JWT Token ile)

**Swagger UI'da:**
1. Önce "Authorize" butonuna tıklayın
2. Login endpoint'inden token alın
3. Token'ı "Authorize" kısmına girin: `Bearer <token>`
4. "Products" tag'ine tıklayın
5. "POST /api/products" endpoint'ini bulun
6. "Try it out" butonuna tıklayın
7. Request body'yi doldurun:
```json
{
  "name": "Laptop",
  "description": "High performance laptop",
  "price": 9999.99,
  "stock": 10
}
```
8. "Execute" butonuna tıklayın
9. Response'u görün

---

## 🔍 Swagger UI Özellikleri

### 1. Endpoint Filtreleme

- Tag'lere göre filtreleme
- Method'lara göre filtreleme
- Arama yapma

---

### 2. Request/Response Örnekleri

- Her endpoint için örnek request
- Her endpoint için örnek response
- Schema görüntüleme

---

### 3. Model Görüntüleme

- DTO'lar görüntülenir
- Field'lar ve tipleri gösterilir
- Validation kuralları gösterilir

---

## 📊 Projede Kullanılan Annotation'lar

### AuthController

```java
@Tag(name = "Authentication", description = "Kullanıcı kaydı, girişi ve kimlik doğrulama işlemleri")
public class AuthController {
    
    @Operation(summary = "Kullanıcı kaydı", description = "...")
    @ApiResponses(value = {...})
    @PostMapping("/register")
    
    @Operation(summary = "Kullanıcı girişi", description = "...")
    @ApiResponses(value = {...})
    @PostMapping("/login")
    
    @Operation(summary = "Mevcut kullanıcı bilgileri", security = @SecurityRequirement(...))
    @ApiResponses(value = {...})
    @GetMapping("/me")
}
```

---

### ProductController

```java
@Tag(name = "Products", description = "Ürün CRUD işlemleri")
public class ProductController {
    
    @Operation(summary = "Yeni ürün oluştur", security = @SecurityRequirement(...))
    @ApiResponses(value = {...})
    @PostMapping
    
    @Operation(summary = "Tüm ürünleri listele", security = @SecurityRequirement(...))
    @ApiResponses(value = {...})
    @GetMapping
    
    @Operation(summary = "Ürün detayı", security = @SecurityRequirement(...))
    @ApiResponses(value = {...})
    @GetMapping("/{id}")
    
    @Operation(summary = "Ürün güncelle", security = @SecurityRequirement(...))
    @ApiResponses(value = {...})
    @PutMapping("/{id}")
    
    @Operation(summary = "Ürün sil", security = @SecurityRequirement(...))
    @ApiResponses(value = {...})
    @DeleteMapping("/{id}")
    
    @Operation(summary = "Kendi ürünlerimi listele", security = @SecurityRequirement(...))
    @ApiResponses(value = {...})
    @GetMapping("/my-products")
}
```

---

## 🎯 Swagger UI Kullanım Senaryoları

### Senaryo 1: API Dokümantasyonu Görüntüleme

1. `http://localhost:8080/swagger-ui.html` adresine gidin
2. Tüm endpoint'leri görüntüleyin
3. Her endpoint için açıklamaları okuyun
4. Request/Response örneklerini inceleyin

---

### Senaryo 2: API Test Etme

1. Swagger UI'da endpoint'e tıklayın
2. "Try it out" butonuna tıklayın
3. Parametreleri doldurun
4. "Execute" butonuna tıklayın
5. Response'u görün

---

### Senaryo 3: JWT Token ile Test

1. Login endpoint'inden token alın
2. "Authorize" butonuna tıklayın
3. Token'ı girin: `Bearer <token>`
4. "Authorize" butonuna tıklayın
5. Artık tüm endpoint'leri test edebilirsiniz

---

## 🔒 Güvenlik Notları

### Swagger UI Erişimi

**Development:**
- ✅ Swagger UI herkese açık (permitAll)
- ✅ API test edilebilir

**Production:**
- ⚠️ Swagger UI'ı kapatın veya sadece yetkili kullanıcılara açın
- ⚠️ API dokümantasyonu production'da gizlenebilir

**Production İçin:**
```java
// SecurityConfig.java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated()
```

---

## 📊 Swagger UI Ekran Görüntüleri

### Ana Sayfa

- Tüm endpoint'ler listelenir
- Tag'lere göre gruplandırılır
- Her endpoint için açıklama

---

### Endpoint Detayı

- Request parametreleri
- Request body örneği
- Response örnekleri
- Hata durumları

---

### Authorize Butonu

- JWT token ekleme
- Token ile test etme
- Güvenli endpoint'ler için gerekli

---

## 🎯 Sonuç

### Swagger/OpenAPI Eklenen Özellikler

1. ✅ **API Dokümantasyonu** → Otomatik oluşturulur
2. ✅ **API Test UI** → Swagger UI ile test edilebilir
3. ✅ **JWT Token Desteği** → Bearer token ile test edilebilir
4. ✅ **Request/Response Örnekleri** → Her endpoint için örnekler
5. ✅ **Model Görüntüleme** → DTO'lar görüntülenir

### Kullanım

1. `http://localhost:8080/swagger-ui.html` adresine gidin
2. Endpoint'leri görüntüleyin
3. "Try it out" ile test edin
4. "Authorize" ile JWT token ekleyin
5. Tüm endpoint'leri test edin

**Swagger/OpenAPI başarıyla eklendi! 🚀**

