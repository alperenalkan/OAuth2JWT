package com.tpe.oauth2jwt.config;

import com.tpe.oauth2jwt.domain.User;
import com.tpe.oauth2jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
// SQL script'leri kullanıyorsanız bu component'i devre dışı bırakabilirsiniz
// @ConditionalOnProperty(name = "app.data.initializer.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Admin kullanıcısı oluştur
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");

            Set<User.Role> adminRoles = new HashSet<>();
            adminRoles.add(User.Role.ROLE_ADMIN);
            adminRoles.add(User.Role.ROLE_USER);
            admin.setRoles(adminRoles);

            userRepository.save(admin);
            System.out.println("Admin user created successfully!");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
        }

        // Test kullanıcısı oluştur (isteğe bağlı)
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setFirstName("Test");
            user.setLastName("User");

            Set<User.Role> userRoles = new HashSet<>();
            userRoles.add(User.Role.ROLE_USER);
            user.setRoles(userRoles);

            userRepository.save(user);
            System.out.println("Test user created successfully!");
            System.out.println("Username: user");
            System.out.println("Password: user123");
        }
    }
}

