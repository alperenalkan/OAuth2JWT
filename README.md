# OAuth2 JWT REST API Project

Bu proje, OAuth2 ve JWT kullanarak güvenli REST API işlemleri yapabilen bir Spring Boot uygulamasıdır.

## Özellikler

- ✅ JWT tabanlı kimlik doğrulama
- ✅ Kullanıcı kaydı ve girişi
- ✅ Product (Ürün) CRUD işlemleri (GET, POST, PUT, DELETE)
- ✅ Role-based authorization (@PreAuthorize)
- ✅ Admin ve User rolleri
- ✅ Admin: Ürün ekleme ve silme yetkisi
- ✅ User: Kendi ürünlerini görüntüleme ve güncelleme
- ✅ Spring Security entegrasyonu
- ✅ PostgreSQL database desteği
- ✅ SQL script'leri (schema.sql, data.sql)
- ✅ Otomatik veritabanı şema oluşturma

## Teknolojiler

- Spring Boot 3.5.7
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- H2 Database
- PostgreSQL
- Lombok
- Maven

## Kurulum

### 1. Projeyi Klonlayın

```bash
git clone <repository-url>
cd OAuth2JWT
```

### 2. Bağımlılıkları Yükleyin

```bash
mvn clean install
```

### 3. Uygulamayı Çalıştırın

```bash
mvn spring-boot:run
```

Uygulama `http://localhost:8080` adresinde çalışacaktır.

### 4. Varsayılan Kullanıcılar

Uygulama ilk çalıştırıldığında otomatik olarak iki kullanıcı oluşturulur:

**Admin Kullanıcısı:**
- Username: `admin`
- Password: `admin123`
- Rol: `ROLE_ADMIN` (Admin + User yetkileri)

**Test Kullanıcısı:**
- Username: `user`
- Password: `user123`
- Rol: `ROLE_USER` (Sadece User yetkileri)

## API Endpoints

### Authentication Endpoints

#### 1. Kullanıcı Kaydı (Register)
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "testuser"
}
```

#### 2. Kullanıcı Girişi (Login)
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "testuser"
}
```

### Product Endpoints

Tüm Product endpoint'leri için JWT token gereklidir. Token'ı header'da şu şekilde gönderin:

```http
Authorization: Bearer <your-jwt-token>
```

#### 1. Ürün Oluşturma (POST) - **SADECE ADMIN**
```http
POST /api/products
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High performance laptop",
  "price": 9999.99,
  "stock": 10
}
```

**Not:** Bu endpoint sadece `ROLE_ADMIN` rolüne sahip kullanıcılar tarafından kullanılabilir. Normal kullanıcılar 403 Forbidden hatası alır.

#### 2. Tüm Ürünleri Listeleme (GET)
```http
GET /api/products
Authorization: Bearer <token>
```

#### 3. Ürün Detayı (GET)
```http
GET /api/products/{id}
Authorization: Bearer <token>
```

#### 4. Ürün Güncelleme (PUT)
```http
PUT /api/products/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Updated Laptop",
  "description": "Updated description",
  "price": 8999.99,
  "stock": 5
}
```

**Not:** 
- **Admin:** Herhangi bir ürünü güncelleyebilir
- **User:** Sadece kendi oluşturdukları ürünleri güncelleyebilir

#### 5. Ürün Silme (DELETE) - **SADECE ADMIN**
```http
DELETE /api/products/{id}
Authorization: Bearer <admin-token>
```

**Not:** Bu endpoint sadece `ROLE_ADMIN` rolüne sahip kullanıcılar tarafından kullanılabilir. Admin herhangi bir ürünü silebilir.

#### 6. Kullanıcının Kendi Ürünlerini Listeleme
```http
GET /api/products/my-products
Authorization: Bearer <token>
```

## Veritabanı Kurulumu

### pgAdmin ile PostgreSQL Kurulumu

**pgAdmin kullanıyorsanız, detaylı rehber için `PGADMIN_GUIDE.md` dosyasına bakın.**

### PostgreSQL Kurulumu

1. **PostgreSQL'i yükleyin:**
   ```bash
   # macOS
   brew install postgresql
   brew services start postgresql
   
   # Linux (Ubuntu/Debian)
   sudo apt-get install postgresql postgresql-contrib
   sudo systemctl start postgresql
   
   # Windows
   # https://www.postgresql.org/download/windows/ adresinden indirin
   ```

2. **pgAdmin Kurulumu:**
   ```bash
   # macOS
   brew install --cask pgadmin4
   
   # Linux
   # https://www.pgadmin.org/download/pgadmin-4-apt/ adresinden kurulum yapın
   
   # Windows
   # PostgreSQL kurulumu ile birlikte gelir veya https://www.pgadmin.org/download/ adresinden indirin
   ```

3. **pgAdmin ile Veritabanı Oluşturma:**
   - pgAdmin'i açın ve PostgreSQL sunucunuza bağlanın
   - Sol panelde "Databases" üzerine sağ tıklayın → "Create" → "Database"
   - Database name: `oauth2jwt`
   - Owner: `postgres` (veya kullanmak istediğiniz kullanıcı)
   - "Save" butonuna tıklayın

