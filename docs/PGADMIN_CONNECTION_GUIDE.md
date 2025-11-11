# pgAdmin Bağlantı Rehberi

## Önemli: Docker Container'ındaki PostgreSQL'e Bağlanma

Docker Compose ile çalıştırılan PostgreSQL container'ı **port 5433**'te çalışıyor (yerel PostgreSQL port 5432'de çalışıyorsa çakışmayı önlemek için).

## pgAdmin Bağlantı Ayarları

### 1. pgAdmin'de Yeni Server Oluşturma

1. pgAdmin'i açın
2. Sol panelde "Servers" üzerine sağ tıklayın
3. "Register" → "Server" seçin

### 2. General Tab

- **Name**: `OAuth2JWT Docker` (istediğiniz bir isim)

### 3. Connection Tab

- **Host name/address**: `localhost` veya `127.0.0.1`
- **Port**: `5433` ⚠️ **ÖNEMLİ: 5432 değil, 5433!**
- **Maintenance database**: `oauth2jwt`
- **Username**: `techpront`
- **Password**: `125322`
- **Save password**: ✅ (isteğe bağlı, şifreyi kaydetmek için)

### 4. Advanced Tab (Opsiyonel)

- **DB restriction**: `oauth2jwt` (sadece bu veritabanını görmek için)

### 5. Save

"Save" butonuna tıklayın.

## Veritabanını Kontrol Etme

### Tabloları Görmek İçin

1. Sol panelde "Servers" → "OAuth2JWT Docker" → "Databases" → "oauth2jwt" → "Schemas" → "public" → "Tables"
2. Tabloları görmek için "Tables" üzerine sağ tıklayıp "Refresh" yapın

### Ürünleri Görmek İçin

1. `t_products` tablosuna sağ tıklayın
2. "View/Edit Data" → "All Rows" seçin
3. Veya Query Tool kullanın:

```sql
SELECT 
    p.id,
    p.name,
    p.description,
    p.price,
    p.stock,
    u.username as created_by,
    p.created_at,
    p.updated_at
FROM t_products p
JOIN t_users u ON p.user_id = u.id
ORDER BY p.created_at DESC;
```

## Sorun Giderme

### Sorun 1: "Connection refused" veya "Could not connect to server"

**Çözüm:**
- Docker container'ının çalıştığından emin olun:
  ```bash
  docker-compose ps
  ```
- Port 5433'ün doğru olduğundan emin olun
- pgAdmin'de port'un **5433** olduğunu kontrol edin (5432 değil!)

### Sorun 2: "Database oauth2jwt does not exist"

**Çözüm:**
- Veritabanının oluşturulduğundan emin olun:
  ```bash
  docker exec oauth2jwt-postgres psql -U techpront -d oauth2jwt -c "\l"
  ```
- Eğer veritabanı yoksa, oluşturun:
  ```bash
  docker exec oauth2jwt-postgres psql -U techpront -c "CREATE DATABASE oauth2jwt;"
  ```

### Sorun 3: "Password authentication failed"

**Çözüm:**
- Username: `techpront`
- Password: `125322`
- Bu bilgileri kontrol edin

### Sorun 4: Tablolar görünmüyor

**Çözüm:**
1. "Tables" üzerine sağ tıklayıp "Refresh" yapın
2. Schema'nın "public" olduğundan emin olun
3. Query Tool'da şu sorguyu çalıştırın:
   ```sql
   SELECT table_name 
   FROM information_schema.tables 
   WHERE table_schema = 'public';
   ```

### Sorun 5: Ürünler görünmüyor ama API'de var

**Çözüm:**
1. Tabloyu refresh edin
2. Query Tool'da şu sorguyu çalıştırın:
   ```sql
   SELECT * FROM t_products;
   ```
3. Eğer hala görünmüyorsa, uygulamanın hangi veritabanına bağlandığını kontrol edin:
   ```bash
   docker logs oauth2jwt-app | grep -i "datasource\|database\|postgresql"
   ```

## Yerel PostgreSQL Kullanıyorsanız

Eğer `docker-compose.local.yml` kullanıyorsanız (yerel PostgreSQL'e bağlanmak için):

- **Host**: `localhost`
- **Port**: `5432` (yerel PostgreSQL portu)
- **Database**: `oauth2jwt`
- **Username**: `techpront` (veya yerel PostgreSQL kullanıcınız)
- **Password**: `125322` (veya yerel PostgreSQL şifreniz)

## Hızlı Kontrol

Veritabanına bağlanıp ürünleri görmek için terminal'de:

```bash
# Docker container içindeki PostgreSQL'e bağlan
docker exec -it oauth2jwt-postgres psql -U techpront -d oauth2jwt

# Ürünleri listele
SELECT * FROM t_products;

# Kullanıcılarla birlikte ürünleri listele
SELECT p.id, p.name, u.username, p.created_at 
FROM t_products p 
JOIN t_users u ON p.user_id = u.id;
```

## Özet

**pgAdmin Bağlantı Bilgileri:**
- Host: `localhost`
- Port: `5433` ⚠️ (5432 değil!)
- Database: `oauth2jwt`
- Username: `techpront`
- Password: `125322`

**Önemli Notlar:**
- Docker container'ı çalışıyor olmalı
- Port 5433 kullanılmalı (5432 değil)
- Tabloları görmek için "Refresh" yapın
- Schema "public" olmalı

