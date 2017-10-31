.PHONY: gcp

help:
	@echo "Usage: make <command>"
	@echo ""
	@echo "  build            Build all the Docker containers."
	@echo "  push             Push all the build Docker containers."
	@echo "  deploy-gcp       Deploy the Docker stack on Google Cloud (require SSH configuration)."
	@echo "  undeploy-gcp     Undeploy the Docker stack from Google Cloud (require SSH configuration)."
	@echo "  deploy-local     Deploy the Docker stack on the local machine."
	@echo "  undeploy-local   Undeploy the Docker stack from the local machine."
	@echo "  all-gcp          Build and push the containers to the registry, deploy to Google Cloud."
	@echo "  all-local        Build and push the containers to the registry, deploy to the local machine."
	@echo "  load-test        Launch a load test against the deployment on Google Cloud"
	@echo ""

build-frontend:
	@echo "---------------------------------------"
	@echo "  [BUILD] Frontend"
	@echo "---------------------------------------"
	@docker build -t wacccourse/frontend:latest frontend
	@echo ""

build-backend:
	@echo "---------------------------------------"
	@echo "  [BUILD] Backend"
	@echo "---------------------------------------"
	@(cd ./backend && sbt docker:publishLocal)
	@docker tag wacc-backend wacccourse/backend:latest
	@echo ""

build-docker-proxy:
	@echo "---------------------------------------"
	@echo "  [BUILD] Docker Proxy"
	@echo "---------------------------------------"
	@docker build -t wacccourse/docker-socket-proxy:latest docker-socket-proxy
	@echo ""

build-mongo-setup:
	@echo "---------------------------------------"
	@echo "  [BUILD] Mongo Setup"
	@echo "---------------------------------------"
	@docker build -t wacccourse/mongo-setup:latest mongo-setup
	@echo ""

build-traefik:
	@echo "---------------------------------------"
	@echo "  [BUILD] Traefik"
	@echo "---------------------------------------"
	@docker build -t wacccourse/traefik:latest traefik
	@echo ""

build: build-frontend build-backend build-docker-proxy build-mongo-setup build-traefik

push-frontend:
	@echo "---------------------------------------"
	@echo "  [PUSH] Frontend"
	@echo "---------------------------------------"
	@docker push wacccourse/frontend
	@echo ""

push-backend:
	@echo "---------------------------------------"
	@echo "  [PUSH] Backend"
	@echo "---------------------------------------"
	@docker push wacccourse/backend
	@echo ""

push-docker-proxy:
	@echo "---------------------------------------"
	@echo "  [PUSH] Docker Proxy"
	@echo "---------------------------------------"
	@docker push wacccourse/docker-socket-proxy
	@echo ""

push-traefik:
	@echo "---------------------------------------"
	@echo "  [PUSH] Traefik"
	@echo "---------------------------------------"
	@docker push wacccourse/traefik
	@echo ""

push-mongo-setup:
	@echo "---------------------------------------"
	@echo "  [BUILD] Mongo Setup"
	@echo "---------------------------------------"
	@docker push wacccourse/mongo-setup
	@echo ""

push: push-frontend push-backend push-docker-proxy push-mongo-setup push-traefik

undeploy-local:
	@echo "---------------------------------------"
	@echo "  [UNDEPLOY] Local Machine"
	@echo "---------------------------------------"
	docker stack rm wacc
	sleep 3
	docker volume rm wacc_mongo_data || exit 0
	docker volume rm wacc_cassandra_data || exit 0
	@echo ""

deploy-local: undeploy-local
	@echo "---------------------------------------"
	@echo "  [DEPLOY] Local Machine"
	@echo "---------------------------------------"
	@docker stack deploy wacc -c wacc-stack.yml
	@echo ""

undeploy-gcp:
	@echo "---------------------------------------"
	@echo "  [UNDEPLOY] Google Cloud Platform"
	@echo "---------------------------------------"
	ssh wacc0 'rm -r ~/repository || exit 0'
	ssh wacc0 'docker stack rm wacc'
	sleep 3
	ssh wacc0 'docker container prune -f'
	ssh wacc1 'docker container prune -f'
	ssh wacc2 'docker container prune -f'
	ssh wacc3 'docker container prune -f'
	ssh wacc1 'docker volume rm -f wacc_mongo_data wacc_cassandra_data wacc_zookeeper_data wacc_zookeeper_datalog wacc_kafka_data || exit 0'
	ssh wacc2 'docker volume rm -f wacc_mongo_data wacc_cassandra_data wacc_zookeeper_data wacc_zookeeper_datalog wacc_kafka_data || exit 0'
	ssh wacc3 'docker volume rm -f wacc_mongo_data wacc_cassandra_data wacc_zookeeper_data wacc_zookeeper_datalog wacc_kafka_data || exit 0'
	@echo ""

deploy-gcp:
	@echo "---------------------------------------"
	@echo "  [DEPLOY] Google Cloud Platform"
	@echo "---------------------------------------"
	ssh wacc0 'mkdir ~/repository -p'
	scp wacc-gcp.yml wacc0:~/repository/wacc-gcp.yml
	scp gcp.env wacc0:~/repository/.env
	ssh wacc0 'set -a && source ~/repository/.env && docker stack deploy wacc -c ~/repository/wacc-gcp.yml'
	@echo ""

all-gcp: build push deploy-gcp

all-local: build push deploy-local

load-test:
	bzt jmeter/wacc.jmx

reboot-gcp:
	@echo "---------------------------------------"
	@echo "  [REBOOT] Google Cloud Platform"
	@echo "---------------------------------------"
	@ssh wacc0 'sudo reboot' || exit 0
	@ssh wacc1 'sudo reboot' || exit 0
	@ssh wacc2 'sudo reboot' || exit 0
	@ssh wacc3 'sudo reboot' || exit 0
