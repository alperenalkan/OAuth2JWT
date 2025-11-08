# Docker Kullanım Kılavuzu

Bu rehber, OAuth2JWT uygulamasını Docker ile nasıl çalıştıracağınızı açıklar.

## Hızlı Başlangıç

### Docker Compose ile (Önerilen)

```bash
# Tüm servisleri başlat (PostgreSQL + App)
docker-compose up -d

# Logları görüntüle
docker-compose logs -f app

# Servisleri durdur
docker-compose down

# Volume'ları da sil (veritabanı verilerini temizler)
docker-compose down -v
```

## Dockerfile

### Multi-stage Build

Dockerfile iki aşamalı build kullanır:

1. **Build Stage**: Maven ile uygulamayı derler
2. **Runtime Stage**: JRE ile uygulamayı çalıştırır

### Özellikler

- ✅ Multi-stage build (küçük image boyutu)
- ✅ Non-root user (güvenlik)
- ✅ Health check
- ✅ Alpine Linux (hafif base image)

## Docker Image Build

### Local Build

```bash
# Image build et
docker build -t oauth2jwt:latest .

# Image'i çalıştır
docker run -d --name oauth2jwt-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/oauth2jwt \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  oauth2jwt:latest
```

### GitHub Container Registry

CI/CD pipeline otomatik olarak image'leri GitHub Container Registry'ye push eder:

```bash
# Image'i çek
docker pull ghcr.io/your-username/oauth2jwt:latest

# Çalıştır
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/oauth2jwt \
  ghcr.io/your-username/oauth2jwt:latest
```

## Docker Compose

### Servisler

1. **postgres**: PostgreSQL 15 database
2. **app**: Spring Boot uygulaması

### Network

Servisler `oauth2jwt-network` adlı bir bridge network'te çalışır.

### Volumes

- `postgres_data`: PostgreSQL verilerini saklar

### Health Checks

Her iki servis de health check'e sahiptir:

- **PostgreSQL**: `pg_isready` komutu ile kontrol edilir
- **App**: `/actuator/health` endpoint'i ile kontrol edilir

## Environment Variables

### Application Variables

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/oauth2jwt
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: postgres
SPRING_JPA_HIBERNATE_DDL_AUTO: none
SPRING_SQL_INIT_MODE: always
JWT_SECRET: your-secret-key
JWT_EXPIRATION: 86400000
```

### Production Variables

Production ortamı için environment variable'ları ayarlayın:

```bash
# .env file
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/oauth2jwt
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
JWT_SECRET=your-production-secret-key
```

## Troubleshooting

### Container Başlamıyor

```bash
# Logları kontrol et
docker-compose logs app

# Container durumunu kontrol et
docker-compose ps

# Container'ı yeniden başlat
docker-compose restart app
```

### Database Connection Error

```bash
# PostgreSQL container'ının çalıştığından emin ol
docker-compose ps postgres

# PostgreSQL loglarını kontrol et
docker-compose logs postgres

# Network bağlantısını test et
docker-compose exec app ping postgres
```

### Health Check Failures

```bash
# Health check endpoint'ini manuel test et
curl http://localhost:8080/actuator/health

# Container loglarını kontrol et
docker-compose logs app
```

### Port Already in Use

```bash
# Port'u kullanan process'i bul
lsof -i :8080

# Veya docker-compose.yml'de port'u değiştir
ports:
  - "8081:8080"  # Host:Container
```

## Production Deployment

### Docker Swarm

```bash
# Stack deploy
docker stack deploy -c docker-compose.yml oauth2jwt
```

### Kubernetes

Kubernetes deployment için `k8s/` dizininde YAML dosyaları oluşturun.

### Cloud Platforms

- **AWS ECS**: ECS task definition kullanın
- **Azure Container Instances**: Azure CLI ile deploy edin
- **Google Cloud Run**: Cloud Run service oluşturun

## Best Practices

1. **Secrets Management**: Production'da secrets'ları environment variable olarak kullanın
2. **Resource Limits**: Memory ve CPU limitleri ayarlayın
3. **Logging**: Structured logging kullanın
4. **Monitoring**: Health checks ve metrics ekleyin
5. **Security**: Non-root user kullanın (zaten yapıldı)
6. **Image Optimization**: Multi-stage build kullanın (zaten yapıldı)

## Docker Commands

```bash
# Image build
docker build -t oauth2jwt:latest .

# Image run
docker run -p 8080:8080 oauth2jwt:latest

# Container list
docker ps

# Container logs
docker logs oauth2jwt-app

# Container exec
docker exec -it oauth2jwt-app sh

# Image remove
docker rmi oauth2jwt:latest

# System prune
docker system prune -a
```

