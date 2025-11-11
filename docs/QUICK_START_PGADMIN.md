# pgAdmin Hızlı Başlangıç Rehberi

Bu rehber, pgAdmin kullanarak OAuth2JWT projesini 5 dakikada kurmanızı sağlar.

## Adım 1: pgAdmin'i Açın ve Bağlanın

1. pgAdmin'i açın
2. Sol panelde "Servers" üzerine sağ tıklayın → "Create" → "Server"
3. Connection bilgilerini girin:
   - **Name**: `Local PostgreSQL`
   - **Host**: `localhost`
   - **Port**: `5432`
   - **Username**: `postgres`
   - **Password**: PostgreSQL şifreniz
4. "Save" butonuna tıklayın

## Adım 2: Veritabanı Oluşturun

1. Sol panelde "Databases" üzerine sağ tıklayın
2. "Create" → "Database" seçin
3. Database name: `oauth2jwt`
4. Owner: `postgres`
5. "Save" butonuna tıklayın

## Adım 3: SQL Script'ini Çalıştırın

1. Oluşturduğunuz `oauth2jwt` veritabanına sağ tıklayın
2. "Query Tool" seçin (veya `Alt+Shift+Q`)
3. `schema.sql` dosyasını açın ve içeriğini kopyalayın
4. Query Tool'a yapıştırın
5. `F5` tuşuna basın veya "Execute" butonuna tıklayın
6. "Query returned successfully" mesajını görmelisiniz

## Adım 4: Tabloları Kontrol Edin

1. `oauth2jwt` → "Schemas" → "public" → "Tables" klasörünü genişletin
2. Şu tabloları görmelisiniz:
   - ✅ `t_users`
   - ✅ `t_user_roles`
   - ✅ `t_products`

## Adım 5: Uygulamayı Başlatın

1. `application.properties` dosyasını kontrol edin:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/oauth2jwt
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

2. Uygulamayı çalıştırın:
   ```bash
   mvn spring-boot:run
   ```

3. Uygulama başladığında, DataInitializer otomatik olarak admin ve user kullanıcılarını oluşturacaktır.

## Adım 6: Verileri Kontrol Edin (pgAdmin'de)

1. `t_users` tablosuna sağ tıklayın
2. "View/Edit Data" → "All Rows" seçin
3. Admin ve user kullanıcılarını görmelisiniz

## Hızlı SQL Sorguları (pgAdmin Query Tool)

### Tüm kullanıcıları görüntüleme
```sql
SELECT * FROM t_users;
```

### Kullanıcı rolleri ile birlikte
```sql
SELECT u.username, u.email, STRING_AGG(ur.role, ', ') as roles
FROM t_users u
LEFT JOIN t_user_roles ur ON u.id = ur.user_id
GROUP BY u.id, u.username, u.email;
```

### Tüm ürünleri görüntüleme
```sql
SELECT p.*, u.username as created_by
FROM t_products p
JOIN t_users u ON p.user_id = u.id;
```

## Sorun Giderme

**Bağlantı hatası alıyorsanız:**
- PostgreSQL servisinin çalıştığından emin olun
- Port 5432'nin açık olduğundan emin olun
- Kullanıcı adı ve şifrenin doğru olduğundan emin olun

**Tablolar görünmüyorsa:**
- Query Tool'da `schema.sql` script'ini çalıştırdığınızdan emin olun
- Hata mesajlarını kontrol edin
- Veritabanını yenileyin (F5)

**Veriler görünmüyorsa:**
- Uygulamanın başarıyla başlatıldığından emin olun
- DataInitializer'ın çalıştığından emin olun (console loglarını kontrol edin)
- pgAdmin'de veritabanını yenileyin

## Sonraki Adımlar

- Detaylı pgAdmin kılavuzu için `PGADMIN_GUIDE.md` dosyasına bakın
- API endpoint'leri için `README.md` dosyasına bakın
- BCrypt hash oluşturma için `generate-bcrypt-hash.md` dosyasına bakın

