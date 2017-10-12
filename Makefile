.PHONY: gcp

help:
	@echo "Usage: make <command>"
	@echo ""
	@echo "  build      build frontend and backend to Docker images"
	@echo "  undeploy   undeploy the Docker stack"
	@echo "  deploy     deploy the Docker stack"
	@echo "  all        build + deploy"
	@echo ""

build:
	@echo "---------------------------------------"
	@echo "  Frontend --> Docker"
	@echo "---------------------------------------"
	@docker build -t wacccourse/frontend:latest frontend
	@echo ""
	@echo "---------------------------------------"
	@echo "  Backend --> Docker"
	@echo "---------------------------------------"
	@(cd ./backend && sbt docker:publishLocal)
	@docker tag wacc-backend wacccourse/backend
	@echo ""
	@echo "---------------------------------------"
	@echo "  Backend --> Docker"
	@echo "---------------------------------------"
	@docker build -t wacccourse/docker-socket-proxy:latest docker-socket-proxy
	@echo ""

push: build
	@echo "---------------------------------------"
	@echo "  Pushing Imanges to Docker Hub"
	@echo "---------------------------------------"
	docker push wacccourse/frontend
	docker push wacccourse/backend
	docker push wacccourse/docker-socket-proxy
	@echo ""

undeploy:
	@echo "---------------------------------------"
	@echo "  Docker: undeploy old stack"
	@echo "---------------------------------------"
	@docker stack rm wacc
	@echo ""
	@echo "---------------------------------------"
	@echo "  Docker: remove consul network"
	@echo "---------------------------------------"
	@docker network rm consul-net || exit 0
	@echo ""

deploy: undeploy
	@echo "---------------------------------------"
	@echo "  Docker: create consul network"
	@echo "---------------------------------------"
	@docker network create consul-net -d overlay --subnet=172.20.0.0/24 || exit 0
	@echo ""

	@echo "---------------------------------------"
	@echo "  Docker: deploy stack"
	@echo "---------------------------------------"
	@docker stack deploy wacc -c wacc-stack.yml
	@echo ""

gcp:
	@echo "---------------------------------------"
	@echo "  Deploy to Google Cloud Platform"
	@echo "---------------------------------------"
	ssh wacc1 'mkdir ~/repository -p'
	scp wacc-stack.yml wacc1:~/repository/wacc-stack.yml
	scp gcp.env wacc1:~/repository/.env
	ssh wacc1 'docker network create consul-net -d overlay --subnet=172.20.0.0/24 || exit 0'
	ssh wacc1 'set -a && source ~/repository/.env && docker stack deploy wacc -c ~/repository/wacc-stack.yml'
	@echo ""

gcp-clean:
	@echo "---------------------------------------"
	@echo "  Cleanup Google Cloud Platform"
	@echo "---------------------------------------"
	ssh wacc1 'rm -r ~/repository || exit 0'
	ssh wacc1 'docker stack rm wacc'
	ssh wacc1 'docker network rm consul-net || exit 0'
	@echo ""

	
all: build deploy