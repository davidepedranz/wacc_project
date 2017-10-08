help:
	@echo "Usage: make <command>"
	@echo ""
	@echo "  build    build frontend and backend to Docker images"
	@echo "  deploy   deploy the Docker stack"
	@echo "  all      build + deploy"
	@echo ""

build:
	@echo "---------------------------------------"
	@echo "  Frontend --> Docker"
	@echo "---------------------------------------"
	@docker build -t wacc-frontend:latest frontend
	@echo ""
	@echo "---------------------------------------"
	@echo "  Backend --> Docker"
	@echo "---------------------------------------"
	@(cd ./backend && sbt docker:publishLocal)
	@echo ""

deploy:
	@echo "---------------------------------------"
	@echo "  Docker: undeploy old stack"
	@echo "---------------------------------------"
	@docker stack rm wacc
	@echo ""
	@echo "---------------------------------------"
	@echo "  Docker: deploy stack"
	@echo "---------------------------------------"
	@docker stack deploy wacc -c wacc-stack.yml
	@echo ""

all: build deploy