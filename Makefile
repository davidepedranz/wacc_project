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

deploy: undeploy
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
	ssh wacc1 'set -a && source ~/repository/.env && docker stack deploy wacc -c ~/repository/wacc-stack.yml'
	@echo ""

gcp-clean:
	@echo "---------------------------------------"
	@echo "  Cleanup Google Cloud Platform"
	@echo "---------------------------------------"
	ssh wacc1 'rm -r ~/repository || exit 0'
	ssh wacc1 'docker stack rm wacc'
	@echo ""

all: build deploy