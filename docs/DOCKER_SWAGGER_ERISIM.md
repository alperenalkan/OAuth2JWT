# Docker Üzerinden Swagger UI Erişimi

## 🐳 Docker Container'ından Swagger UI'ya Erişim

### Durum

- ✅ Container çalışıyor: `oauth2jwt-app`
- ✅ Port mapping doğru: `0.0.0.0:8080->8080/tcp`
- ✅ Uygulama başlamış: "Started OAuth2JwtApplication"

---

## 🔍 Swagger UI URL'leri (Docker Üzerinden)

### Doğru URL'ler

**Swagger UI:**
```
http://localhost:8080/swagger-ui/index.html
```

**Alternatif:**
```
http://localhost:8080/swagger-ui.html
```

**API Dokümantasyonu (JSON):**
```
http://localhost:8080/v3/api-docs
```

---

## ✅ Kontrol Adımları

### 1. Container'ın Çalıştığını Kontrol Edin

```bash
docker ps | grep oauth2jwt-app
```

**Beklenen:**
```
oauth2jwt-app   Up   ...   0.0.0.0:8080->8080/tcp
```

---

### 2. Port Mapping'i Kontrol Edin

```bash
docker port oauth2jwt-app
```

**Beklenen:**
```
8080/tcp -> 0.0.0.0:8080
```

---

### 3. Container Loglarını Kontrol Edin

```bash
docker logs oauth2jwt-app --tail 50
```

**Kontrol Edin:**
- "Started OAuth2JwtApplication" mesajı var mı?
- Hata var mı?

---

### 4. Swagger UI'ya Erişimi Test Edin

**Tarayıcıda:**
```
http://localhost:8080/swagger-ui/index.html
```

**Terminal'de:**
```bash
curl http://localhost:8080/swagger-ui/index.html
curl http://localhost:8080/v3/api-docs
```

---

## 🔧 Sorun Giderme

### Sorun 1: localhost:8080 Çalışmıyor

**Kontrol:**
```bash
# Container çalışıyor mu?
docker ps | grep oauth2jwt-app

# Port mapping doğru mu?
docker port oauth2jwt-app

# Container loglarında hata var mı?
docker logs oauth2jwt-app --tail 50
```

**Çözüm:**
- Container'ı yeniden başlatın: `docker-compose restart app`
- Port'u kontrol edin: `docker-compose.yml`'de `8080:8080` olmalı

---

### Sorun 2: 403 Forbidden

**Neden:**
- SecurityConfig'de Swagger path'leri permitAll yapılmamış
- Container içinde SecurityConfig yüklenmemiş

**Çözüm:**
1. Container'ı yeniden build edin:
```bash
docker-compose down
docker-compose up -d --build
```

2. SecurityConfig'i kontrol edin:
```java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
```

---

### Sorun 3: 404 Not Found

**Neden:**
- Yanlış URL
- Swagger dependency eksik
- SwaggerConfig yüklenmemiş

**Çözüm:**
1. Doğru URL'i kullanın: `http://localhost:8080/swagger-ui/index.html`
2. Container'ı yeniden build edin
3. Logları kontrol edin

---

## 🎯 Hızlı Test

### Test 1: Container Durumu

```bash
docker-compose ps
```

**Beklenen:**
```
NAME                 STATUS
oauth2jwt-app        Up (healthy)
oauth2jwt-postgres   Up (healthy)
```

---

### Test 2: Port Erişimi

```bash
curl http://localhost:8080/actuator/health
```

**Beklenen:**
```json
{"status":"UP"}
```

---

### Test 3: Swagger UI

**Tarayıcıda:**
```
http://localhost:8080/swagger-ui/index.html
```

**Beklenen:**
- Swagger UI sayfası açılır
- Tüm endpoint'ler görünür

---

## 📝 Önemli Notlar

### Docker Container İçinde

- Container içinde uygulama `localhost:8080`'de çalışır
- Host'tan erişmek için port mapping gerekir: `8080:8080`
- Swagger UI path'leri container içinde de aynıdır

### Port Mapping

**docker-compose.yml:**
```yaml
ports:
  - "8080:8080"  # Host:Container
```

**Anlamı:**
- Host'tan `localhost:8080` → Container içinde `localhost:8080`

---

## ✅ Sonuç

**Docker üzerinden Swagger UI'ya erişim:**

1. Container'ın çalıştığını kontrol edin
2. Port mapping'in doğru olduğunu kontrol edin
3. Doğru URL'i kullanın: `http://localhost:8080/swagger-ui/index.html`

**Eğer hala çalışmıyorsa:**
1. Container'ı yeniden build edin: `docker-compose up -d --build`
2. Logları kontrol edin: `docker logs oauth2jwt-app`
3. Port'u kontrol edin: `docker port oauth2jwt-app`

