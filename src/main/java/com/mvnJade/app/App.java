package com.mvnJade.app;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mvnJade.app.world.World;

import jade.core.ProfileImpl;
import jade.core.Runtime;

/**
 * Hello world!
 *
 */
public class App 
{
    
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ContainerController cc = Runtime.instance().createMainContainer(new ProfileImpl());
        System.out.println( "Conceiner created" );
        World newWorld = World.getInstance(3);
        Object[] agentArgs = {};
        try {
            AgentController ag =  cc.createNewAgent("testAnt", "com.mvnJade.app.agents.Ant",agentArgs);
            ag.start();
            // TODO: Start gui and add multiple ants
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }
}
