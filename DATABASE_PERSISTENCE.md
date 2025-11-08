# Veritabanı Verilerinin Kalıcılığı (Persistence)

## Sorun
Docker container'ları her başlatıldığında veritabanı verileri kayboluyordu.

## Neden?
`schema.sql` dosyasında `DROP TABLE IF EXISTS` komutları vardı. Spring Boot, `spring.sql.init.mode=always` ayarı nedeniyle her başlangıçta bu script'i çalıştırıyordu ve tablolar silinip yeniden oluşturuluyordu.

## Çözüm
`schema.sql` dosyası güncellendi:
- `DROP TABLE` komutları kaldırıldı
- `CREATE TABLE IF NOT EXISTS` kullanıldı
- `CREATE INDEX IF NOT EXISTS` kullanıldı

Bu sayede:
- Tablolar zaten varsa, yeniden oluşturulmaz
- Mevcut veriler korunur
- Sadece tablolar yoksa oluşturulur

## Docker Volume'ları
Veritabanı verileri Docker volume'larında saklanır:
- Volume adı: `oauth2jwt_postgres_data`
- Lokasyon: `/var/lib/docker/volumes/oauth2jwt_postgres_data/_data`

## Veriler Ne Zaman Kaybolur?

### ✅ Veriler KORUNUR:
- `docker-compose restart` - Container'lar yeniden başlatılır, veriler korunur
- `docker-compose stop` ve `docker-compose start` - Container'lar durdurulup başlatılır, veriler korunur
- `docker-compose down` - Container'lar kaldırılır, ama volume'lar korunur
- Uygulama yeniden başlatıldığında - Veriler korunur (artık `IF NOT EXISTS` kullanıldığı için)

### ❌ Veriler KAYBOLUR:
- `docker-compose down -v` - Volume'lar da silinir, tüm veriler kaybolur
- `docker volume rm oauth2jwt_postgres_data` - Volume manuel olarak silinirse, veriler kaybolur
- `docker-compose down` ve volume'u manuel silme

## Verileri Yedekleme

### 1. pg_dump ile Yedekleme
```bash
docker exec oauth2jwt-postgres pg_dump -U techpront -d oauth2jwt > backup.sql
```

### 2. Yedekten Geri Yükleme
```bash
docker exec -i oauth2jwt-postgres psql -U techpront -d oauth2jwt < backup.sql
```

### 3. Volume'u Yedekleme
```bash
docker run --rm -v oauth2jwt_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz /data
```

## Verileri Temizleme (Tüm Verileri Silme)

Eğer tüm verileri silmek isterseniz:

```bash
# Container'ları durdur ve volume'ları sil
docker-compose down -v

# Veya sadece volume'u sil
docker volume rm oauth2jwt_postgres_data
```

**DİKKAT:** Bu işlem tüm verileri kalıcı olarak siler!

## Verileri Koruma İpuçları

1. **Volume'ları Yedekleyin**: Düzenli olarak yedek alın
2. **`-v` Parametresini Kullanmayın**: `docker-compose down -v` komutunu dikkatli kullanın
3. **Production'da**: Production ortamında volume'ları düzenli olarak yedekleyin ve yedekleme stratejisi oluşturun

## Test Etme

Verilerin korunup korunmadığını test etmek için:

```bash
# 1. Bir ürün oluşturun
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","description":"Test","price":100,"stock":10}'

# 2. Container'ları yeniden başlatın
docker-compose restart

# 3. Ürünlerin hala var olduğunu kontrol edin
docker exec oauth2jwt-postgres psql -U techpront -d oauth2jwt -c "SELECT * FROM t_products;"
```

## Özet

- ✅ Artık `IF NOT EXISTS` kullanıldığı için tablolar yeniden oluşturulmaz
- ✅ Docker volume'ları verileri kalıcı olarak saklar
- ✅ `docker-compose restart` ile veriler korunur
- ✅ `docker-compose down -v` ile veriler kaybolur (dikkatli kullanın!)
- ✅ Düzenli yedekleme yapın

