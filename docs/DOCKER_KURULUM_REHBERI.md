# Docker Kurulum Rehberi - OAuth2JWT Projesi

Bu rehber, OAuth2JWT projesini Docker ile nasıl kurup çalıştıracağınızı adım adım açıklar.

## 📋 İçindekiler

1. [Kullanılan Dosyalar](#kullanılan-dosyalar)
2. [Gereksinimler](#gereksinimler)
3. [Kurulum Adımları](#kurulum-adımları)
4. [Docker Dosyalarının Açıklaması](#docker-dosyalarının-açıklaması)
5. [Kullanım Senaryoları](#kullanım-senaryoları)
6. [Sorun Giderme](#sorun-giderme)

---

## 📁 Kullanılan Dosyalar

Projede Docker kurulumu için aşağıdaki dosyalar kullanılmaktadır:

### 1. **Dockerfile**
- **Konum**: Proje kök dizini (`/Dockerfile`)
- **Amaç**: Spring Boot uygulamasını containerize etmek için kullanılır
- **Özellikler**:
  - Multi-stage build (2 aşamalı derleme)
  - Maven ile build aşaması
  - JRE ile runtime aşaması
  - Non-root user (güvenlik)
  - Health check desteği

### 2. **docker-compose.yml**
- **Konum**: Proje kök dizini (`/docker-compose.yml`)
- **Amaç**: PostgreSQL ve Spring Boot uygulamasını birlikte çalıştırmak
- **İçerik**:
  - PostgreSQL 15 servisi
  - Spring Boot uygulama servisi
  - Network yapılandırması
  - Volume yapılandırması
  - Health check'ler

### 3. **docker-compose.local.yml**
- **Konum**: Proje kök dizini (`/docker-compose.local.yml`)
- **Amaç**: Yerel (host) PostgreSQL kullanarak sadece uygulamayı çalıştırmak
- **Kullanım**: Zaten bilgisayarınızda PostgreSQL kuruluysa

---

## 🔧 Gereksinimler

Kurulumdan önce aşağıdaki yazılımların yüklü olması gerekir:

- ✅ **Docker** (v20.10 veya üzeri)
- ✅ **Docker Compose** (v2.0 veya üzeri)
- ✅ **Java 17** (sadece local build için, Docker içinde zaten var)
- ✅ **Maven** (sadece local build için, Docker içinde zaten var)

### Docker Kurulum Kontrolü

```bash
# Docker versiyonunu kontrol et
docker --version

# Docker Compose versiyonunu kontrol et
docker-compose --version

# Docker servisinin çalıştığını kontrol et
docker ps
```

---

## 🚀 Kurulum Adımları

### Senaryo 1: Docker Compose ile Tam Kurulum (Önerilen)

Bu yöntem, PostgreSQL ve uygulamayı birlikte Docker container'larında çalıştırır.

#### Adım 1: Proje Dizinine Git

```bash
cd /Users/alperenalkan/IdeaProjects/OAuth2JWT/OAuth2JWT
```

#### Adım 2: Docker Compose ile Servisleri Başlat

```bash
# Tüm servisleri arka planda başlat
docker-compose up -d

# Veya logları görmek için (arka plan olmadan)
docker-compose up
```

#### Adım 3: Servislerin Durumunu Kontrol Et

```bash
# Tüm container'ların durumunu görüntüle
docker-compose ps

# Beklenen çıktı:
# NAME                  IMAGE                    STATUS
# oauth2jwt-app         oauth2jwt-app            Up (healthy)
# oauth2jwt-postgres    postgres:15-alpine       Up (healthy)
```

#### Adım 4: Logları İzle

```bash
# Uygulama loglarını görüntüle
docker-compose logs -f app

# PostgreSQL loglarını görüntüle
docker-compose logs -f postgres

# Tüm logları görüntüle
docker-compose logs -f
```

#### Adım 5: Uygulamayı Test Et

```bash
# Health check endpoint'ini test et
curl http://localhost:8080/actuator/health

# Tarayıcıda aç
# http://localhost:8080/actuator/health
```

---

### Senaryo 2: Yerel PostgreSQL ile Kurulum

Eğer bilgisayarınızda zaten PostgreSQL kuruluysa, sadece uygulamayı Docker'da çalıştırabilirsiniz.

#### Adım 1: Yerel PostgreSQL'in Çalıştığından Emin Ol

```bash
# PostgreSQL servisinin çalıştığını kontrol et
# macOS için:
brew services list | grep postgresql

# Veya:
psql -U techpront -d oauth2jwt -c "SELECT 1;"
```

#### Adım 2: docker-compose.local.yml ile Başlat

```bash
# Sadece uygulamayı başlat (PostgreSQL host'ta çalışıyor)
docker-compose -f docker-compose.local.yml up -d
```

#### Adım 3: Logları Kontrol Et

```bash
docker-compose -f docker-compose.local.yml logs -f app
```

---

## 📖 Docker Dosyalarının Detaylı Açıklaması

### Dockerfile Analizi

```dockerfile
# Stage 1: Build Stage
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B  # Bağımlılıkları önceden indir
COPY src ./src
RUN mvn clean package -DskipTests  # Uygulamayı derle

# Stage 2: Runtime Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN apt-get update && apt-get install -y wget  # Health check için
RUN groupadd -r spring && useradd -r -g spring spring  # Non-root user
COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s CMD wget --spider http://localhost:8080/actuator/health
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Önemli Noktalar:**
- ✅ Multi-stage build sayesinde final image küçük kalır
- ✅ Non-root user ile güvenlik artırılır
- ✅ Health check ile container sağlığı izlenir
- ✅ Dependencies önceden indirilir (cache optimizasyonu)

### docker-compose.yml Analizi

```yaml
services:
  postgres:
    image: postgres:15-alpine
    container_name: oauth2jwt-postgres
    environment:
      POSTGRES_DB: oauth2jwt
      POSTGRES_USER: techpront
      POSTGRES_PASSWORD: 125322
    ports:
      - "5433:5432"  # Host:Container port mapping
    volumes:
      - postgres_data:/var/lib/postgresql/data  # Veri kalıcılığı
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U techpront"]
      interval: 10s
    networks:
      - oauth2jwt-network

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: oauth2jwt-app
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/oauth2jwt
      SPRING_DATASOURCE_USERNAME: techpront
      SPRING_DATASOURCE_PASSWORD: 125322
      JWT_SECRET: mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy  # PostgreSQL hazır olana kadar bekle
    networks:
      - oauth2jwt-network
    restart: unless-stopped

volumes:
  postgres_data:  # PostgreSQL verileri burada saklanır

networks:
  oauth2jwt-network:  # Servisler bu network'te iletişim kurar
```

**Önemli Noktalar:**
- ✅ `depends_on` ile servis bağımlılıkları yönetilir
- ✅ `healthcheck` ile servislerin hazır olması beklenir
- ✅ `volumes` ile veri kalıcılığı sağlanır
- ✅ `networks` ile servisler birbirine bağlanır

---

## 💡 Kullanım Senaryoları

### 1. Servisleri Başlatma

```bash
# Arka planda başlat
docker-compose up -d

# Loglarla birlikte başlat
docker-compose up
```

### 2. Servisleri Durdurma

```bash
# Servisleri durdur (container'lar silinir)
docker-compose down

# Volume'ları da sil (veritabanı verileri silinir)
docker-compose down -v
```

### 3. Servisleri Yeniden Başlatma

```bash
# Tüm servisleri yeniden başlat
docker-compose restart

# Sadece uygulamayı yeniden başlat
docker-compose restart app
```

### 4. Logları Görüntüleme

```bash
# Uygulama logları
docker-compose logs app

# Canlı log takibi
docker-compose logs -f app

# Son 100 satır
docker-compose logs --tail=100 app
```

### 5. Container İçine Girme

```bash
# Uygulama container'ına gir
docker-compose exec app sh

# PostgreSQL container'ına gir
docker-compose exec postgres psql -U techpront -d oauth2jwt
```

### 6. Image'ı Yeniden Build Etme

```bash
# Cache olmadan yeniden build
docker-compose build --no-cache

# Sadece uygulamayı rebuild et
docker-compose build app

# Build edip başlat
docker-compose up -d --build
```

### 7. Veritabanı Yedekleme

```bash
# PostgreSQL verilerini yedekle
docker-compose exec postgres pg_dump -U techpront oauth2jwt > backup.sql

# Yedekten geri yükle
docker-compose exec -T postgres psql -U techpront oauth2jwt < backup.sql
```

---

## 🔍 Sorun Giderme

### Problem 1: Port Zaten Kullanımda

**Hata:**
```
Error: bind: address already in use
```

**Çözüm:**
```bash
# Port'u kullanan process'i bul
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Process'i sonlandır veya docker-compose.yml'de port'u değiştir
ports:
  - "8081:8080"  # Farklı port kullan
```

### Problem 2: Container Başlamıyor

**Kontrol:**
```bash
# Container durumunu kontrol et
docker-compose ps

# Logları incele
docker-compose logs app

# Container'ı yeniden başlat
docker-compose restart app
```

### Problem 3: Veritabanı Bağlantı Hatası

**Hata:**
```
Connection refused: postgres:5432
```

**Çözüm:**
```bash
# PostgreSQL container'ının çalıştığını kontrol et
docker-compose ps postgres

# PostgreSQL loglarını kontrol et
docker-compose logs postgres

# Network bağlantısını test et
docker-compose exec app ping postgres

# PostgreSQL'in hazır olmasını bekle
docker-compose exec postgres pg_isready -U techpront
```

### Problem 4: Health Check Başarısız

**Kontrol:**
```bash
# Health check endpoint'ini manuel test et
curl http://localhost:8080/actuator/health

# Container loglarını kontrol et
docker-compose logs app

# Health check ayarlarını kontrol et
docker inspect oauth2jwt-app | grep -A 10 Healthcheck
```

### Problem 5: Volume Verileri Kayboldu

**Kontrol:**
```bash
# Volume'ları listele
docker volume ls

# Volume detaylarını görüntüle
docker volume inspect oauth2jwt_postgres_data

# Volume'u sil ve yeniden oluştur
docker-compose down -v
docker-compose up -d
```

### Problem 6: Build Hatası

**Hata:**
```
Maven build failed
```

**Çözüm:**
```bash
# Cache'i temizle ve yeniden build et
docker-compose build --no-cache app

# Maven dependencies'i kontrol et
docker-compose run app mvn dependency:tree

# Local'de test et
mvn clean package
```

---

## 📊 Kurulum Özeti

### Kullanılan Dosyalar

| Dosya | Amaç | Kullanım |
|-------|------|----------|
| `Dockerfile` | Uygulamayı containerize eder | Otomatik (docker-compose build) |
| `docker-compose.yml` | PostgreSQL + App birlikte | `docker-compose up -d` |
| `docker-compose.local.yml` | Sadece App (local DB) | `docker-compose -f docker-compose.local.yml up -d` |

### Kurulum Adımları Özeti

1. ✅ Docker ve Docker Compose kurulumu
2. ✅ Proje dizinine gitme
3. ✅ `docker-compose up -d` komutu ile servisleri başlatma
4. ✅ `docker-compose ps` ile durum kontrolü
5. ✅ `docker-compose logs -f app` ile log takibi
6. ✅ `http://localhost:8080/actuator/health` ile test

### Önemli Komutlar

```bash
# Başlat
docker-compose up -d

# Durdur
docker-compose down

# Loglar
docker-compose logs -f app

# Durum
docker-compose ps

# Yeniden başlat
docker-compose restart

# Rebuild
docker-compose up -d --build
```

---

## 🎯 Sonuç

Bu rehber ile OAuth2JWT projesini Docker ile başarıyla çalıştırabilirsiniz. Herhangi bir sorunla karşılaşırsanız, [Sorun Giderme](#sorun-giderme) bölümüne bakabilir veya logları inceleyebilirsiniz.

**Başarılı kurulum için:**
- ✅ Docker ve Docker Compose yüklü olmalı
- ✅ Port 8080 ve 5433 boş olmalı
- ✅ Yeterli disk alanı olmalı
- ✅ İnternet bağlantısı olmalı (ilk build için)

**İyi çalışmalar! 🚀**

