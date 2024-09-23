# Lançando agentes Jade

#### Manualmente

Para lançar agentes de maneira manual basta usar o comando `make run` com a flag `JADE_AGENTS` passando os agentes.

````bash
  make run JADE_AGENTS="agentOne:com.mvnJade.app.AgentOne;agentTwo:com.mvnJade.app.AgentTwo;"
````

Caso precise controlar as flags de maneira mais precisamente pode usar a flag `JADE_FLAGS`
````bash
  make run JADE_FLAGS="-gui"
````

#### Automaticamente

Para chamar agentes de maneira automatizada basta, no arquivo `Makefile`, adicionar o seu agente no final de linha contendo o `JADE_AGENTS`
````bash
JADE_AGENTS      = mainAgent:$(PROJECT_GROUP).HelloWorld;second:$(PROJECT_GROUP).SecondAgent;anotherAgent:$(PROJECT_GROUP).AnotherOne; 

````