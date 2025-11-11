# 📚 OAuth2JWT Projesi - Dokümantasyon ve Rehberler

Bu klasör, OAuth2JWT projesi için tüm dokümantasyon ve rehber dosyalarını içerir.

## 📋 İçindekiler

### 🐳 Docker Rehberleri

- **[DOCKER_KURULUM_REHBERI.md](./DOCKER_KURULUM_REHBERI.md)** - Docker kurulum rehberi (detaylı)
  - Kullanılan dosyalar
  - Kurulum adımları
  - Docker dosyalarının açıklaması
  - Kullanım senaryoları
  - Sorun giderme

- **[DOCKER_GUIDE.md](./DOCKER_GUIDE.md)** - Docker kullanım kılavuzu
  - Hızlı başlangıç
  - Docker Compose kullanımı
  - Docker Image build
  - Environment variables
  - Troubleshooting

- **[DOCKER_NASIL_CALISIR.md](./DOCKER_NASIL_CALISIR.md)** - Docker nasıl her ortamda aynı çalışır?
  - Containerization konsepti
  - Dockerfile'ın rolü
  - Base image'lar
  - Container izolasyonu
  - Platform bağımsızlık
  - Pratik örnekler

- **[ARKADASINIZ_PROJEYI_NASIL_CALISTIRIR.md](./ARKADASINIZ_PROJEYI_NASIL_CALISTIRIR.md)** - Arkadaşınız projeyi nasıl çalıştırır?
  - Gereksinimler
  - Adım adım rehber
  - Docker Compose ile çalıştırma
  - Olası sorunlar ve çözümleri
  - Hızlı başlangıç komutları

### 🔄 CI/CD Rehberleri

- **[CI_CD_KURULUM_REHBERI.md](./CI_CD_KURULUM_REHBERI.md)** - CI/CD kurulum rehberi (detaylı)
  - Kullanılan dosyalar
  - GitHub tarafında yapılan ayarlar
  - Kurulum adımları
  - Workflow detayları
  - GitHub Container Registry
  - Deployment yapılandırması
  - Sorun giderme

- **[CI_CD_FAYDALARI.md](./CI_CD_FAYDALARI.md)** - CI/CD'nin faydaları
  - Otomatik test ve build
  - Otomatik Docker image
  - Kod kalitesi kontrolü
  - Hızlı geri bildirim
  - Pratik örnekler

- **[CI_CD_CHECK.md](./CI_CD_CHECK.md)** - CI/CD durumunu kontrol etme
  - GitHub Actions kontrolü
  - Workflow durumu
  - Docker image kontrolü

- **[DEPLOY_DURUMU.md](./DEPLOY_DURUMU.md)** - Deploy durumu ve otomatik deploy kurulumu
  - Mevcut durum
  - Otomatik tetikleme
  - Otomatik deploy'u aktif etme
  - SSH ile server'a deploy
  - Sorun giderme

- **[CI_CD_TEST_NASIL_CALISIR.md](./CI_CD_TEST_NASIL_CALISIR.md)** - CI/CD test nasıl çalışır ve bug'ları nasıl bulur?
  - Test çalıştırma adımları
  - Bug bulma yöntemleri
  - Test türleri (Unit, Integration, Spring Boot Test)
  - Hata yakalama senaryoları
  - Pratik örnekler

### 🗄️ Veritabanı Rehberleri

- **[DATABASE_PERSISTENCE.md](./DATABASE_PERSISTENCE.md)** - Veritabanı kalıcılığı
  - Docker volumes
  - Veri yedekleme
  - Veri geri yükleme

- **[PGADMIN_CONNECTION_GUIDE.md](./PGADMIN_CONNECTION_GUIDE.md)** - pgAdmin bağlantı rehberi
  - pgAdmin kurulumu
  - PostgreSQL bağlantısı
  - Veritabanı yönetimi

### 🛠️ Geliştirme Rehberleri

- **[MAPSTRUCT_SUPERBUILDER_GUIDE.md](./MAPSTRUCT_SUPERBUILDER_GUIDE.md)** - MapStruct ve SuperBuilder kullanımı
  - MapStruct kurulumu
  - SuperBuilder entegrasyonu
  - Örnek kullanımlar

- **[COLLECTIONTABLE_VS_JOINTABLE.md](./COLLECTIONTABLE_VS_JOINTABLE.md)** - @CollectionTable vs @JoinTable
  - Farklar
  - Kullanım senaryoları
  - Örnekler

