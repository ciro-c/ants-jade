# Nome do artefato final gerado pelo maven-shade-plugin
FAT_JAR_NAME = maven-jade-1.0-SNAPSHOT.jar
FAT_JAR_PATH = target/$(FAT_JAR_NAME)

# Argumentos para a sua aplicação (que por sua vez iniciará o JADE)
# Deixamos o -gui por enquanto, mas ele pode não funcionar bem com a janela LWJGL
# O importante é a lista de agentes.
JADE_AGENTS = mainAgent:com.mvnJade.app.HelloWorld;
JADE_FLAGS  = -agents "$(JADE_AGENTS)"

.PHONY: build run clean

# Constrói o projeto criando o "fat JAR" executável
build:
	@echo "Gerando o JAR executável com todas as dependências..."
	./mvnw clean package

# Executa o JAR que foi gerado
run:
	@echo "Executando o projeto a partir do JAR empacotado..."
	java -jar $(FAT_JAR_PATH)

# Executa o JAR que foi gerado
run-custom:
	@echo "Executando o projeto a partir do JAR empacotado..."
	java -jar $(FAT_JAR_PATH) $(JADE_FLAGS)

# Limpa os arquivos gerados pelo build
clean:
	@echo "Limpando o projeto..."
	./mvnw clean