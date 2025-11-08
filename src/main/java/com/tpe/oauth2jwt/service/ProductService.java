package com.tpe.oauth2jwt.service;

import com.tpe.oauth2jwt.domain.Product;
import com.tpe.oauth2jwt.domain.User;
import com.tpe.oauth2jwt.dto.ProductRequest;
import com.tpe.oauth2jwt.dto.ProductResponse;
import com.tpe.oauth2jwt.mapper.ProductMapper;
import com.tpe.oauth2jwt.repository.ProductRepository;
import com.tpe.oauth2jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

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

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.productListToProductResponseList(products);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return productMapper.productToProductResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Admin herhangi bir ürünü güncelleyebilir, normal kullanıcı sadece kendi ürününü
        boolean isAdmin = user.getRoles().contains(User.Role.ROLE_ADMIN);
        if (!isAdmin && !product.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this product");
        }

        productMapper.updateProductFromRequest(productRequest, product);

        Product updatedProduct = productRepository.save(product);
        return productMapper.productToProductResponse(updatedProduct);
    }

    public void deleteProduct(Long id, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Admin kontrolü @PreAuthorize ile yapıldığı için burada sadece ürünü siliyoruz
        // Admin herhangi bir ürünü silebilir
        productRepository.delete(product);
    }

    public List<ProductResponse> getProductsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Product> products = productRepository.findByUserId(user.getId());
        return productMapper.productListToProductResponseList(products);
    }
}