### 🔐 Güvenlik Rehberleri

- **[OAUTH2_KULLANIMI_VE_FAYDALARI.md](./OAUTH2_KULLANIMI_VE_FAYDALARI.md)** - OAuth2/JWT kullanımı ve faydaları
  - OAuth2 dependency'leri
  - JWT authentication
  - Role-based authorization
  - Token-based security
  - Username kontrolü dışındaki faydalar

- **[OAUTH2_EKLEMEK_MANTIKLI_MI.md](./OAUTH2_EKLEMEK_MANTIKLI_MI.md)** - OAuth2 eklemek mantıklı mı? Analiz ve öneriler
  - Mevcut durum analizi
  - OAuth2 eklemek mantıklı olduğu durumlar
  - OAuth2 eklemek mantıklı olmadığı durumlar
  - Karar matrisi
  - Alternatif çözümler

- **[CORS_CROSSORIGIN_REHBERI.md](./CORS_CROSSORIGIN_REHBERI.md)** - @CrossOrigin(origins = "*") Rehberi
  - CORS nedir?
  - Neden kullanıldı?
  - Projede nerede kullanıldı?
  - Development vs Production
  - Güvenlik notları
  - CORS hataları ve çözümleri

### 🆘 Yardım ve Sorun Giderme

- **[TROUBLESHOOTING.md](./TROUBLESHOOTING.md)** - Sorun giderme rehberi
  - Yaygın problemler
  - Çözümler
  - Hata mesajları

- **[HELP.md](./HELP.md)** - Yardım dosyası
  - Hızlı referans
  - Sık sorulan sorular

### 🚀 Proje Geliştirme

- **[PROJEYE_EKLENEBILECEK_OZELLIKLER.md](./PROJEYE_EKLENEBILECEK_OZELLIKLER.md)** - Projeye eklenebilecek özellikler
  - Piyasa standartları
  - Öncelik sıralaması
  - Swagger/OpenAPI
  - Pagination & Sorting
  - Search & Filtering
  - Refresh Token
  - Unit/Integration Tests
  - Email Service
  - File Upload/Download
  - Ve daha fazlası...

- **[SWAGGER_OPENAPI_REHBERI.md](./SWAGGER_OPENAPI_REHBERI.md)** - Swagger/OpenAPI Kullanım Rehberi
  - Swagger/OpenAPI nedir?
  - Kurulum adımları
  - Swagger UI kullanımı
  - JWT token ile test
  - Controller annotation'ları
  - Pratik örnekler

---

## 🚀 Hızlı Başlangıç

### Docker ile Başlatma

```bash
# Tüm servisleri başlat (PostgreSQL + App)
docker-compose up -d

# Logları görüntüle
docker-compose logs -f app
```

Detaylı bilgi için: [DOCKER_KURULUM_REHBERI.md](./DOCKER_KURULUM_REHBERI.md)

### CI/CD Kontrolü

```bash
# GitHub Actions durumunu kontrol et
# https://github.com/alperenalkan/OAuth2JWT/actions
```

Detaylı bilgi için: [CI_CD_CHECK.md](./CI_CD_CHECK.md)

---

## 📖 Rehber Kategorileri

### Kurulum Rehberleri
- Docker kurulumu
- CI/CD kurulumu
- Veritabanı kurulumu

### Kullanım Rehberleri
- Docker kullanımı
- CI/CD kullanımı
- pgAdmin kullanımı

### Geliştirme Rehberleri
- MapStruct kullanımı
- Veritabanı yapılandırması
- Best practices

### Sorun Giderme
- Troubleshooting
- Hata çözümleri
- Yardım dosyaları

---

## 🔍 Dosya Arama

Belirli bir konu hakkında bilgi arıyorsanız:

- **Docker** → `DOCKER_*.md` dosyalarına bakın
- **CI/CD** → `CI_CD_*.md` dosyalarına bakın
- **Veritabanı** → `DATABASE_*.md` ve `PGADMIN_*.md` dosyalarına bakın
- **Sorun Giderme** → `TROUBLESHOOTING.md` ve `HELP.md` dosyalarına bakın

---

## 📝 Notlar

- Tüm rehberler Türkçe olarak hazırlanmıştır
- Rehberler proje geliştikçe güncellenmektedir
- Sorularınız için GitHub Issues kullanabilirsiniz

---

**İyi çalışmalar! 🚀**

