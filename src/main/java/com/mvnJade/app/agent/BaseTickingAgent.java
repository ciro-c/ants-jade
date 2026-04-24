package com.mvnJade.app.agent;

import com.mvnJade.app.MersenneTwisterFast;
import jade.core.Agent;

public class BaseTickingAgent extends Agent {

  protected final MersenneTwisterFast rng = new MersenneTwisterFast(System.nanoTime());

  @Override
  protected void setup() {
    // Agent base pronto para extender com comportamentos no futuro.
  }
}