4. **SQL Script'lerini pgAdmin'de Çalıştırma:**
   - pgAdmin'de oluşturduğunuz `oauth2jwt` veritabanına sağ tıklayın
   - "Query Tool" seçeneğini seçin
   - `schema.sql` dosyasının içeriğini açın ve Query Tool'a yapıştırın
   - F5 tuşuna basın veya "Execute" butonuna tıklayın
   - Tablolar başarıyla oluşturulacaktır

5. **application.properties dosyasını kontrol edin:**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/oauth2jwt
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

6. **SQL Script'leri:**
   - `schema.sql`: Veritabanı şemasını oluşturur (tablolar, indexler)
   - `data.sql`: İlk verileri ekler (opsiyonel, DataInitializer kullanılıyorsa gerekli değil)
   
   **Not:** SQL script'leri uygulama başlangıcında otomatik olarak çalıştırılır, ancak pgAdmin üzerinden manuel olarak da çalıştırabilirsiniz.

### H2 Database (Development - Opsiyonel)

Development için H2 database kullanmak isterseniz, `application.properties` dosyasını şu şekilde güncelleyin:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

## Yapılandırma

### application.properties

JWT secret key ve expiration time'ı `application.properties` dosyasından değiştirebilirsiniz:

```properties
jwt.secret=mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong
jwt.expiration=86400000
```

### SQL Script'leri

Proje PostgreSQL için hazır SQL script'leri içerir:

- **schema.sql**: Veritabanı şemasını oluşturur
  - `t_users` tablosu
  - `t_user_roles` tablosu
  - `t_products` tablosu
  - İlgili index'ler ve foreign key'ler

- **data.sql**: İlk verileri ekler (opsiyonel)
  - Admin ve test kullanıcıları
  - **Not**: BCrypt hash'leri için DataInitializer kullanılması önerilir

### DataInitializer vs SQL Script'leri

İki yöntem mevcuttur:

1. **DataInitializer (Önerilen)**: 
   - BCrypt hash'leri otomatik oluşturulur
   - `DataInitializer` component'i uygulama başlangıcında çalışır
   - Kullanıcılar zaten varsa tekrar oluşturulmaz

2. **SQL Script'leri**:
   - `data.sql` dosyasını kullanabilirsiniz
   - BCrypt hash'lerini manuel olarak oluşturmanız gerekir
   - `generate-bcrypt-hash.md` dosyasına bakın

### Veritabanı Bağlantı Ayarları

PostgreSQL kullanmak için `application.properties` dosyasını güncelleyin:

```properties
# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/oauth2jwt
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# SQL Script Configuration
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
spring.sql.init.continue-on-error=true
```

## Örnek Kullanım (cURL)

### 1. Kullanıcı Kaydı
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 2. Giriş Yapma
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. Admin ile Giriş Yapma
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 4. Ürün Oluşturma (Admin Gerekli)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High performance laptop",
    "price": 9999.99,
    "stock": 10
  }'
```

### 5. Ürünleri Listeleme
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>"
```

### 6. Ürün Güncelleme
```bash
# Admin herhangi bir ürünü güncelleyebilir
# User sadece kendi ürününü güncelleyebilir
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop",
    "description": "Updated description",
    "price": 8999.99,
    "stock": 5
  }'
```

### 7. Ürün Silme (Admin Gerekli)
```bash
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <admin-token>"
```

## Proje Yapısı

```
src/
├── main/
│   ├── java/
│   │   └── com/tpe/oauth2jwt/
│   │       ├── config/          # Configuration classes (DataInitializer)
│   │       ├── controller/      # REST Controllers
│   │       ├── domain/          # Entity classes
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── exception/       # Exception handlers
│   │       ├── repository/      # Repository interfaces
│   │       ├── security/        # Security configuration
│   │       └── service/         # Business logic
│   └── resources/
│       ├── application.properties
│       ├── schema.sql           # PostgreSQL schema script
│       ├── data.sql             # PostgreSQL data script (opsiyonel)
│       ├── generate-bcrypt-hash.md
│       └── PGADMIN_GUIDE.md     # pgAdmin kullanım kılavuzu
└── test/
    └── java/
        └── com/tpe/oauth2jwt/
```

## Güvenlik ve Yetkilendirme

- JWT token'lar 24 saat geçerlidir (varsayılan)
- Şifreler BCrypt ile hash'lenir
- Tüm product endpoint'leri kimlik doğrulama gerektirir
- **Role-Based Access Control (RBAC)** kullanılır:
  - **ROLE_ADMIN**: 
    - Ürün ekleme (POST) ✅
    - Ürün silme (DELETE) ✅
    - Herhangi bir ürünü güncelleme (PUT) ✅
    - Tüm ürünleri görüntüleme (GET) ✅
  - **ROLE_USER**:
    - Ürün ekleme (POST) ❌
    - Ürün silme (DELETE) ❌
    - Sadece kendi ürünlerini güncelleme (PUT) ✅
    - Tüm ürünleri görüntüleme (GET) ✅
- `@PreAuthorize("hasRole('ADMIN')")` annotation'ı ile endpoint seviyesinde yetkilendirme

## Lisans

Bu proje eğitim amaçlı oluşturulmuştur.

# OAuth2JWT
