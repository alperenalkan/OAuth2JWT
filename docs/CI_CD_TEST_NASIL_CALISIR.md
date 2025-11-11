# CI/CD Test Nasıl Çalışır ve Bug'ları Nasıl Bulur?

## 🎯 CI/CD Test Süreci

### 1. Test Çalıştırma Adımları

CI/CD pipeline'ı şu adımları izler:

```
1. Push yapıldı
   ↓
2. GitHub Actions tetiklenir
   ↓
3. PostgreSQL container başlatılır
   ↓
4. Java 17 kurulumu
   ↓
5. Maven test komutu çalıştırılır
   ↓
6. Test sonuçları kontrol edilir
   ↓
7. Başarılı → Build devam eder
   Başarısız → Build durur, hata gösterilir
```

---

## 🔍 Test Nasıl Çalışır?

### Adım 1: PostgreSQL Container Başlatma

```yaml
services:
  postgres:
    image: postgres:15-alpine
    env:
      POSTGRES_DB: oauth2jwt_test
      POSTGRES_USER: techpront
      POSTGRES_PASSWORD: 125322
    ports:
      - 5432:5432
    options: >-
      --health-cmd pg_isready
      --health-interval 10s
      --health-timeout 5s
      --health-retries 5
```

**Ne Yapıyor:**
- Test için PostgreSQL container'ı başlatır
- Health check ile hazır olmasını bekler
- Test veritabanı oluşturur

**Neden Önemli:**
- Testler gerçek veritabanına ihtiyaç duyar
- Her test için temiz bir veritabanı sağlar
- Production veritabanını etkilemez

---

### Adım 2: Maven Test Komutu

```yaml
- name: Run tests
  run: mvn clean test
  env:
    SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/oauth2jwt_test
    SPRING_DATASOURCE_USERNAME: techpront
    SPRING_DATASOURCE_PASSWORD: 125322
```

**Ne Yapıyor:**
- `mvn clean test` komutu çalıştırılır
- Tüm test dosyaları (`*Test.java`) bulunur
- Her test sırayla çalıştırılır
- Test sonuçları raporlanır

**Maven Test Komutu:**
```bash
mvn clean test
```

Bu komut:
1. `src/test/java` klasöründeki tüm test dosyalarını bulur
2. Her test metodunu (`@Test` ile işaretlenmiş) çalıştırır
3. Test sonuçlarını raporlar
4. Başarısız test varsa build'i durdurur

---

## 🐛 Bug'ları Nasıl Bulur?

### Senaryo 1: Compile Hatası (Derleme Hatası)

**Örnek Bug:**
```java
// Hatalı kod
public void testMethod() {
    String name = null;
    int length = name.length(); // NullPointerException!
}
```

**CI/CD Ne Yapar:**
```
1. Maven test komutu çalışır
2. Kod derlenmeye çalışılır
3. Compile hatası bulunur
4. Build başarısız olur
5. Hata logları gösterilir
```

**Sonuç:**
- ❌ Build başarısız
- ❌ Docker image oluşturulmaz
- ❌ Production'a deploy edilmez
- ✅ Hata erken yakalanır

---

### Senaryo 2: Test Başarısız

**Örnek Bug:**
```java
@Test
void testUserCreation() {
    User user = new User();
    user.setUsername("test");
    user.setPassword("123");
    
    // Beklenen: User kaydedilmeli
    userService.save(user);
    
    // Test: User bulunabilmeli
    User found = userService.findByUsername("test");
    assertNotNull(found); // Bu başarısız olabilir!
}
```

**CI/CD Ne Yapar:**
```
1. Test çalıştırılır
2. Assert başarısız olur
3. Test exception fırlatır
4. Maven test başarısız olur
5. Build durur
```

**Sonuç:**
- ❌ Test başarısız
- ❌ Build başarısız
- ✅ Bug erken yakalanır
- ✅ Production'a gitmez

---

### Senaryo 3: Runtime Hatası

**Örnek Bug:**
```java
@Test
void testDatabaseConnection() {
    // Veritabanı bağlantısı test edilir
    DataSource dataSource = applicationContext.getBean(DataSource.class);
    Connection conn = dataSource.getConnection();
    // Eğer bağlantı başarısız olursa exception fırlatır
}
```

**CI/CD Ne Yapar:**
```
1. Test çalıştırılır
2. Veritabanı bağlantısı başarısız olur
3. Exception fırlatılır
4. Test başarısız olur
5. Build durur
```

**Sonuç:**
- ❌ Test başarısız
- ❌ Build başarısız
- ✅ Konfigürasyon hatası erken yakalanır

---

