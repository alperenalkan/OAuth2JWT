# Arkadaşınız Projeyi Nasıl Çalıştırır? - Adım Adım Rehber

## ✅ Kısa Cevap: EVET, Sorunsuz Çalışır!

Arkadaşınızın bilgisayarında **Docker kurulu** olduğu sürece, projeyi GitHub'dan indirip **sorunsuz çalıştırabilir**.

---

## 🎯 Gereksinimler

### Arkadaşınızın Bilgisayarında Olması Gerekenler

1. ✅ **Docker Desktop** (veya Docker Engine)
   - Mac için: Docker Desktop for Mac
   - Windows için: Docker Desktop for Windows
   - Linux için: Docker Engine

2. ✅ **Git** (opsiyonel, GitHub'dan indirmek için)
   - Veya GitHub'dan ZIP olarak indirebilir

3. ✅ **İnternet Bağlantısı** (ilk build için)
   - Base image'ları indirmek için

**Bu kadar!** Başka bir şey gerekmez.

---

## 🚀 Adım Adım: Arkadaşınızın Yapması Gerekenler

### Senaryo: Arkadaşınız Windows Kullanıyor (veya Mac/Linux)

#### Adım 1: Docker'ın Kurulu Olduğunu Kontrol Etme

**Windows'ta:**
```bash
docker --version
# Çıktı: Docker version 24.0.0, build ...
```

**Mac'te:**
```bash
docker --version
# Çıktı: Docker version 24.0.0, build ...
```

**Linux'ta:**
```bash
docker --version
# Çıktı: Docker version 24.0.0, build ...
```

**Eğer Docker yoksa:**
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) indirip kurmalı
- Kurulum sonrası bilgisayarı yeniden başlatmalı

---

#### Adım 2: Projeyi GitHub'dan İndirme

**Yöntem 1: Git ile (Önerilen)**
```bash
git clone https://github.com/alperenalkan/OAuth2JWT.git
cd OAuth2JWT
```

**Yöntem 2: ZIP Olarak İndirme**
1. GitHub'da "Code" butonuna tıkla
2. "Download ZIP" seçeneğini seç
3. ZIP'i aç
4. Klasöre git

---

#### Adım 3: Proje Klasörüne Gitme

```bash
cd OAuth2JWT
```

**Kontrol:**
```bash
ls
# veya Windows'ta:
dir
```

**Görmesi Gerekenler:**
```
Dockerfile
docker-compose.yml
pom.xml
src/
...
```

---

#### Adım 4: Docker Compose ile Çalıştırma

**En Kolay Yöntem:**
```bash
docker-compose up -d
```

**Ne Yapar?**
1. PostgreSQL container'ı başlatır
2. Uygulama container'ı build eder
3. Her ikisini de çalıştırır
4. Arka planda çalışır

