package com.mvnJade.app.agent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.TickerBehaviour;
import com.mvnJade.app.model.GameState;
import com.mvnJade.app.model.Entity;

public class PlayerAgent extends Agent {

    // 1. O agente guarda seu PRÓPRIO posicionamento e física
    private float meuX;
    private float meuY;
    private float velocidadeX = 2.0f; 
    private float velocidadeY = 1.0f;

    // 2. A "foto" que ele vai mandar para o quadro de avisos do LWJGL
    private Entity minhaRepresentacaoVisual;

    @Override
    protected void setup() {
        System.out.println("Jogador " + getLocalName() + " entrou em campo!");

        // Posição inicial (poderia vir de getArguments() passado na criação do agente)
        meuX = 100.0f; 
        meuY = 100.0f;

        // Instancia a Entidade visual (usando uma classe anônima rápida baseada na classe abstrata do Passo 1)
        minhaRepresentacaoVisual = new Entity(getLocalName(), meuX, meuY) {};
        
        // Registra essa entidade no "Quadro de Avisos" (O Estado Compartilhado)
        GameState.getInstance().registerEntity(minhaRepresentacaoVisual);

        // 3. O Loop de Inteligência (Roda a cada 16ms, simulando ~60 ações por segundo)
        addBehaviour(new TickerBehaviour(this, 16) {
            @Override
            protected void onTick() {
                // O agente olha para o estado do jogo. Se o juiz (ou os botões) pararam o jogo, ele não se move.
                if (GameState.getInstance().getStatus() == GameState.Status.RUNNING) {
                    pensarEAgir();
                }
            }
        });
    }

    private void pensarEAgir() {
        // Olha para o quadro de avisos para descobrir onde a bola está
        Entity bola = GameState.getInstance().getEntities().get("Bola");
        
        if (bola != null) {
            float bolaX = bola.getPosition().getX();
            float bolaY = bola.getPosition().getY();
            
            // Calcula a distância entre o jogador e a bola
            float dx = bolaX - meuX;
            float dy = bolaY - meuY;
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distancia > 15.0f) {
                // Se está longe, corre na direção da bola
                meuX += (dx / distancia) * velocidadeX;
                meuY += (dy / distancia) * velocidadeY;
            } else {
                // Chegou na bola! Prepara o chute via mensagem ACL
                ACLMessage chute = new ACLMessage(ACLMessage.INFORM);
                chute.addReceiver(new jade.core.AID("Bola", jade.core.AID.ISLOCALNAME));
                
                // Gera uma força aleatória para simular um chute pro alto ou pros lados
                float forcaX = (float) (Math.random() * 10 - 5);
                float forcaY = (float) (Math.random() * 10 - 5);
                
                chute.setContent(forcaX + "," + forcaY);
                send(chute);
                
                System.out.println(getLocalName() + " chutou a bola!");
            }
        }

        // Atualiza a própria posição visual
        minhaRepresentacaoVisual.updatePosition(meuX, meuY);
    }
}