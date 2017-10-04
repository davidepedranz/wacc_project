# Backend

## Build
```bash
sbt docker:publishLocal
```

## Run (Docker)
```bash
docker run -p 9000:9000 -e APPLICATION_SECRET=yeeee wacc-backend
```
