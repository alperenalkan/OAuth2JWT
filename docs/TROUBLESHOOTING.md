# Troubleshooting Guide - 403 Forbidden Hatası

## Problem
`POST /api/products` endpoint'ine admin token ile istek gönderildiğinde 403 Forbidden hatası alınıyor.

## Çözüm Adımları

### 1. Admin Kullanıcısı ile Login Yapın

**ÖNEMLİ:** Sadece `admin` kullanıcısı ürün oluşturabilir. `user` kullanıcısı 403 hatası alır.

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

### 2. Token'ı Doğru Kullanın

**YANLIŞ:**
```http
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9... (Bearer kelimesinden sonra boşluk yok)
Authorization: eyJhbGciOiJIUzM4NCJ9... (Bearer kelimesi eksik)
Authorization: Bearer{token} (Boşluk yok)
```

**DOĞRU:**
```http
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

**cURL Örneği:**
```bash
TOKEN="eyJhbGciOiJIUzM4NCJ9..."  # Login'den aldığınız token'ı buraya yapıştırın

curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Iphone Pro Max",
    "description": "512 GB",
    "price": 1250,
    "stock": 100
  }'
```

### 3. Token'ınızı Kontrol Edin

Token'ınızın geçerli olduğunu ve admin yetkilerine sahip olduğunu kontrol etmek için:

```bash
TOKEN="your-token-here"

curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Beklenen Response (Admin için):**
```json
{
  "username": "admin",
  "authorities": ["ROLE_ADMIN", "ROLE_USER"],
  "authenticated": true
}
```

**Eğer `ROLE_ADMIN` görmüyorsanız:**
- Admin kullanıcısı ile login yapmadınız
- Ya da admin kullanıcısı veritabanında ROLE_ADMIN'e sahip değil

### 4. Veritabanını Kontrol Edin

Admin kullanıcısının rolleri veritabanında kontrol edin:

```sql
-- PostgreSQL'de
SELECT u.username, r.role 
FROM t_users u 
JOIN t_user_roles r ON u.id = r.user_id 
WHERE u.username = 'admin';
```

**Beklenen Sonuç:**
```
username | role
---------|----------
admin    | ROLE_ADMIN
admin    | ROLE_USER
```

### 5. Postman/Insomnia Kullanıyorsanız

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "name": "Iphone Pro Max",
  "description": "512 GB",
  "price": 1250,
  "stock": 100
}
```

**ÖNEMLİ:** 
- `Authorization` header'ında `Bearer` kelimesinden sonra **mutlaka bir boşluk** olmalı
- Token'ın başında veya sonunda **boşluk olmamalı**

### 6. Hata Mesajlarını Kontrol Edin

Uygulama loglarını kontrol edin:

```bash
docker logs oauth2jwt-app | tail -50
```

Veya yerel çalıştırıyorsanız konsol çıktısını kontrol edin.

### 7. Token Süresi Dolmuş Olabilir

JWT token'ların varsayılan süresi 24 saattir. Eğer token süresi dolmuşsa, yeni bir token alın:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

## Test Script'i

Aşağıdaki script'i kullanarak tüm adımları test edebilirsiniz:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== 1. Admin Login ==="
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token: $TOKEN"
echo ""

echo "=== 2. Check Current User ==="
curl -s -X GET $BASE_URL/api/auth/me \
  -H "Authorization: Bearer $TOKEN" | jq .
echo ""

echo "=== 3. Create Product ==="
curl -v -X POST $BASE_URL/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Iphone Pro Max",
    "description": "512 GB",
    "price": 1250,
    "stock": 100
  }'
```

## Yaygın Hatalar

### Hata 1: "403 Forbidden"
**Sebep:** Admin token'ı kullanmıyorsunuz veya token geçersiz
**Çözüm:** Admin kullanıcısı ile login yapın ve token'ı doğru kullanın

### Hata 2: "401 Unauthorized"
**Sebep:** Token eksik veya geçersiz
**Çözüm:** Token'ı kontrol edin, yeni token alın

### Hata 3: "Bearer" kelimesi çalışmıyor
**Sebep:** Header formatı yanlış
**Çözüm:** `Authorization: Bearer <token>` formatını kullanın (Bearer'den sonra boşluk olmalı)

### Hata 4: Token var ama hala 403 alıyorum
**Sebep:** User kullanıcısı ile login yaptınız
**Çözüm:** Admin kullanıcısı (`admin` / `admin123`) ile login yapın

## Debug Endpoint

Mevcut kullanıcı bilgilerinizi görmek için:

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Bu endpoint size:
- Kullanıcı adınızı
- Rollerinizi (authorities)
- Authentication durumunuzu

gösterir.

## İletişim

Sorun devam ederse:
1. Uygulama loglarını kontrol edin
2. Token'ınızı `/api/auth/me` endpoint'i ile kontrol edin
3. Veritabanında admin kullanıcısının rolleri kontrol edin

