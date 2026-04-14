# Deployment Guide

## Docker Deployment

### Local Development with Docker Compose

1. Copy environment variables:
```bash
cp .env.example .env
```

2. Start the application:
```bash
docker-compose up --build
```

3. Access the application:
- Backend API: http://localhost:8080
- Health check: http://localhost:8080/actuator/health
- API Documentation: http://localhost:8080/swagger-ui.html

### Build Docker Image

```bash
# Build image
docker build -t lms-platform:latest .

# Run container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/mydatabase \
  -e DB_USERNAME=myuser \
  -e DB_PASSWORD=your-password \
  lms-platform:latest
```

## PaaS Deployment Options

### 1. Render (Recommended)

**Free Tier Available:**
- PostgreSQL: Free
- Web Service: Free (with limitations)

**Steps:**

1. Create account at https://render.com
2. Fork this repository to your GitHub account
3. Connect Render to your GitHub repository

**Deploy PostgreSQL:**
- Go to Render Dashboard → New → PostgreSQL
- Database Name: `mydatabase`
- User: `myuser`
- Region: Choose nearest
- Click "Create Database"
- Copy the internal database URL

**Deploy Web Service:**
- Go to Render Dashboard → New → Web Service
- Connect your repository
- Build Command: `./mvnw clean package -DskipTests`
- Start Command: `java -jar target/lms-platform-0.0.1-SNAPSHOT.jar`
- Environment Variables:
  ```
  DB_URL=jdbc://<your-postgres-url>
  DB_USERNAME=myuser
  DB_PASSWORD=<your-postgres-password>
  SPRING_PROFILE=prod
  ```
- Click "Create Web Service"

### 2. Railway

**Free Tier Available:**
- PostgreSQL: Free ($5 free credit)
- Web Service: Free ($5 free credit)

**Steps:**

1. Install Railway CLI:
```bash
npm install -g @railway/cli
```

2. Login:
```bash
railway login
```

3. Initialize project:
```bash
railway init
```

4. Add PostgreSQL:
```bash
railway add postgresql
```

5. Add environment variables:
```bash
railway variables set DB_URL="jdbc://postgresql://localhost:5432/railway"
railway variables set DB_USERNAME="postgres"
railway variables set DB_PASSWORD="<password-from-railway>"
railway variables set SPRING_PROFILE="prod"
```

6. Deploy:
```bash
railway up
```

### 3. Fly.io

**Free Tier Available:**
- PostgreSQL: Free (limited)
- Apps: Free (limited)

**Steps:**

1. Install Flyctl:
```bash
curl -L https://fly.io/install.sh | sh
```

2. Login:
```bash
flyctl auth login
```

3. Create app:
```bash
flyctl launch
```

4. Create PostgreSQL:
```bash
flyctl postgres create
```

5. Set environment variables:
```bash
flyctl secrets set DB_URL="jdbc://<postgres-url>"
flyctl secrets set DB_USERNAME="postgres"
flyctl secrets set DB_PASSWORD="<password>"
flyctl secrets set SPRING_PROFILE="prod"
```

6. Deploy:
```bash
flyctl deploy
```

## CI/CD with GitHub Actions

### Required Secrets

Add these secrets to your GitHub repository (Settings → Secrets and variables → Actions):

```
DOCKER_USERNAME          # Your Docker Hub username
DOCKER_PASSWORD          # Your Docker Hub password/access token
APP_URL                 # Your deployed application URL
RENDER_API_KEY          # (Optional) Render API key for deployment
RAILWAY_TOKEN           # (Optional) Railway token for deployment
```

### Workflow Stages

The CI/CD pipeline includes:

1. **Build and Test**
   - Compiles the application
   - Runs unit tests with PostgreSQL service
   - Generates test reports
   - Builds JAR artifact

2. **Docker Build**
   - Builds Docker image
   - Pushes to Docker Hub
   - Uses build cache for faster builds

3. **Deploy**
   - Deploys to PaaS (configure based on your choice)
   - Runs healthcheck
   - Verifies deployment

4. **Security Scan**
   - Scans for vulnerabilities using Trivy
   - Uploads results to GitHub Security

### Manual Deployment

To deploy manually:

```bash
# Build application
./mvnw clean package -DskipTests

# Build Docker image
docker build -t <your-dockerhub-username>/lms-platform:latest .

# Push to Docker Hub
docker push <your-dockerhub-username>/lms-platform:latest

# Pull and run on server
docker pull <your-dockerhub-username>/lms-platform:latest
docker run -p 8080:8080 \
  -e DB_URL=jdbc://<your-db-url> \
  -e DB_USERNAME=myuser \
  -e DB_PASSWORD=<password> \
  <your-dockerhub-username>/lms-platform:latest
```

## Health Check

The application exposes a health check endpoint at `/actuator/health`.

**Example:**
```bash
curl https://your-app-url.com/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760,
        "path": "/app"
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| DB_URL | PostgreSQL JDBC URL | jdbc:postgresql://localhost:5432/mydatabase |
| DB_USERNAME | Database username | myuser |
| DB_PASSWORD | Database password | my-safe-password-2026 |
| SPRING_PROFILE | Spring profile | prod |

## Troubleshooting

### Database Connection Issues

1. Check if PostgreSQL is running:
```bash
docker-compose ps postgres
```

2. Check logs:
```bash
docker-compose logs postgres
docker-compose logs app
```

3. Test database connection:
```bash
docker-compose exec postgres psql -U myuser -d mydatabase
```

### Docker Build Issues

1. Clear Docker cache:
```bash
docker system prune -a
```

2. Rebuild without cache:
```bash
docker-compose build --no-cache
```

### CI/CD Failures

1. Check GitHub Actions logs
2. Verify all secrets are set correctly
3. Ensure Docker Hub credentials are valid
4. Check if PostgreSQL service is healthy in the workflow

### PaaS Deployment Issues

1. Check application logs in the PaaS dashboard
2. Verify environment variables are set correctly
3. Ensure database is accessible from the web service
4. Check if the port (8080) is correctly exposed

## Monitoring

### Actuator Endpoints

- Health: `/actuator/health`
- Info: `/actuator/info`
- Metrics: `/actuator/metrics`

### Logs

- Docker: `docker-compose logs -f app`
- PaaS: Check the PaaS dashboard for logs
- CI/CD: Check GitHub Actions logs

## Scaling

### Docker Compose Scaling

```bash
# Scale to 3 instances
docker-compose up --scale app=3
```

### PaaS Scaling

- Render: Upgrade to paid tier for horizontal scaling
- Railway: Add more dynos
- Fly.io: Use `flyctl scale count 3`