### Senaryo 4: Integration Test Hatası

**Örnek Bug:**
```java
@Test
void testProductCreation() {
    // Product oluşturulur
    Product product = new Product();
    product.setName("Laptop");
    product.setPrice(1000.0);
    
    // Service ile kaydedilir
    productService.save(product);
    
    // Repository'den bulunur
    Product found = productRepository.findById(product.getId());
    
    // Assert: Bulunmalı
    assertNotNull(found); // Eğer kayıt başarısız olduysa null döner
    assertEquals("Laptop", found.getName());
}
```

**CI/CD Ne Yapar:**
```
1. Test çalıştırılır
2. Product kaydedilmeye çalışılır
3. Veritabanı hatası olursa exception fırlatır
4. Test başarısız olur
5. Build durur
```

**Sonuç:**
- ❌ Test başarısız
- ❌ Build başarısız
- ✅ Veritabanı entegrasyon hatası erken yakalanır

---

## 📊 Test Türleri ve Bug Bulma

### 1. Unit Test (Birim Test)

**Ne Test Eder:**
- Tek bir metodun doğru çalışıp çalışmadığını test eder
- Bağımlılıklar mock'lanır

**Örnek:**
```java
@Test
void testPasswordEncryption() {
    String password = "123456";
    String encrypted = passwordEncoder.encode(password);
    
    assertNotNull(encrypted);
    assertNotEquals(password, encrypted);
    assertTrue(passwordEncoder.matches(password, encrypted));
}
```

**Bug Bulma:**
- Şifreleme algoritması hatalıysa test başarısız olur
- Hash fonksiyonu çalışmıyorsa test başarısız olur

---

### 2. Integration Test (Entegrasyon Testi)

**Ne Test Eder:**
- Birden fazla component'in birlikte çalışıp çalışmadığını test eder
- Gerçek veritabanı kullanılır

**Örnek:**
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testUserCreationAndRetrieval() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        
        userService.save(user);
        
        User found = userRepository.findByUsername("testuser");
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }
}
```

**Bug Bulma:**
- Repository kayıt yapmıyorsa test başarısız olur
- Service ve Repository arasında sorun varsa test başarısız olur
- Veritabanı constraint'leri ihlal edilirse test başarısız olur

---

### 3. Spring Boot Test (Context Test)

**Ne Test Eder:**
- Spring Boot context'inin doğru yüklenip yüklenmediğini test eder
- Bean'lerin doğru oluşturulup oluşturulmadığını test eder

**Örnek:**
```java
@SpringBootTest
class OAuth2JwtApplicationTests {
    
    @Test
    void contextLoads() {
        // Spring context yüklenirse test başarılı
        // Eğer bir bean oluşturulamazsa test başarısız olur
    }
}
```

**Bug Bulma:**
- Bean tanımları hatalıysa context yüklenmez
- Configuration hatalıysa context yüklenmez
- Dependency injection hatalıysa context yüklenmez

---

## 🔍 CI/CD Test Süreci Detayları

### Test Çalıştırma Komutu

```bash
mvn clean test
```

**Bu Komut Ne Yapar:**

1. **Clean:** Önceki build dosyalarını temizler
2. **Compile:** Test kodlarını derler
3. **Test:** Tüm testleri çalıştırır
4. **Report:** Test sonuçlarını raporlar

### Test Sonuçları

**Başarılı Test:**
```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Başarısız Test:**
```
[ERROR] Tests run: 5, Failures: 1, Errors: 0, Skipped: 0
[ERROR] testUserCreation(com.tpe.oauth2jwt.UserServiceTest) FAILED
[ERROR]   java.lang.AssertionError: expected: <testuser> but was: <null>
[ERROR] BUILD FAILURE
```

---

## 🎯 Bug Bulma Örnekleri

### Örnek 1: NullPointerException

**Hatalı Kod:**
```java
public String getUserName(Long userId) {
    User user = userRepository.findById(userId);
    return user.getUsername(); // user null olabilir!
}
```

**Test:**
```java
@Test
void testGetUserName() {
    String username = userService.getUserName(999L); // Olmayan ID
    // NullPointerException fırlatır
}
```

**CI/CD Sonucu:**
- ❌ Test başarısız
- ❌ Build başarısız
- ✅ Bug erken yakalanır

---

### Örnek 2: Veritabanı Constraint Hatası

**Hatalı Kod:**
```java
public void createUser(String username, String password) {
    User user = new User();
    user.setUsername(username); // Unique constraint var
    user.setPassword(password);
    userRepository.save(user); // Aynı username varsa hata!
}
```

