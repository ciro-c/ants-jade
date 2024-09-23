# Dependências

A instalação de dependências, de preferência, deve ser feita pelo Maven nos repositórios dela (da menos trabalho :) ), que é bem esplicado aqui [Maven in 5 minutes.](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) 


### Instalando `.jar`
Caso a dependência que você deseja utilizar seja um `.jar` ou você deseja alterar uma dependência `.jar` existente como `jade.jar`,`commons-codec-1.3.jar` ou `jadeExamples.jar`, vai ter que:

#### 1 - Adicionar o arquivo `.jar` ao projeto
Recomendamos colocar o arquivo dentro de `src/libs` para manter o padrão. Caso coloque em outro lugar na hora de colocar no makefile o `$(PATH_LIBS)` deverá ser substituído pelo caminho do arquivo

#### 2 - Adicionar a dependência ao makefile
No arquivo makefile adicionar o seguinte te código no final do arquivo e substituir o que está entre colchetes `[]` pelas informações da sua dependência
````bash
install-[lib]:
	@echo "Instalando [lib]"
	./mvnw clean install:install-file -Dfile=$(PATH_LIBS)/[lib].jar \
   -DgroupId=[lib-group-id] \
   -DartifactId=[lib-artifact-id] \
   -Dversion=[lib-version] \
   -Dpackaging=jar

````
depois adicionar no comando build-libs o seu comando criado
````bash
build-libs:
	@echo "Instalando JAR libs externas"
	make install-jade install-commons install-examples-jade install-[lib]
````

e por fim para adicionar no seu classPath basta adicionar no final do `LIBS_JAR`

````bash
LIBS_JAR    = $(PATH_LIBS)/jade.jar:$(PATH_LIBS)/jadeExamples.jar:$(PATH_LIBS)/commons-codec-1.3.jar:$(PATH_LIBS)/[lib].jar
````

#### 3 - Adicionar a dependência ao `pom.xml`
No arquivo `pom.xml` adicionar a sua nova dependência dentro da tag de `dependencies`
````bash
<dependencies>
  ... outras dependências
  <dependency>
    <groupId>[lib-group-id]</groupId>
    <artifactId>[lib-artifact-id]</artifactId>
    <version>[lib-version]</version>
  </dependency>
  ...
</dependencies>

````

Agora basta rodar `make build` que ele vai instalar tudo de maneira automática.
