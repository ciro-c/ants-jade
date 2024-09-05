PATH_PROJECT_JAR = target/cars-n-lights-1.0-SNAPSHOT.jar
PATH_LIBS    = src/main/resources/libs
LIBS_JAR    = $(PATH_LIBS)/jade.jar:$(PATH_LIBS)/jadeExamples:$(PATH_LIBS)/commons-codec-1.3
PROJECT_CLASS_PATH = $(LIBS_JAR):$(PATH_PROJECT_JAR)
PROJECT_GROUP    = org.fga.paradigmas
JADE_AGENTS      = mainAgent:$(PROJECT_GROUP).App; pedestrianAgent:$(PROJECT_GROUP).agents.PedestrianAgent;
JADE_FLAGS 		 = -gui -agents "$(JADE_AGENTS)"

.PHONY:
	clean
	build-and-run

build-and-run:
	@echo "Gerando a build e executando o projeto"
	make build run

build:
	@echo "Gerando a build do projeto"
	./mvnw clean install

run:
	@echo "Executando o projeto com a última build criada"
	java -cp $(PROJECT_CLASS_PATH) jade.Boot $(JADE_FLAGS)

clean:
	@echo "Removendo a build do projeto"
	./mvnw clean

build-and-run-win:
	@echo "Gerando a build e executando o projeto"
	make build-win run

build-win:
	@echo "Gerando a build do projeto"
	.\mvnw clean install
