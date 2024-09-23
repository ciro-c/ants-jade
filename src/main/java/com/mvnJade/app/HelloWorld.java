package com.mvnJade.app;

import jade.core.Agent;

public class HelloWorld extends Agent {
  private static final long serialVersionUID = 1L;

  protected void setup() {
    System.out.println("Ola Mundo! ");
    System.out.println("Meu nome: " + getLocalName());
  }
}
