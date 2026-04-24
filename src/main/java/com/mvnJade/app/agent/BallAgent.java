package com.mvnJade.app.agent;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.mvnJade.app.model.GameState;
import com.mvnJade.app.model.Entity;

public class BallAgent extends Agent {
    private float x = 400; // Começa no meio do campo (considerando tela 800x600)
    private float y = 300;
    private float vx = 0;  // Velocidade X
    private float vy = 0;  // Velocidade Y
    private final float ATRITO = 0.98f; // Faz a bola parar aos poucos

    private Entity representacaoVisual;

    @Override
    protected void setup() {
        System.out.println("A Bola está em campo!");
        
        representacaoVisual = new Entity(getLocalName(), x, y) {};
        GameState.getInstance().registerEntity(representacaoVisual);

        addBehaviour(new TickerBehaviour(this, 16) {
            @Override
            protected void onTick() {
                if (GameState.getInstance().getStatus() == GameState.Status.RUNNING) {
                    ouvirChutes();
                    aplicarFisica();
                }
            }
        });
    }

    private void ouvirChutes() {
        // Verifica se chegou alguma mensagem do tipo INFORM (que estamos usando para "chute")
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = receive(mt);
        
        if (msg != null) {
            // O jogador envia a força no formato "forcaX,forcaY"
            String[] forcas = msg.getContent().split(",");
            vx += Float.parseFloat(forcas[0]);
            vy += Float.parseFloat(forcas[1]);
        }
    }

    private void aplicarFisica() {
        x += vx;
        y += vy;
        
        // Aplica o atrito para a bola ir parando
        vx *= ATRITO;
        vy *= ATRITO;

        // Bater nas bordas da tela e quicar
        if (x < 0 || x > 800) { vx *= -1; x = Math.max(0, Math.min(x, 800)); }
        if (y < 0 || y > 600) { vy *= -1; y = Math.max(0, Math.min(y, 600)); }

        representacaoVisual.updatePosition(x, y);
    }
}