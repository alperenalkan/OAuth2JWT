# CI/CD Kurulum Rehberi - OAuth2JWT Projesi

Bu rehber, OAuth2JWT projesine GitHub Actions ile CI/CD pipeline'ının nasıl kurulduğunu ve yapılandırıldığını detaylı olarak açıklar.

## 📋 İçindekiler

1. [Genel Bakış](#genel-bakış)
2. [Kullanılan Dosyalar](#kullanılan-dosyalar)
3. [GitHub Tarafında Yapılan Ayarlar](#github-tarafında-yapılan-ayarlar)
4. [Kurulum Adımları](#kurulum-adımları)
5. [Workflow Detayları](#workflow-detayları)
6. [GitHub Container Registry](#github-container-registry)
7. [Deployment Yapılandırması](#deployment-yapılandırması)
8. [Sorun Giderme](#sorun-giderme)

---

## 🎯 Genel Bakış

Bu projede **GitHub Actions** kullanılarak otomatik CI/CD pipeline'ı kurulmuştur. Pipeline şu işlemleri yapar:

1. ✅ **Kod Kontrolü**: Repository'den kodu çeker
2. ✅ **Test Çalıştırma**: Maven ile unit testleri çalıştırır
3. ✅ **Build İşlemi**: Uygulamayı derler ve JAR dosyası oluşturur
4. ✅ **Docker Image Build**: Docker image'ı oluşturur
5. ✅ **Container Registry Push**: Image'ı GitHub Container Registry'ye gönderir
6. ✅ **Deployment**: (Opsiyonel) Production ortamına deploy eder

---

## 📁 Kullanılan Dosyalar

### 1. **`.github/workflows/ci-cd.yml`**
- **Konum**: `.github/workflows/ci-cd.yml`
- **Amaç**: GitHub Actions workflow tanımı
- **İçerik**:
  - Workflow trigger'ları (push, pull_request)
  - Build and Test job'ı
  - Docker Image Build job'ı
  - Deploy job'ı (opsiyonel)

### 2. **`Dockerfile`**
- **Konum**: Proje kök dizini (`/Dockerfile`)
- **Amaç**: Docker image oluşturmak için kullanılır
- **CI/CD'de Kullanımı**: Docker build işlemi bu dosyayı kullanır

### 3. **`pom.xml`**
- **Konum**: Proje kök dizini (`/pom.xml`)
- **Amaç**: Maven bağımlılıklarını ve build yapılandırmasını içerir
- **CI/CD'de Kullanımı**: Maven test ve build işlemleri için

### 4. **`.github/workflows/README.md`**
- **Konum**: `.github/workflows/README.md`
- **Amaç**: Workflow dosyalarının açıklaması

---

## ⚙️ GitHub Tarafında Yapılan Ayarlar

### 1. GitHub Actions'ı Aktif Etme

GitHub Actions varsayılan olarak aktif gelir, ancak kontrol etmek için:

1. **Repository Settings** → **Actions** → **General**
2. **Workflow permissions** bölümünde:
   - ✅ "Read and write permissions" seçeneğini seçin (Container Registry push için gerekli)
   - ✅ "Allow GitHub Actions to create and approve pull requests" (opsiyonel)

### 2. GitHub Container Registry (ghcr.io) Ayarları

GitHub Container Registry otomatik olarak kullanılabilir. Özel bir ayar gerekmez, ancak:

1. **Repository Settings** → **Actions** → **General**
2. **Workflow permissions** bölümünde:
   - ✅ "Read and write permissions" aktif olmalı

### 3. Secrets Yapılandırması (Deployment için)

Eğer deployment yapılandırmak isterseniz, aşağıdaki secrets'ları ekleyin:

1. **Repository Settings** → **Secrets and variables** → **Actions**
2. **New repository secret** butonuna tıklayın
3. Aşağıdaki secrets'ları ekleyin:

| Secret Adı | Açıklama | Örnek Değer |
|------------|----------|-------------|
| `HOST` | Deployment server hostname/IP | `192.168.1.100` veya `example.com` |
| `USERNAME` | SSH kullanıcı adı | `deploy` |
| `SSH_KEY` | SSH private key | `-----BEGIN OPENSSH PRIVATE KEY-----...` |

**Not**: Deployment şu anda yorum satırında, aktif değil. Aktif etmek için workflow dosyasındaki ilgili bölümü yorumdan çıkarın.

### 4. Environment Variables (Opsiyonel)

Production deployment için environment variables ekleyebilirsiniz:

1. **Repository Settings** → **Environments**
2. **New environment** → `production` oluşturun
3. Environment variables ekleyin (gerekirse)

---

## 🚀 Kurulum Adımları

### Adım 1: Workflow Dosyasını Oluşturma

`.github/workflows/ci-cd.yml` dosyasını oluşturun:

```bash
# Proje kök dizininde
mkdir -p .github/workflows
touch .github/workflows/ci-cd.yml
```

### Adım 2: Workflow İçeriğini Ekleme

`ci-cd.yml` dosyasına aşağıdaki içeriği ekleyin (zaten mevcut):

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, master, develop ]
  pull_request:
    branches: [ main, master, develop ]

env:
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  build-and-test:
    # ... (detaylar aşağıda)
```

### Adım 3: GitHub'a Push Etme

Workflow dosyasını GitHub'a push edin:

```bash
git add .github/workflows/ci-cd.yml
git commit -m "Add CI/CD pipeline"
git push origin main
```

### Adım 4: Workflow'un Çalıştığını Kontrol Etme

1. GitHub repository'nize gidin
2. **Actions** sekmesine tıklayın
3. **CI/CD Pipeline** workflow'unu göreceksiniz
4. İlk run otomatik olarak başlayacaktır

---

## 🔧 Workflow Detayları

### Workflow Trigger'ları

Workflow şu durumlarda otomatik çalışır:

```yaml
on:
  push:
    branches: [ main, master, develop ]  # Bu branch'lere push
  pull_request:
    branches: [ main, master, develop ]  # Bu branch'lere PR
```

**Manuel Tetikleme:**
- GitHub Actions UI'dan "Run workflow" butonu ile

### Job 1: Build and Test

Bu job şu adımları içerir:

#### 1.1. Checkout Code
```yaml
- name: Checkout code
  uses: actions/checkout@v4
```
- Repository'den kodu çeker

#### 1.2. Set up JDK 17
```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v4
  with:
    java-version: '17'
    distribution: 'temurin'
    cache: maven
```
- Java 17 (Temurin) kurulumu
- Maven cache optimizasyonu

#### 1.3. PostgreSQL Service
```yaml
services:
  postgres:
    image: postgres:15-alpine
    env:
      POSTGRES_DB: oauth2jwt_test
      POSTGRES_USER: techpront
      POSTGRES_PASSWORD: 125322
    ports:
      - 5432:5432
    options: >-
      --health-cmd pg_isready
      --health-interval 10s
      --health-timeout 5s
      --health-retries 5
```
- Test için PostgreSQL container'ı başlatılır
- Health check ile hazır olması beklenir

#### 1.4. Run Tests
```yaml
- name: Run tests
  run: mvn clean test
  env:
    SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/oauth2jwt_test
    SPRING_DATASOURCE_USERNAME: techpront
    SPRING_DATASOURCE_PASSWORD: 125322
```
- Maven ile testler çalıştırılır
- PostgreSQL service'e bağlanır

#### 1.5. Build Application
```yaml
- name: Build application
  run: mvn clean package -DskipTests
```
- Uygulama derlenir (testler zaten çalıştırıldı)
- JAR dosyası oluşturulur

#### 1.6. Upload JAR Artifact
```yaml
- name: Upload JAR artifact
  uses: actions/upload-artifact@v4
  with:
    name: application-jar
    path: target/*.jar
    retention-days: 7
```
- JAR dosyası artifact olarak kaydedilir
- 7 gün boyunca saklanır

### Job 2: Build Docker Image

Bu job sadece `main` veya `master` branch'e push edildiğinde çalışır:

```yaml
if: github.event_name == 'push' && (github.ref == 'refs/heads/main' || github.ref == 'refs/heads/master')
```

#### 2.1. Checkout Code
```yaml
- name: Checkout code
  uses: actions/checkout@v4
```

#### 2.2. Set up Docker Buildx
```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3
```
- Docker Buildx kurulumu (multi-arch build için)

#### 2.3. Log in to Container Registry
```yaml
- name: Log in to Container Registry
  uses: docker/login-action@v3
  with:
    registry: ${{ env.REGISTRY }}
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}
```
- GitHub Container Registry'ye giriş yapar
- `GITHUB_TOKEN` otomatik olarak sağlanır

#### 2.4. Extract Metadata
```yaml
- name: Extract metadata
  id: meta
  uses: docker/metadata-action@v5
  with:
    images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
    tags: |
      type=ref,event=branch
      type=ref,event=pr
      type=semver,pattern={{version}}
      type=semver,pattern={{major}}.{{minor}}
      type=sha,prefix={{branch}}-
      type=raw,value=latest,enable={{is_default_branch}}
```
- Image tag'lerini otomatik oluşturur
- Örnek tag'ler:
  - `latest` (main branch için)
  - `main-abc1234` (SHA ile)
  - `v1.0.0` (semver ile)

#### 2.5. Build and Push Docker Image
```yaml
- name: Build and push Docker image
  uses: docker/build-push-action@v5
  with:
    context: .
    push: true
    tags: ${{ steps.meta.outputs.tags }}
    labels: ${{ steps.meta.outputs.labels }}
    cache-from: type=gha
    cache-to: type=gha,mode=max
```
- Dockerfile kullanılarak image build edilir
- GitHub Actions cache kullanılır (hızlandırma)
- Image GitHub Container Registry'ye push edilir

### Job 3: Deploy (Opsiyonel)

Bu job şu anda yorum satırında. Aktif etmek için:

```yaml
- name: Deploy to server
  run: |
    echo "Deployment steps would go here"
    # SSH ile deployment yapılabilir
```

**SSH ile Deployment Örneği:**
```yaml
- name: Deploy via SSH
  uses: appleboy/ssh-action@v1
  with:
    host: ${{ secrets.HOST }}
    username: ${{ secrets.USERNAME }}
    key: ${{ secrets.SSH_KEY }}
    script: |
      cd /path/to/app
      docker-compose pull
      docker-compose up -d
```

---

## 📦 GitHub Container Registry

### Image Adresi

Image'lar şu formatta GitHub Container Registry'de saklanır:

```
ghcr.io/<username>/<repository>:<tag>
```

**Örnek:**
```
ghcr.io/alperenalkan/oauth2jwt:latest
ghcr.io/alperenalkan/oauth2jwt:main-abc1234
```

### Image'ı Görüntüleme

1. **GitHub Repository** → **Packages** sekmesi
2. Veya direkt URL:
   ```
   https://github.com/alperenalkan/OAuth2JWT/pkgs/container/oauth2jwt
   ```

### Image'ı Çekme ve Kullanma

```bash
# Latest image'i çek
docker pull ghcr.io/alperenalkan/oauth2jwt:latest

# Image'ı çalıştır
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/oauth2jwt \
  -e SPRING_DATASOURCE_USERNAME=techpront \
  -e SPRING_DATASOURCE_PASSWORD=125322 \
  ghcr.io/alperenalkan/oauth2jwt:latest
```

### Image İzinleri

Varsayılan olarak image'lar **private**'dır. Public yapmak için:

1. **Packages** sekmesine gidin
2. Image'a tıklayın
3. **Package settings** → **Change visibility** → **Public**

---

## 🚢 Deployment Yapılandırması

### Senaryo 1: Docker Compose ile Deployment

Production server'da:

```bash
# docker-compose.yml dosyasını güncelleyin
services:
  app:
    image: ghcr.io/alperenalkan/oauth2jwt:latest
    # ... diğer ayarlar
```

Workflow'da deployment:

```yaml
- name: Deploy via SSH
  uses: appleboy/ssh-action@v1
  with:
    host: ${{ secrets.HOST }}
    username: ${{ secrets.USERNAME }}
    key: ${{ secrets.SSH_KEY }}
    script: |
      cd /path/to/app
      docker-compose pull
      docker-compose up -d
```

### Senaryo 2: Kubernetes Deployment

Kubernetes için:

```yaml
- name: Deploy to Kubernetes
  uses: azure/k8s-deploy@v4
  with:
    manifests: |
      k8s/deployment.yaml
      k8s/service.yaml
    images: |
      ghcr.io/alperenalkan/oauth2jwt:${{ github.sha }}
```

### Senaryo 3: Cloud Platform Deployment

#### AWS ECS
```yaml
- name: Deploy to ECS
  uses: aws-actions/amazon-ecs-deploy-task-definition@v1
  with:
    task-definition: task-definition.json
    service: oauth2jwt-service
    cluster: oauth2jwt-cluster
```

#### Google Cloud Run
```yaml
- name: Deploy to Cloud Run
  uses: google-github-actions/deploy-cloudrun@v1
  with:
    service: oauth2jwt
    image: ghcr.io/alperenalkan/oauth2jwt:latest
```

---

## 🔍 Sorun Giderme

### Problem 1: Workflow Çalışmıyor

**Kontrol:**
```bash
# Workflow dosyasının doğru konumda olduğundan emin olun
ls -la .github/workflows/ci-cd.yml

# GitHub Actions'ın aktif olduğundan emin olun
# Repository Settings → Actions → General
```

**Çözüm:**
- Workflow dosyasının syntax'ını kontrol edin
- Branch adının doğru olduğundan emin olun (`main`, `master`, `develop`)

### Problem 2: Test Başarısız

**Hata:**
```
Tests failed
```

**Kontrol:**
```bash
# Local'de testleri çalıştırın
mvn clean test

# PostgreSQL'in çalıştığından emin olun
docker ps | grep postgres
```

**Çözüm:**
- Test loglarını GitHub Actions UI'dan kontrol edin
- Local'de testleri çalıştırıp hataları düzeltin
- PostgreSQL service'in health check'ini kontrol edin

### Problem 3: Docker Build Başarısız

**Hata:**
```
Docker build failed
```

**Kontrol:**
```bash
# Local'de Docker build yapın
docker build -t test-image .

# Dockerfile syntax'ını kontrol edin
docker build --no-cache -t test-image .
```

**Çözüm:**
- Dockerfile'daki syntax hatalarını düzeltin
- Multi-stage build adımlarını kontrol edin
- Build loglarını GitHub Actions UI'dan inceleyin

### Problem 4: Container Registry Push Başarısız

**Hata:**
```
Permission denied: ghcr.io
```

**Kontrol:**
```bash
# Repository Settings → Actions → General
# "Workflow permissions" → "Read and write permissions" aktif olmalı
```

**Çözüm:**
1. Repository Settings → Actions → General
2. Workflow permissions → "Read and write permissions" seçin
3. Workflow'u yeniden çalıştırın

### Problem 5: Image Pull Başarısız

**Hata:**
```
unauthorized: authentication required
```

**Çözüm:**
```bash
# GitHub'a login olun
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# Veya personal access token kullanın
docker login ghcr.io -u USERNAME -p TOKEN
```

### Problem 6: Deployment Başarısız

**Hata:**
```
SSH connection failed
```

**Kontrol:**
```bash
# SSH bağlantısını test edin
ssh -i ~/.ssh/key user@host

# Secrets'ların doğru olduğundan emin olun
# Repository Settings → Secrets and variables → Actions
```

**Çözüm:**
- SSH key'in doğru olduğundan emin olun
- Server'ın erişilebilir olduğundan emin olun
- Firewall ayarlarını kontrol edin

---

## 📊 CI/CD Pipeline Özeti

### Kullanılan Dosyalar

| Dosya | Amaç | Konum |
|-------|------|-------|
| `.github/workflows/ci-cd.yml` | Workflow tanımı | `.github/workflows/` |
| `Dockerfile` | Docker image build | Proje kök dizini |
| `pom.xml` | Maven build | Proje kök dizini |

### Workflow Adımları

1. ✅ **Checkout** → Kod çekilir
2. ✅ **Setup JDK 17** → Java kurulumu
3. ✅ **PostgreSQL Service** → Test DB başlatılır
4. ✅ **Run Tests** → Maven test
5. ✅ **Build Application** → Maven package
6. ✅ **Upload Artifact** → JAR kaydedilir
7. ✅ **Docker Buildx** → Docker setup
8. ✅ **Login to Registry** → ghcr.io giriş
9. ✅ **Extract Metadata** → Tag oluşturma
10. ✅ **Build & Push** → Image oluşturma ve push

### GitHub Tarafında Yapılanlar

1. ✅ **GitHub Actions** aktif edildi
2. ✅ **Workflow permissions** yapılandırıldı
3. ✅ **Container Registry** otomatik kullanılabilir
4. ⚠️ **Secrets** eklendi (deployment için, opsiyonel)

### Image Tag Stratejisi

- `latest` → Main branch için
- `main-<sha>` → SHA ile tag
- `v1.0.0` → Semver tag (opsiyonel)
- `pr-123` → Pull request tag

---

## 🎯 Sonuç

Bu rehber ile OAuth2JWT projesine CI/CD pipeline'ı başarıyla kurulmuştur. Her push ve pull request'te otomatik olarak:

- ✅ Testler çalıştırılır
- ✅ Uygulama build edilir
- ✅ Docker image oluşturulur
- ✅ Image GitHub Container Registry'ye push edilir

**Başarılı kurulum için:**
- ✅ GitHub Actions aktif olmalı
- ✅ Workflow permissions doğru yapılandırılmalı
- ✅ Dockerfile mevcut olmalı
- ✅ Testler başarılı olmalı

**İyi çalışmalar! 🚀**

---

## 📚 Ek Kaynaklar

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [GitHub Container Registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [Docker Buildx](https://docs.docker.com/buildx/)
- [Maven Documentation](https://maven.apache.org/)

