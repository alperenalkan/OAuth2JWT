# CI/CD Durumunu Kontrol Etme

## 🚀 GitHub Actions Kontrolü

### 1. Web Arayüzünden (En Kolay)

1. **GitHub Repository'ye gidin:**
   ```
   https://github.com/alperenalkan/OAuth2JWT
   ```

2. **Actions sekmesine tıklayın:**
   - Repository sayfasının üst kısmında "Actions" sekmesine tıklayın
   - Veya direkt: `https://github.com/alperenalkan/OAuth2JWT/actions`

3. **Workflow'u kontrol edin:**
   - Sol tarafta "CI/CD Pipeline" workflow'unu göreceksiniz
   - En üstteki run (en son push) durumunu gösterir:
     - 🟡 **Sarı (Yellow)**: Çalışıyor (In Progress)
     - 🟢 **Yeşil (Green)**: Başarılı (Success) ✅
     - 🔴 **Kırmızı (Red)**: Başarısız (Failed) ❌

4. **Detayları görüntüleyin:**
   - Run'a tıklayarak her adımın detaylarını görebilirsiniz
   - Logları inceleyebilirsiniz
   - Hata varsa nerede olduğunu görebilirsiniz

### 2. Commit Sayfasından

1. **Repository ana sayfasına gidin:**
   ```
   https://github.com/alperenalkan/OAuth2JWT
   ```

2. **Son commit'e tıklayın:**
   - Ana sayfada son commit'i göreceksiniz
   - Commit'in yanında küçük bir durum ikonu olacak:
     - ✅ Yeşil tik: CI/CD başarılı
     - ❌ Kırmızı X: CI/CD başarısız
     - 🟡 Sarı nokta: CI/CD çalışıyor

### 3. Terminal'den (GitHub CLI ile)

Eğer GitHub CLI yüklüyse:

```bash
# GitHub CLI'yı yükle (macOS)
brew install gh

# GitHub'a login ol
gh auth login

# Workflow run'larını listele
gh run list --repo alperenalkan/OAuth2JWT

# Son workflow run'unu görüntüle
gh run view --repo alperenalkan/OAuth2JWT

# Workflow loglarını görüntüle
gh run watch --repo alperenalkan/OAuth2JWT
```

### 4. E-posta Bildirimleri (Opsiyonel)

GitHub Actions varsayılan olarak workflow başarısız olduğunda e-posta gönderir. Ayarlardan bildirim tercihlerinizi değiştirebilirsiniz.

## 📊 CI/CD Pipeline Adımları

Pipeline şu adımları içerir:

1. ✅ **Checkout code**: Kod repository'den çekilir
2. ✅ **Set up JDK 17**: Java 17 kurulumu
3. ✅ **Start PostgreSQL**: Test için PostgreSQL container'ı
4. ✅ **Run tests**: Maven test komutu
5. ✅ **Build application**: Maven package
6. ✅ **Build Docker image**: Docker image oluşturulur
7. ✅ **Push to GitHub Container Registry**: Image `ghcr.io`'ya gönderilir

## 🔍 Sorun Giderme

### CI/CD Çalışmıyorsa:

1. **Workflow dosyasını kontrol edin:**
   - `.github/workflows/ci-cd.yml` dosyasının doğru olduğundan emin olun

2. **Branch adını kontrol edin:**
   - Workflow sadece `main`, `master`, `develop` branch'lerinde çalışır
   - Push ettiğiniz branch'i kontrol edin: `git branch`

3. **GitHub Actions'ın aktif olduğundan emin olun:**
   - Repository Settings → Actions → General
   - "Allow all actions and reusable workflows" seçeneğinin aktif olduğundan emin olun

### CI/CD Başarısız Oluyorsa:

1. **Logları kontrol edin:**
   - Actions sekmesinde başarısız run'a tıklayın
   - Hangi adımda başarısız olduğunu görün
   - Logları inceleyin

2. **Yaygın hatalar:**
   - Test hataları: Testler başarısız olabilir
   - Build hataları: Kod derleme hatası olabilir
   - Docker build hataları: Dockerfile'da sorun olabilir

## 📝 Hızlı Kontrol

En hızlı yöntem:

1. Tarayıcınızda şu adrese gidin:
   ```
   https://github.com/alperenalkan/OAuth2JWT/actions
   ```

2. En üstteki workflow run'unun durumuna bakın:
   - 🟢 Yeşil = Başarılı
   - 🔴 Kırmızı = Başarısız
   - 🟡 Sarı = Çalışıyor

## 🎯 Docker Image Kontrolü

CI/CD başarılı olduktan sonra Docker image'ı kontrol edin:

```bash
# GitHub Container Registry'den image'ı çek
docker pull ghcr.io/alperenalkan/oauth2jwt:latest

# Image'ı çalıştır
docker run -p 8080:8080 ghcr.io/alperenalkan/oauth2jwt:latest
```

Veya GitHub Container Registry'de görüntüleyin:
```
https://github.com/alperenalkan/OAuth2JWT/pkgs/container/oauth2jwt
```

