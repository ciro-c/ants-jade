package com.mvnJade.app;

import com.mvnJade.app.agent.JadeController;
import com.mvnJade.app.model.Entity;
import com.mvnJade.app.model.GameState;
import com.mvnJade.app.render.FieldRenderer;
import com.mvnJade.app.render.UIManager;
import com.mvnJade.app.render.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class App {
    private Window window;
    private UIManager uiManager;
    private FieldRenderer fieldRenderer;

    public void run() {
        System.out.println("Iniciando o Sistema Multiagentes...");
        JadeController jadeController = new JadeController();
        jadeController.startJade();

        System.out.println("Iniciando o Motor Gráfico...");
        window = new Window(800, 600, "Simulador de Futebol (JADE + LWJGL)");
        window.init();

        initUI();
        loop();

        window.destroy();
        System.exit(0);
    }

    private void initUI() {
        uiManager = new UIManager();
        uiManager.init(window.getHandle());
        fieldRenderer = new FieldRenderer();

        GLFW.glfwSetMouseButtonCallback(window.getHandle(), (win, button, action, mods) -> {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
                double[] mouseX = new double[1];
                double[] mouseY = new double[1];
                GLFW.glfwGetCursorPos(win, mouseX, mouseY);
                uiManager.processClick(mouseX[0], mouseY[0], window.getWidth());
            }
        });
    }

    private void loop() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, window.getWidth(), window.getHeight(), 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        while (!window.shouldClose()) {
            GL11.glClearColor(0.1f, 0.6f, 0.2f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            fieldRenderer.render(window.getWidth(), window.getHeight()); // <-- ADICIONE ISTO

            drawEntities();

            uiManager.render(window.getWidth(), window.getHeight()); 

            window.update(); 
        }
    }

    private void drawEntities() {
        for (Entity entity : GameState.getInstance().getEntities().values()) {
            float x = entity.getPosition().getX();
            float y = entity.getPosition().getY();
            
            GL11.glBegin(GL11.GL_QUADS);
            
            if (entity.getId().equals("Bola")) {
                GL11.glColor3f(1.0f, 1.0f, 0.0f); // Amarelo
                GL11.glVertex2f(x - 3, y - 3);
                GL11.glVertex2f(x + 3, y - 3);
                GL11.glVertex2f(x + 3, y + 3);
                GL11.glVertex2f(x - 3, y + 3);
            } else {
                GL11.glColor3f(1.0f, 1.0f, 1.0f); // Branco
                GL11.glVertex2f(x - 5, y - 5);
                GL11.glVertex2f(x + 5, y - 5);
                GL11.glVertex2f(x + 5, y + 5);
                GL11.glVertex2f(x - 5, y + 5);
            }
            
            GL11.glEnd();
        }
    }

    public static void main(String[] args) {
        new App().run();
    }
}