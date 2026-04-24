package com.mvnJade.app.agent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import com.mvnJade.app.model.GameState;

public class JadeController {

    private ContainerController mainContainer;

    public void startJade() {
        // Pega a instância do motor do JADE
        Runtime rt = Runtime.instance();
        
        // Cria um perfil de configuração (GUI = false desativa a janelinha padrão do JADE)
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "false"); 
        
        // Cria o container principal onde os agentes vão viver
        mainContainer = rt.createMainContainer(profile);

        try {
            System.out.println("Criando agentes no JADE...");

            jade.wrapper.AgentController bola = mainContainer.createNewAgent("Bola", BallAgent.class.getName(), new Object[]{});
            bola.start();
            
            // Cria dois jogadores para testarmos
            AgentController player1 = mainContainer.createNewAgent("Jogador1", PlayerAgent.class.getName(), new Object[]{});
            player1.start();
            
            AgentController player2 = mainContainer.createNewAgent("Jogador2", PlayerAgent.class.getName(), new Object[]{});
            player2.start();
            
            // Muda o status do jogo para RUNNING para que os agentes comecem a se mover no TickerBehaviour
            GameState.getInstance().setStatus(GameState.Status.RUNNING);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}