**Bekleme Süresi:**
- İlk sefer: 5-10 dakika (image'ları indirmek için)
- Sonraki seferler: 1-2 dakika

---

#### Adım 5: Uygulamanın Çalıştığını Kontrol Etme

**Container'ların Durumunu Kontrol:**
```bash
docker-compose ps
```

**Beklenen Çıktı:**
```
NAME                  STATUS
oauth2jwt-app         Up (healthy)
oauth2jwt-postgres    Up (healthy)
```

**Uygulamayı Test Et:**
```bash
curl http://localhost:8080/actuator/health
```

**Veya Tarayıcıda:**
```
http://localhost:8080/actuator/health
```

**Beklenen Çıktı:**
```json
{
  "status": "UP"
}
```

---

## ✅ Sonuç: Sorunsuz Çalışır!

### Neden Sorunsuz Çalışır?

1. ✅ **Aynı Dockerfile**
   - GitHub'da aynı Dockerfile var
   - Herkes aynı Dockerfile'ı kullanır

2. ✅ **Aynı Base Image**
   - Docker Hub'dan aynı image'lar indirilir
   - Herkes aynı ortamı kullanır

3. ✅ **Container İzolasyonu**
   - Container, host OS'den bağımsız
   - Mac, Windows, Linux - hepsi aynı sonucu verir

4. ✅ **Docker Compose**
   - Aynı docker-compose.yml dosyası
   - Aynı yapılandırma
   - Aynı sonuç

---

## 🎯 Pratik Örnek: Arkadaşınızın Deneyimi

### Senaryo: Arkadaşınız Windows Kullanıyor

**Adım 1: Docker Kontrolü**
```bash
docker --version
# ✅ Docker version 24.0.0
```

**Adım 2: Projeyi İndirme**
```bash
git clone https://github.com/alperenalkan/OAuth2JWT.git
cd OAuth2JWT
```

**Adım 3: Docker Compose ile Çalıştırma**
```bash
docker-compose up -d
```

**Ne Olur?**
```
[+] Running 2/2
 ✔ Container oauth2jwt-postgres    Started
 ✔ Container oauth2jwt-app          Started
```

**Adım 4: Kontrol**
```bash
docker-compose ps
```

**Görür:**
```
NAME                  STATUS
oauth2jwt-app         Up (healthy)
oauth2jwt-postgres    Up (healthy)
```

**Adım 5: Test**
```
http://localhost:8080/actuator/health
```

**Sonuç:** ✅ **Çalışıyor!**

---

## 🔍 Olası Sorunlar ve Çözümleri

### Sorun 1: Docker Kurulu Değil

**Hata:**
```
docker: command not found
```

**Çözüm:**
1. [Docker Desktop](https://www.docker.com/products/docker-desktop/) indir
2. Kur
3. Bilgisayarı yeniden başlat
4. Tekrar dene

---

### Sorun 2: Port Zaten Kullanımda

**Hata:**
```
Error: bind: address already in use
```

**Çözüm:**
```bash
# Port'u kullanan process'i bul
# Windows'ta:
netstat -ano | findstr :8080

# Mac/Linux'ta:
lsof -i :8080

# Process'i sonlandır veya docker-compose.yml'de port'u değiştir
```

---

### Sorun 3: Docker Desktop Çalışmıyor

**Hata:**
```
Cannot connect to the Docker daemon
```

**Çözüm:**
1. Docker Desktop'ı aç
2. "Start" butonuna tıkla
3. Docker'ın başlamasını bekle
4. Tekrar dene

---

### Sorun 4: İnternet Bağlantısı Yok

**Hata:**
```
Error pulling image
```

**Çözüm:**
- İnternet bağlantısını kontrol et
- İlk build için internet gerekli (base image'ları indirmek için)
- Sonraki build'lerde cache kullanılır

---

## 💡 Alternatif Yöntemler

### Yöntem 1: Sadece Dockerfile ile (docker-compose olmadan)

**Eğer docker-compose.yml yoksa:**

```bash
# 1. PostgreSQL'i başlat
docker run -d --name postgres \
  -e POSTGRES_DB=oauth2jwt \
  -e POSTGRES_USER=techpront \
  -e POSTGRES_PASSWORD=125322 \
  -p 5433:5432 \
  postgres:15-alpine

# 2. Uygulamayı build et
docker build -t oauth2jwt .

# 3. Uygulamayı çalıştır
docker run -d --name oauth2jwt-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/oauth2jwt \
  -e SPRING_DATASOURCE_USERNAME=techpront \
  -e SPRING_DATASOURCE_PASSWORD=125322 \
  oauth2jwt
```

---

### Yöntem 2: GitHub Container Registry'den Image Çekme

**Eğer image GitHub Container Registry'de varsa:**

```bash
# Image'ı çek
docker pull ghcr.io/alperenalkan/oauth2jwt:latest

# Çalıştır
docker run -p 8080:8080 ghcr.io/alperenalkan/oauth2jwt:latest
```

---

## 🎯 Özet: Arkadaşınızın Yapması Gerekenler

### Minimum Gereksinimler

1. ✅ **Docker Desktop** kurulu olmalı
2. ✅ **İnternet bağlantısı** olmalı (ilk build için)

### Adımlar

1. ✅ Projeyi GitHub'dan indir
2. ✅ Proje klasörüne git
3. ✅ `docker-compose up -d` komutunu çalıştır
4. ✅ Bekle (ilk sefer 5-10 dakika)
5. ✅ `http://localhost:8080/actuator/health` ile test et

### Sonuç

✅ **Sorunsuz çalışır!**

**Neden?**
- Aynı Dockerfile
- Aynı docker-compose.yml
- Aynı base image'lar
- Container izolasyonu

---

## 🚀 Hızlı Başlangıç Komutları

### Arkadaşınız İçin Hızlı Rehber

```bash
# 1. Docker'ın kurulu olduğunu kontrol et
docker --version

# 2. Projeyi indir
git clone https://github.com/alperenalkan/OAuth2JWT.git
cd OAuth2JWT

# 3. Çalıştır
docker-compose up -d

# 4. Durumu kontrol et
docker-compose ps

# 5. Logları görüntüle
docker-compose logs -f app

# 6. Test et
curl http://localhost:8080/actuator/health

# 7. Durdur
docker-compose down
```

---

## ✅ Sonuç

### Arkadaşınız Projeyi Çalıştırabilir mi?

**EVET!** ✅

**Gereksinimler:**
- ✅ Docker Desktop kurulu
- ✅ İnternet bağlantısı (ilk build için)

**Adımlar:**
1. GitHub'dan indir
2. `docker-compose up -d` çalıştır
3. Bekle
4. Test et

**Sonuç:**
- ✅ Mac'te çalışır
- ✅ Windows'ta çalışır
- ✅ Linux'ta çalışır

**Hepsi aynı sonucu alır çünkü:**
- ✅ Aynı Dockerfile
- ✅ Aynı docker-compose.yml
- ✅ Aynı base image'lar
- ✅ Container izolasyonu

**"It Works on My Machine" Problemi Çözülür!** 🚀

