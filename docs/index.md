# ants-jade
Algoritmos de Colônia de formigas (Ant colony optimization algorithms) com o framework [JADE](https://jade.tilab.com/)

Esse projeto busca implementar um de busca de melhor caminho utilizando algoritmos de colônia de formigas 

Procurando por um repositório modelo base com apenas o [MAVEN](https://maven.apache.org/) + JADE? olhe na branch ´base-model´

Para mais informações consulte: [Documentação do projeto](https://ciro-c.github.io/ants-jade/)

#### Requisitos 
 Para aplicação é necessário `JAVA >= 11`

 **Opcional** Para documentos é necessário `docker compose` ou [nodejs](https://nodejs.org/)

#### Rodando a aplicação
  Todos commandos aplicação se encontram no arquivo `makefile`;

  ```bash
  make build-and-run
  ``` 

#### Rodando a documentação
   ```bash
  docker-compose up
  ``` 