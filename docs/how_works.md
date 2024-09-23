# Como o projeto funciona

O projeto tem como base o [MAVEN](https://maven.apache.org/) que organiza e gerencia o processo de build automaticamente, o `Makefile` para facilitar os comandos do terminal e o framework [JADE](https://jade.tilab.com/) para criar e gerenciar os Agentes.


### MAVEN
***OBS: é recomendado utilizar o comando `./mvnw` invés do `mvn` para melhor consistência.***

Faz o build do projeto com base na organização de pastas, então cada pasta dentro do `src/main/java` é compilado para um pacote específico.

Exemplo:

  Se tivermos uma classe chamada `HelloWorld` que fica dentro do `src/main/java/com/mvnJade/app/HelloWorld.java` então o pacote dele é `com.mvnJade.app`

Sobre dependências, o Maven cuida delas para você, basta colocar ela dentro do `pom.xml`. Se for uma dependência que não está disponível nos repositórios da Maven dê uma olhada na documentação [Mudando dependências](dependencies.md).

### MAKEFILE
Apenas um arquivo que facilita rodar o projeto usando comandos mais curtos.
Quando você chama:
````bash
make run
````
Ele roda:
````bash
java -cp src/libs/jade.jar:src/libs/jadeExamples.jar:src/libs/commons-codec-1.3.jar:target/maven-jade-1.0-SNAPSHOT.jar jade.Boot -gui -agents "mainAgent:com.mvnJade.app.HelloWorld"
````
O comando make também permite passar variáveis diretamente invés de usar o que está no arquivo.
Se rodar:
````bash
make run JADE_AGENTS="teste:com.mvnJade.app.OutroAgente"
````
Ele roda:
````bash
java -cp src/libs/jade.jar:src/libs/jadeExamples.jar:src/libs/commons-codec-1.3.jar:target/maven-jade-1.0-SNAPSHOT.jar jade.Boot -gui -agents "teste:com.mvnJade.app.OutroAgente"
````

### Jade
O Jade é o núcleo do projeto. Ele está instalado como uma dependência `.jar` na versão 4.6.0.

Também está instalado os exemplos do jade. Para executar um exemplo basta executar um comando trocando o `host:examples.party.HostAgent` por outro exemplo.
````bash
make run JADE_FLAGS="-gui host:examples.party.HostAgent"
````

Para mais informações de como lançar mais agentes consulte: [Lançando agentes jade](launch_agents.md).

para mudar a versão do jade ou das outras libs de uma olhada em: [Mudando dependências](dependencies.md).
