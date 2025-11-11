# Deploy Durumu ve Otomatik Deploy Kurulumu

## 📊 Mevcut Durum

### Şu Anda Çalışan İşlemler

1. ✅ **Test ve Build** → Çalışıyor
   - Her push ve PR'da otomatik çalışır
   - Testler çalıştırılır
   - JAR dosyası oluşturulur

2. ✅ **Docker Image Build ve Push** → Çalışıyor
   - Sadece `main` veya `master` branch'e push edildiğinde çalışır
   - Docker image oluşturulur
   - GitHub Container Registry'ye (ghcr.io) push edilir
   - Image adresi: `ghcr.io/alperenalkan/oauth2jwt:latest`

3. ⚠️ **Deploy** → Sadece log yazdırıyor (gerçek deployment yok)
   - Deploy job'ı çalışıyor ama sadece echo komutları var
   - Gerçek deployment yapılmıyor

---

## 🔄 Otomatik Tetikleme

### Şu Anda Ne Tetikleniyor?

**Otomatik olarak tetiklenen işlemler:**

1. **Her push ve PR'da:**
   - ✅ Test çalıştırma
   - ✅ Build işlemi

2. **Sadece `main` veya `master` branch'e push edildiğinde:**
   - ✅ Docker image build
   - ✅ Docker image push (ghcr.io)
   - ⚠️ Deploy job (ama sadece log yazdırıyor)

**Manuel tetikleme gerekmez!** Her push otomatik olarak workflow'u başlatır.

---

## 🚀 Otomatik Deploy'u Aktif Etme

### Senaryo 1: SSH ile Server'a Deploy

Eğer bir server'ınız varsa ve SSH ile bağlanabiliyorsanız:

#### Adım 1: GitHub Secrets Ekleme

1. GitHub repository'nize gidin
2. **Settings** → **Secrets and variables** → **Actions**
3. **New repository secret** butonuna tıklayın
4. Aşağıdaki secrets'ları ekleyin:

| Secret Adı | Açıklama | Örnek |
|------------|----------|-------|
| `HOST` | Server IP veya hostname | `192.168.1.100` veya `example.com` |
| `USERNAME` | SSH kullanıcı adı | `deploy` veya `ubuntu` |
| `SSH_KEY` | SSH private key | `-----BEGIN OPENSSH PRIVATE KEY-----...` |

**SSH Key Oluşturma:**
```bash
# SSH key oluştur (eğer yoksa)
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions

# Public key'i server'a ekle
ssh-copy-id -i ~/.ssh/github_actions.pub user@your-server

# Private key'i kopyala (GitHub secret olarak ekleyeceksiniz)
cat ~/.ssh/github_actions
```

#### Adım 2: Workflow Dosyasını Güncelleme

`.github/workflows/ci-cd.yml` dosyasındaki deploy bölümünü güncelleyin:

```yaml
deploy:
  name: Deploy Application
  runs-on: ubuntu-latest
  needs: build-docker-image
  if: github.event_name == 'push' && (github.ref == 'refs/heads/main' || github.ref == 'refs/heads/master')
  environment:
    name: production
    url: https://your-domain.com
  
  steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Deploy via SSH
      uses: appleboy/ssh-action@v1
      with:
        host: ${{ secrets.HOST }}
        username: ${{ secrets.USERNAME }}
        key: ${{ secrets.SSH_KEY }}
        script: |
          cd /path/to/your/app
          docker-compose pull
          docker-compose up -d
          docker system prune -f
```

**Önemli:** `/path/to/your/app` kısmını kendi server'daki uygulama dizininizle değiştirin.

#### Adım 3: Server'da docker-compose.yml Hazırlama

Server'da `docker-compose.yml` dosyası oluşturun:

```yaml
services:
  app:
    image: ghcr.io/alperenalkan/oauth2jwt:latest
    container_name: oauth2jwt-app
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/oauth2jwt
      SPRING_DATASOURCE_USERNAME: techpront
      SPRING_DATASOURCE_PASSWORD: 125322
      SPRING_JPA_HIBERNATE_DDL_AUTO: none
      SPRING_SQL_INIT_MODE: always
      JWT_SECRET: mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong
      JWT_EXPIRATION: 86400000
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    restart: unless-stopped

  postgres:
    image: postgres:15-alpine
    container_name: oauth2jwt-postgres
    environment:
      POSTGRES_DB: oauth2jwt
      POSTGRES_USER: techpront
      POSTGRES_PASSWORD: 125322
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  postgres_data:
```

