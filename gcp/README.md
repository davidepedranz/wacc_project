# Setup Google Cloud Computing

## DNS records
node1.wacc-group-4.tk
node2.wacc-group-4.tk
node3.wacc-group-4.tk

## Docker Setup
See `docker.sh`.

## Docker Swarm

### Node 1
```bash
docker swarm init
docker swarm join-token manager
```

### Node 2 and 3
```bash
docker swarm join --token xxx
```

## Docker Hub
Please login to Docker Hub from your local machine.
