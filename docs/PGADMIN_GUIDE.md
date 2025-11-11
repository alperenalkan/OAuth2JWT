# pgAdmin Kullanım Kılavuzu

Bu rehber, OAuth2JWT projesini pgAdmin kullanarak nasıl kurulacağını açıklar.

## 1. pgAdmin Kurulumu

### macOS
```bash
brew install --cask pgadmin4
```

### Linux
```bash
# Ubuntu/Debian
curl -fsS https://www.pgadmin.org/static/packages_pgadmin_org.pub | sudo gpg --dearmor -o /usr/share/keyrings/packages-pgadmin-org.gpg
sudo sh -c 'echo "deb [signed-by=/usr/share/keyrings/packages-pgadmin-org.gpg] https://ftp.postgresql.org/pub/pgadmin/pgadmin4/apt/$(lsb_release -cs) pgadmin4 main" > /etc/apt/sources.list.d/pgadmin4.list'
sudo apt update
sudo apt install pgadmin4
```

### Windows
PostgreSQL kurulumu ile birlikte gelir veya https://www.pgadmin.org/download/ adresinden indirebilirsiniz.

## 2. PostgreSQL Sunucusuna Bağlanma

1. pgAdmin'i açın
2. Sol panelde "Servers" üzerine sağ tıklayın
3. "Create" → "Server" seçeneğini seçin
4. "General" sekmesinde:
   - Name: `Local PostgreSQL` (istediğiniz bir isim)
5. "Connection" sekmesinde:
   - Host name/address: `localhost`
   - Port: `5432`
   - Maintenance database: `postgres`
   - Username: `postgres`
   - Password: PostgreSQL kurulumunda belirlediğiniz şifre
6. "Save" butonuna tıklayın

## 3. Veritabanı Oluşturma

1. pgAdmin'de bağlandığınız sunucunun altında "Databases" klasörünü genişletin
2. "Databases" üzerine sağ tıklayın
3. "Create" → "Database" seçeneğini seçin
4. "General" sekmesinde:
   - Database: `oauth2jwt`
   - Owner: `postgres`
5. "Save" butonuna tıklayın

## 4. SQL Script'lerini Çalıştırma

### Schema.sql Çalıştırma

1. pgAdmin'de oluşturduğunuz `oauth2jwt` veritabanına sağ tıklayın
2. "Query Tool" seçeneğini seçin (veya Alt+Shift+Q)
3. `schema.sql` dosyasını bir metin editöründe açın
4. Tüm içeriği kopyalayıp Query Tool'a yapıştırın
5. F5 tuşuna basın veya üst menüden "Execute" butonuna tıklayın
6. Başarılı mesajını görmelisiniz: "Query returned successfully"

### Data.sql Çalıştırma (Opsiyonel)

**Not:** DataInitializer kullanıyorsanız bu adım gerekli değildir.

1. Aynı şekilde Query Tool'u açın
2. `data.sql` dosyasının içeriğini açın
3. BCrypt hash'lerini oluşturup INSERT statement'larını uncomment edin
4. Query Tool'a yapıştırıp çalıştırın

## 5. Tabloları Kontrol Etme

1. `oauth2jwt` veritabanını genişletin
2. "Schemas" → "public" → "Tables" klasörünü genişletin
3. Aşağıdaki tabloları görmelisiniz:
   - `t_users`
   - `t_user_roles`
   - `t_products`

## 6. Tablo Yapısını İnceleme

1. Herhangi bir tabloya sağ tıklayın
2. "Properties" seçeneğini seçin
3. "Columns" sekmesinde kolonları görebilirsiniz
4. "Constraints" sekmesinde primary key, foreign key gibi kısıtlamaları görebilirsiniz

## 7. Veri Görüntüleme

1. Herhangi bir tabloya sağ tıklayın
2. "View/Edit Data" → "All Rows" seçeneğini seçin
3. Tablodaki tüm verileri görebilirsiniz

## 8. Query Yazma ve Çalıştırma

1. Veritabanına sağ tıklayın → "Query Tool"
2. SQL sorgusu yazın, örneğin:
   ```sql
   SELECT * FROM t_users;
   SELECT * FROM t_products;
   ```
3. F5 tuşuna basın veya "Execute" butonuna tıklayın

## 9. Veritabanı Yedeği Alma

1. Veritabanına sağ tıklayın
2. "Backup" seçeneğini seçin
3. Dosya adı ve konumu seçin
4. "Backup" butonuna tıklayın

## 10. Veritabanı Geri Yükleme

1. "Databases" üzerine sağ tıklayın
2. "Restore" seçeneğini seçin
3. Yedek dosyayı seçin
4. "Restore" butonuna tıklayın

## Yararlı SQL Sorguları

### Tüm kullanıcıları listeleme
```sql
SELECT * FROM t_users;
```

### Kullanıcı rolleri ile birlikte listeleme
```sql
SELECT u.id, u.username, u.email, u.first_name, u.last_name, 
       STRING_AGG(ur.role, ', ') as roles
FROM t_users u
LEFT JOIN t_user_roles ur ON u.id = ur.user_id
GROUP BY u.id, u.username, u.email, u.first_name, u.last_name;
```

### Tüm ürünleri listeleme
```sql
SELECT p.*, u.username as created_by
FROM t_products p
JOIN t_users u ON p.user_id = u.id;
```

### Admin kullanıcısını kontrol etme
```sql
SELECT u.username, ur.role
FROM t_users u
JOIN t_user_roles ur ON u.id = ur.user_id
WHERE u.username = 'admin';
```

## Sorun Giderme

### Bağlantı Hatası
- PostgreSQL servisinin çalıştığından emin olun
- Port 5432'nin açık olduğundan emin olun
- Kullanıcı adı ve şifrenin doğru olduğundan emin olun

### SQL Script Hataları
- Script'leri sırayla çalıştırın (önce schema.sql, sonra data.sql)
- Hata mesajlarını kontrol edin
- Tablolar zaten varsa DROP TABLE ifadeleri çalışacaktır

### Veri Görünmüyor
- DataInitializer'ın çalıştığından emin olun
- Uygulamayı yeniden başlatın
- pgAdmin'de veritabanını yenileyin (F5)

