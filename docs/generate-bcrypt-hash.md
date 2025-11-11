# BCrypt Hash Oluşturma

BCrypt hash'lerini oluşturmak için aşağıdaki yöntemleri kullanabilirsiniz:

## Yöntem 1: Java Kodu ile

Bir test class'ı oluşturun:

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("user123: " + encoder.encode("user123"));
    }
}
```

## Yöntem 2: Spring Boot Application ile

Uygulamayı çalıştırın ve DataInitializer otomatik olarak hash'leri oluşturacaktır.

## Yöntem 3: Online BCrypt Generator

https://bcrypt-generator.com/ adresini kullanarak hash'leri oluşturabilirsiniz.

## Yöntem 4: DataInitializer Kullanımı (Önerilen)

DataInitializer component'i otomatik olarak BCrypt hash'lerini oluşturur ve kullanıcıları veritabanına ekler.
Bu yüzden data.sql dosyasını kullanmak zorunlu değildir.

