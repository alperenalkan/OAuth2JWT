package com.tpe.oauth2jwt.mapper;

import com.tpe.oauth2jwt.domain.Product;
import com.tpe.oauth2jwt.domain.User;
import com.tpe.oauth2jwt.dto.ProductRequest;
import com.tpe.oauth2jwt.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // ProductRequest -> Product (Entity oluşturma için)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Product productRequestToProduct(ProductRequest productRequest);

    // Product -> ProductResponse (default method ile user mapping)
    default ProductResponse productToProductResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .createdBy(product.getUser() != null ? product.getUser().getUsername() : null)
                .user(userToUserSummaryDTO(product.getUser()))
                .build();
    }

    // ProductRequest -> Product (Entity güncelleme için)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateProductFromRequest(ProductRequest productRequest, @MappingTarget Product product);

    // List<Product> -> List<ProductResponse>
    List<ProductResponse> productListToProductResponseList(List<Product> products);

    // User -> UserSummaryDTO mapping (default method)
    default ProductResponse.UserSummaryDTO userToUserSummaryDTO(User user) {
        if (user == null) {
            return null;
        }
        
        ProductResponse.UserSummaryDTO dto = new ProductResponse.UserSummaryDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFirstName() + " " + user.getLastName());
        return dto;
    }
}

