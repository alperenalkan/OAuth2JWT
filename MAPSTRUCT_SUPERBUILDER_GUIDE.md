# MapStruct ve SuperBuilder Kullanım Kılavuzu

Bu projede DTO-Entity dönüşümleri için **MapStruct** ve **Lombok SuperBuilder** kullanılmaktadır.

## Özellikler

- ✅ **MapStruct**: Compile-time code generation ile yüksek performanslı mapper'lar
- ✅ **SuperBuilder**: Builder pattern ile fluent API
- ✅ **Spring Integration**: MapStruct Spring component model ile entegrasyon
- ✅ **Type-safe**: Compile-time'da hata kontrolü

## Kullanılan Teknolojiler

1. **MapStruct 1.5.5.Final**: DTO-Entity dönüşümleri
2. **Lombok SuperBuilder**: Builder pattern desteği
3. **lombok-mapstruct-binding**: Lombok ve MapStruct uyumluluğu

## Yapılandırma

### pom.xml

```xml
<properties>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
</properties>

<dependencies>
    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${mapstruct.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Annotation Processor Configuration

MapStruct ve Lombok birlikte çalışması için annotation processor sırası önemlidir:

```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
    </path>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
    </path>
</annotationProcessorPaths>
```

## Entity ve DTO Yapılandırması

### Product Entity

```java
@Entity
@Table(name = "t_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder  // ← SuperBuilder eklendi
public class Product {
    // fields...
}
```

### ProductRequest DTO

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder  // ← SuperBuilder eklendi
public class ProductRequest {
    // fields...
}
```

### ProductResponse DTO

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder  // ← SuperBuilder eklendi
public class ProductResponse {
    // fields...
}
```

## Mapper Interface

### ProductMapper

```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    // ProductRequest -> Product
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Product productRequestToProduct(ProductRequest productRequest);

    // Product -> ProductResponse
    @Mapping(source = "user.username", target = "createdBy")
    ProductResponse productToProductResponse(Product product);

    // ProductRequest -> Product (Update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateProductFromRequest(ProductRequest productRequest, @MappingTarget Product product);

    // List<Product> -> List<ProductResponse>
    List<ProductResponse> productListToProductResponseList(List<Product> products);
}
```

## Service Katmanında Kullanım

### Önceki Kod (Manuel Mapping)

```java
public ProductResponse createProduct(ProductRequest productRequest, String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Product product = new Product();
    product.setName(productRequest.getName());
    product.setDescription(productRequest.getDescription());
    product.setPrice(productRequest.getPrice());
    product.setStock(productRequest.getStock());
    product.setUser(user);

    Product savedProduct = productRepository.save(product);
    
    ProductResponse response = new ProductResponse();
    response.setId(savedProduct.getId());
    response.setName(savedProduct.getName());
    // ... diğer alanlar
    return response;
}
```

### Yeni Kod (MapStruct ile)

```java
@Autowired
private ProductMapper productMapper;

public ProductResponse createProduct(ProductRequest productRequest, String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Product product = productMapper.productRequestToProduct(productRequest);
    product.setUser(user);

    Product savedProduct = productRepository.save(product);
    return productMapper.productToProductResponse(savedProduct);
}
```

## SuperBuilder Kullanımı (Opsiyonel)

SuperBuilder ile fluent API kullanabilirsiniz:

```java
// ProductRequest oluşturma
ProductRequest request = ProductRequest.builder()
    .name("Laptop")
    .description("High performance laptop")
    .price(9999.99)
    .stock(10)
    .build();

// ProductResponse oluşturma
ProductResponse response = ProductResponse.builder()
    .id(1L)
    .name("Laptop")
    .description("High performance laptop")
    .price(9999.99)
    .stock(10)
    .createdAt(LocalDateTime.now())
    .updatedAt(LocalDateTime.now())
    .createdBy("admin")
    .build();
```

## Mapping Özellikleri

### 1. Basit Mapping
Alan adları aynıysa otomatik mapping yapılır:

```java
@Mapping(source = "user.username", target = "createdBy")
ProductResponse productToProductResponse(Product product);
```

### 2. Ignore Fields
Belirli alanları ignore edebilirsiniz:

```java
@Mapping(target = "id", ignore = true)
@Mapping(target = "createdAt", ignore = true)
Product productRequestToProduct(ProductRequest productRequest);
```

### 3. Update Mapping
Mevcut entity'yi güncellemek için:

```java
void updateProductFromRequest(ProductRequest productRequest, @MappingTarget Product product);
```

### 4. Collection Mapping
List mapping otomatik yapılır:

```java
List<ProductResponse> productListToProductResponseList(List<Product> products);
```

## Avantajlar

1. **Performans**: Compile-time code generation, runtime reflection yok
2. **Type-safe**: Compile-time'da hata kontrolü
3. **Temiz Kod**: Manuel mapping kodları yok
4. **Bakım Kolaylığı**: Değişiklikler tek yerden yapılır
5. **Test Edilebilirlik**: Mapper'lar bağımsız test edilebilir

## Oluşturulan Kod

MapStruct compile-time'da şu şekilde implementasyon oluşturur:

```java
@Component
public class ProductMapperImpl implements ProductMapper {
    
    @Override
    public Product productRequestToProduct(ProductRequest productRequest) {
        if (productRequest == null) {
            return null;
        }
        
        Product.ProductBuilder product = Product.builder();
        product.name(productRequest.getName());
        product.description(productRequest.getDescription());
        product.price(productRequest.getPrice());
        product.stock(productRequest.getStock());
        return product.build();
    }
    
    // ... diğer metodlar
}
```

## Sorun Giderme

### MapStruct Implementasyonu Oluşturulmuyor

1. Maven clean compile yapın:
   ```bash
   mvn clean compile
   ```

2. IDE'yi yeniden başlatın

3. Annotation processing'in aktif olduğundan emin olun

### Lombok ve MapStruct Uyumsuzluğu

1. `lombok-mapstruct-binding` dependency'sinin eklendiğinden emin olun
2. Annotation processor sırasını kontrol edin (Lombok önce olmalı)

### SuperBuilder Hatası

1. Lombok version'ını kontrol edin (1.18.2+)
2. `@SuperBuilder` annotation'ının doğru import edildiğinden emin olun

## Örnek Kullanım

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    public ProductResponse createProduct(ProductRequest request, String username) {
        // DTO -> Entity
        Product product = productMapper.productRequestToProduct(request);
        product.setUser(user);
        
        // Save
        Product saved = productRepository.save(product);
        
        // Entity -> DTO
        return productMapper.productToProductResponse(saved);
    }
    
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow();
        
        // Update mapping
        productMapper.updateProductFromRequest(request, product);
        
        Product updated = productRepository.save(product);
        return productMapper.productToProductResponse(updated);
    }
}
```

## Sonuç

MapStruct ve SuperBuilder kullanarak:
- ✅ Temiz ve okunabilir kod
- ✅ Yüksek performans
- ✅ Type-safe mapping
- ✅ Kolay bakım

Bu yapı sayesinde DTO-Entity dönüşümleri otomatik ve güvenli hale geldi.

