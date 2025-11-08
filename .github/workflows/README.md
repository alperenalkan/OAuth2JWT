# GitHub Actions CI/CD Pipeline

Bu dizin GitHub Actions workflow dosyalarını içerir.

## Workflow Dosyası

- `ci-cd.yml`: Ana CI/CD pipeline

## Pipeline Özellikleri

### Build and Test Job
- ✅ JDK 17 setup
- ✅ Maven cache
- ✅ PostgreSQL service (test için)
- ✅ Unit test çalıştırma
- ✅ Application build
- ✅ JAR artifact upload

### Build Docker Image Job
- ✅ Docker Buildx setup
- ✅ GitHub Container Registry login
- ✅ Multi-arch image build (amd64, arm64)
- ✅ Image tagging (branch, SHA, latest)
- ✅ Cache optimization

### Deploy Job
- ✅ Production environment
- ✅ Deployment automation (yapılandırılabilir)

## Kullanım

### Otomatik Tetikleme

Pipeline şu durumlarda otomatik çalışır:
- `main` veya `master` branch'e push
- `develop` branch'e push
- Pull request açıldığında

### Manual Trigger

Workflow'u manuel olarak da tetikleyebilirsiniz:
1. GitHub repository'ye gidin
2. Actions sekmesine tıklayın
3. "CI/CD Pipeline" workflow'unu seçin
4. "Run workflow" butonuna tıklayın

## Secrets ve Environment Variables

### Gerekli Secrets (Deployment için)

Eğer deployment yapılandırmak isterseniz, aşağıdaki secrets'ları ekleyin:

- `HOST`: Server hostname/IP
- `USERNAME`: SSH username
- `SSH_KEY`: SSH private key

### GitHub Container Registry

Docker image'ler otomatik olarak GitHub Container Registry'ye push edilir:
- Registry: `ghcr.io`
- Image: `ghcr.io/your-username/oauth2jwt`

## Docker Image Kullanımı

```bash
# Latest image'i çek
docker pull ghcr.io/your-username/oauth2jwt:latest

# Specific tag çek
docker pull ghcr.io/your-username/oauth2jwt:main-abc1234

# Çalıştır
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/oauth2jwt \
  ghcr.io/your-username/oauth2jwt:latest
```

## Troubleshooting

### Build Failures
- Maven dependencies kontrol edin
- Test failures kontrol edin
- Java version uyumluluğunu kontrol edin

### Docker Build Failures
- Dockerfile syntax kontrol edin
- Multi-stage build adımlarını kontrol edin
- Registry permissions kontrol edin

### Deployment Failures
- SSH connection kontrol edin
- Server accessibility kontrol edin
- Environment variables kontrol edin