#### Adım 4: GitHub Container Registry'ye Login

Server'da GitHub Container Registry'ye login olun:

```bash
# Server'da çalıştırın
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# Veya personal access token ile
docker login ghcr.io -u USERNAME -p TOKEN
```

**Not:** Image'ı public yaparsanız login gerekmez.

---

### Senaryo 2: Cloud Platform Deploy (AWS, GCP, Azure)

#### AWS ECS için:

```yaml
- name: Deploy to ECS
  uses: aws-actions/amazon-ecs-deploy-task-definition@v1
  with:
    task-definition: task-definition.json
    service: oauth2jwt-service
    cluster: oauth2jwt-cluster
    wait-for-service-stability: true
```

#### Google Cloud Run için:

```yaml
- name: Deploy to Cloud Run
  uses: google-github-actions/deploy-cloudrun@v1
  with:
    service: oauth2jwt
    image: ghcr.io/alperenalkan/oauth2jwt:latest
    region: us-central1
```

---

## 📝 Deploy Durumunu Kontrol Etme

### GitHub Actions UI'dan:

1. Repository → **Actions** sekmesi
2. **CI/CD Pipeline** workflow'unu seçin
3. Son run'a tıklayın
4. **Deploy Application** job'ını kontrol edin

### Server'da Kontrol:

```bash
# Container'ların çalıştığını kontrol et
docker ps

# Logları kontrol et
docker-compose logs -f app

# Yeni image'ın çekildiğini kontrol et
docker images | grep oauth2jwt
```

---

## ⚠️ Önemli Notlar

### 1. Image İzinleri

GitHub Container Registry'de image varsayılan olarak **private**'dır. Public yapmak için:

1. GitHub → **Packages** sekmesi
2. Image'a tıklayın
3. **Package settings** → **Change visibility** → **Public**

### 2. Environment Protection

Production environment için protection rules ekleyebilirsiniz:

1. **Settings** → **Environments** → **production**
2. **Required reviewers** ekleyin (opsiyonel)
3. **Wait timer** ekleyin (opsiyonel)

### 3. Deployment Rollback

Eğer deployment başarısız olursa:

```bash
# Server'da önceki image'a geri dön
docker-compose pull
docker-compose up -d --no-deps app
```

---

## 🎯 Özet

### Şu Anda:

- ✅ **Test ve Build** → Otomatik çalışıyor
- ✅ **Docker Image Build** → Otomatik çalışıyor (main/master push'unda)
- ⚠️ **Deploy** → Sadece log yazdırıyor (gerçek deployment yok)

### Otomatik Deploy İçin Yapılacaklar:

1. ✅ GitHub Secrets ekle (HOST, USERNAME, SSH_KEY)
2. ✅ Workflow dosyasını güncelle (SSH deployment kısmını aktif et)
3. ✅ Server'da docker-compose.yml hazırla
4. ✅ Server'da GitHub Container Registry'ye login ol
5. ✅ Push et → Otomatik deploy başlar!

**Manuel tetikleme gerekmez!** Her `main` veya `master` branch'e push otomatik olarak deploy'u başlatır.

---

## 🔍 Sorun Giderme

### Deploy Job Çalışmıyor

**Kontrol:**
- Branch adının `main` veya `master` olduğundan emin olun
- Workflow dosyasındaki `if` koşulunu kontrol edin

### SSH Bağlantı Hatası

**Kontrol:**
- Secrets'ların doğru olduğundan emin olun
- SSH key'in server'a eklendiğinden emin olun
- Firewall ayarlarını kontrol edin

### Image Pull Hatası

**Kontrol:**
- GitHub Container Registry'ye login olduğunuzdan emin olun
- Image'ın public olduğundan veya token'ın geçerli olduğundan emin olun

---

**İyi çalışmalar! 🚀**