**Test:**
```java
@Test
void testDuplicateUsername() {
    userService.createUser("admin", "password");
    userService.createUser("admin", "password"); // Aynı username!
    // ConstraintViolationException fırlatır
}
```

**CI/CD Sonucu:**
- ❌ Test başarısız
- ❌ Build başarısız
- ✅ Constraint hatası erken yakalanır

---

### Örnek 3: Business Logic Hatası

**Hatalı Kod:**
```java
public double calculatePrice(Product product) {
    return product.getPrice() * 0.20; // %20 indirim yapıyor
    // Ama %10 indirim yapması gerekiyor!
}
```

**Test:**
```java
@Test
void testPriceCalculation() {
    Product product = new Product();
    product.setPrice(100.0);
    
    double finalPrice = productService.calculatePrice(product);
    
    assertEquals(90.0, finalPrice); // %10 indirim = 90 TL
    // Ama kod %20 indirim yapıyor = 80 TL
    // Test başarısız!
}
```

**CI/CD Sonucu:**
- ❌ Test başarısız
- ❌ Build başarısız
- ✅ Business logic hatası erken yakalanır

---

## 📈 Test Coverage (Test Kapsamı)

### Test Coverage Nedir?

Test coverage, kodunuzun ne kadarının test edildiğini gösterir.

**Örnek:**
```
Kod Satırları: 1000
Test Edilen Satırlar: 800
Test Coverage: %80
```

**CI/CD ile Test Coverage:**

```yaml
- name: Run tests with coverage
  run: mvn clean test jacoco:report
```

**Faydası:**
- Hangi kodların test edilmediğini görürsünüz
- Test edilmemiş kodlar risk taşır
- Coverage raporu CI/CD'de gösterilir

---

## 🚨 Hata Yakalama Senaryoları

### Senaryo 1: Push Yapıldı, Test Başarısız

```
1. Kod yazdınız
2. Push yaptınız
3. CI/CD test çalıştırdı
4. Test başarısız oldu
5. Build durdu
6. GitHub'da ❌ işareti göründü
7. Hata loglarını incelediniz
8. Bug'ı düzelttiniz
9. Tekrar push yaptınız
10. Test geçti ✅
```

### Senaryo 2: Pull Request Açıldı, Test Başarısız

```
1. Feature branch oluşturdunuz
2. Kod yazdınız
3. Pull request açtınız
4. CI/CD otomatik test çalıştırdı
5. Test başarısız oldu
6. PR'da ❌ işareti göründü
7. Code review yapılamadı (test geçmeden)
8. Bug'ı düzelttiniz
9. Push yaptınız
10. Test geçti ✅
11. PR merge edilebilir hale geldi
```

---

## 💡 Best Practices

### 1. Her Özellik İçin Test Yazın

```java
// Her yeni özellik için test yazın
@Test
void testNewFeature() {
    // Test kodları
}
```

### 2. Test İsimlendirme

```java
// İyi test isimleri
@Test
void testUserCreationWithValidData() { }

@Test
void testUserCreationWithDuplicateUsername() { }

// Kötü test isimleri
@Test
void test1() { } // Ne test ettiği belli değil
```

### 3. Assert Mesajları

```java
@Test
void testUserCreation() {
    User user = userService.createUser("test", "password");
    
    assertNotNull(user, "User should not be null");
    assertEquals("test", user.getUsername(), "Username should match");
}
```

### 4. Test Isolation (Test İzolasyonu)

```java
@SpringBootTest
@Transactional // Her test sonunda rollback
class UserServiceTest {
    
    @Test
    void test1() {
        // Test 1
    }
    
    @Test
    void test2() {
        // Test 2 (test1'in verilerinden etkilenmez)
    }
}
```

---

## 🎯 Sonuç

### CI/CD Test Süreci:

1. ✅ **Otomatik Test Çalıştırma** → Her push'ta
2. ✅ **Hata Yakalama** → Compile, runtime, logic hataları
3. ✅ **Erken Tespit** → Production'a gitmeden önce
4. ✅ **Raporlama** → Detaylı hata logları
5. ✅ **Build Kontrolü** → Test geçmeden build olmaz

### Bug Bulma Yöntemleri:

- ✅ **Compile Hatası** → Kod derlenemez
- ✅ **Test Başarısız** → Assert başarısız
- ✅ **Runtime Hatası** → Exception fırlatılır
- ✅ **Integration Hatası** → Component'ler çalışmaz
- ✅ **Business Logic Hatası** → Beklenen sonuç gelmez

**Sonuç:** CI/CD, testler sayesinde bug'ları erken yakalar ve production'a gitmesini engeller! 🚀

