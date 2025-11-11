# Swagger UI Erişim Sorunları ve Çözümleri

## 🔍 Sorun: Swagger UI'ya Erişilemiyor (403 veya 404)

### Olası Nedenler ve Çözümler

#### 1. Uygulama Yeniden Başlatılmadı

**Sorun:**
- SecurityConfig değişiklikleri uygulanmadı
- SwaggerConfig yüklenmedi

**Çözüm:**
```bash
# Uygulamayı yeniden başlatın
# IDE'de: Stop ve Run
# Veya terminal'de:
mvn spring-boot:run
```

---

#### 2. Yanlış URL

**Spring Boot 3.x ve springdoc-openapi 2.x için doğru URL'ler:**

**Deneyin:**
1. `http://localhost:8080/swagger-ui/index.html` ✅
2. `http://localhost:8080/swagger-ui.html` ✅
3. `http://localhost:8080/swagger-ui/` ✅

**API Dokümantasyonu:**
- `http://localhost:8080/v3/api-docs` ✅

---

#### 3. SecurityConfig Path Pattern'leri

**Kontrol Edin:**
```java
.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
```

**Önemli:** Path pattern'leri permitAll yapılmalı ve `anyRequest().authenticated()`'dan **önce** olmalı.

---

#### 4. Port Kontrolü

**Kontrol:**
```bash
# Uygulamanın çalıştığını kontrol edin
curl http://localhost:8080/actuator/health

# Veya
curl http://localhost:8080/api/auth/login
```

**Eğer farklı port kullanıyorsanız:**
- `application.properties`'te `server.port` kontrol edin
- URL'de doğru port'u kullanın

---

## ✅ Doğru Swagger UI URL'leri

### Spring Boot 3.x için:

1. **Swagger UI:**
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

2. **Swagger UI (Alternatif):**
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **API Dokümantasyonu (JSON):**
   ```
   http://localhost:8080/v3/api-docs
   ```

---

## 🔧 Sorun Giderme Adımları

### Adım 1: Uygulamanın Çalıştığını Kontrol Edin

```bash
curl http://localhost:8080/actuator/health
```

**Beklenen:**
```json
{"status":"UP"}
```

---

### Adım 2: Swagger UI URL'lerini Deneyin

**Tarayıcıda:**
1. `http://localhost:8080/swagger-ui/index.html`
2. `http://localhost:8080/swagger-ui.html`
3. `http://localhost:8080/swagger-ui/`

**Terminal'de:**
```bash
curl http://localhost:8080/swagger-ui/index.html
curl http://localhost:8080/v3/api-docs
```

---

### Adım 3: SecurityConfig'i Kontrol Edin

**SecurityConfig.java:**
```java
.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
```

**Önemli:**
- Path pattern'leri permitAll yapılmalı
- `anyRequest().authenticated()`'dan **önce** olmalı

---

### Adım 4: Application Properties'i Kontrol Edin

**application.properties:**
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

---

### Adım 5: Uygulamayı Yeniden Başlatın

**IDE'de:**
1. Uygulamayı durdurun
2. Uygulamayı yeniden başlatın

**Terminal'de:**
```bash
# Ctrl+C ile durdurun
# Sonra:
mvn spring-boot:run
```

---

## 🎯 Hızlı Test

### Test 1: API Dokümantasyonu

```bash
curl http://localhost:8080/v3/api-docs
```

**Beklenen:** JSON response (OpenAPI spec)

---

### Test 2: Swagger UI

**Tarayıcıda:**
```
http://localhost:8080/swagger-ui/index.html
```

**Beklenen:** Swagger UI sayfası

---

## 🔍 Yaygın Hatalar

### Hata 1: 403 Forbidden

**Neden:**
- SecurityConfig'de Swagger path'leri permitAll yapılmamış
- Path pattern'leri yanlış

**Çözüm:**
```java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
```

---

### Hata 2: 404 Not Found

**Neden:**
- Yanlış URL
- Swagger dependency eksik
- Uygulama çalışmıyor

**Çözüm:**
1. Doğru URL'i kullanın: `http://localhost:8080/swagger-ui/index.html`
2. pom.xml'de dependency kontrol edin
3. Uygulamanın çalıştığını kontrol edin

---

### Hata 3: Blank Page

**Neden:**
- SwaggerConfig yüklenmedi
- Dependency eksik

**Çözüm:**
1. Uygulamayı yeniden başlatın
2. pom.xml'de dependency kontrol edin
3. SwaggerConfig.java'nın doğru olduğunu kontrol edin

---

## ✅ Doğru Yapılandırma Kontrol Listesi

- [ ] pom.xml'de `springdoc-openapi-starter-webmvc-ui` dependency var
- [ ] SwaggerConfig.java oluşturuldu
- [ ] SecurityConfig.java'da Swagger path'leri permitAll yapıldı
- [ ] application.properties'te Swagger ayarları var
- [ ] Uygulama yeniden başlatıldı
- [ ] Doğru URL kullanılıyor: `http://localhost:8080/swagger-ui/index.html`

---

## 🚀 Sonuç

**Doğru URL:**
```
http://localhost:8080/swagger-ui/index.html
```

**Eğer hala çalışmıyorsa:**
1. Uygulamayı yeniden başlatın
2. Logları kontrol edin
3. Port'u kontrol edin
4. SecurityConfig'i kontrol edin

