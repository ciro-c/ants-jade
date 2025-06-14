package com.mvnJade.app;

import com.mvnJade.app.dto.MapTileDTO;
import com.mvnJade.app.world.World;

import jade.core.Profile;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import org.lwjgl.glfw.*;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class SimulationWindow {

    private long window;
    private long vg; // Contexto do NanoVG

    // Configurações da janela e do mundo
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;
    private final int WORLD_SIZE = 50; // Comece com um valor menor para testar
    private final float TILE_SIZE = (float) Math.min(WINDOW_WIDTH, WINDOW_HEIGHT) / WORLD_SIZE;
    private NVGColor color = NVGColor.create();

    private ContainerController cc; // O container do JADE para criar agentes
    private int antNumberToLaunch = 200;
    private int antsLaunched = 0;
    private final long antLaunchInterval = 50; // Intervalo em milissegundos
    private long lastAntLaunchTime = 0;
    private boolean spacebarPressed = false; // Para controlar o pressionar da tecla
    private World world;
    private boolean isRunning = false; // Controla se a simulação está rodando
    // No topo da classe SimulationWindow, junto com as outras variáveis

    public void run(String[] jadeArgs) {
        initJade(jadeArgs);
        init();
        loop();
        cleanup();
    }

    private void initJade(String[] args) {
        System.out.println("Iniciando o container JADE...");
        // 1. Pega a instância do Runtime do JADE
        Runtime rt = Runtime.instance();

        // 2. Cria um perfil de configuração
        Profile profile = new ProfileImpl();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-gui")) {
                profile.setParameter(Profile.GUI, "true");
            } else if (arg.equals("-agents")) {
                if (i + 1 < args.length) {
                    profile.setParameter(Profile.AGENTS, args[i + 1]);
                    i++;
                }
            }
        }

        // 3. Cria o container principal do JADE
        this.cc = rt.createMainContainer(profile);
        System.out.println("Container JADE iniciado.");
    }

    private void init() {
        // Inicializa GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configura a janela
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Ant Colony Simulation - LWJGL", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Centraliza a janela
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Ativa o V-Sync
        glfwShowWindow(window);

        // Inicializa OpenGL
        GL.createCapabilities();

        // Inicializa NanoVG
        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (vg == NULL) {
            throw new RuntimeException("Could not init NanoVG");
        }

        // --- AQUI VOCÊ INICIA SUA LÓGICA DE SIMULAÇÃO (JADE, WORLD, ETC) ---
        // Exemplo:
        // ContainerController cc = Runtime.instance().createMainContainer(new
        // ProfileImpl());
        this.world = World.getInstance(WORLD_SIZE);
        System.out.println("Pressione ESPAÇO para iniciar/pausar a simulação.");
    }

    private void loop() {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f); // Cor de fundo cinza escuro

        while (!glfwWindowShouldClose(window)) {
            // --- 1. PROCESSAR INPUTS ---
            processInput();

            // --- 2. ATUALIZAR A LÓGICA DA SIMULAÇÃO ---
            if (isRunning) {
                updateSimulation();
            }

            // --- 3. RENDERIZAR O MUNDO ---
            render();

            glfwPollEvents(); // Processa eventos da janela (fechar, redimensionar, etc.)
        }
    }

    private void processInput() {
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_RELEASE) {
            if (!spacebarPressed) {
                isRunning = !isRunning;
                world.setRunning(isRunning); // Informa o novo estado para o mundo
                System.out.println("Simulação " + (isRunning ? "iniciada." : "pausada."));

                if (isRunning) {
                    lastAntLaunchTime = System.currentTimeMillis();
                }
            }
            spacebarPressed = true;
        } else {
            spacebarPressed = false;
        }
    }

    private void updateSimulation() {
        if (!isRunning) {
            return;
        }

        world.updateWorld();

        // Verifica se ainda há formigas para lançar
        if (antsLaunched >= antNumberToLaunch) {
            return;
        }

        // Lógica de tempo para lançar formigas em intervalos
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAntLaunchTime > antLaunchInterval) {
            launchAnt(antsLaunched++);
            lastAntLaunchTime = currentTime; // Atualiza o tempo do último lançamento
        }
    }

    void launchAnt(int number) {
        try {
            // Usa o ContainerController (cc) que inicializamos no initJade
            AgentController ag = cc.createNewAgent("ant" + number, "com.mvnJade.app.agents.Ant", new Object[0]);
            ag.start();
        } catch (Exception e) {
            System.err.println("Falha ao criar agente formiga: " + number);
            e.printStackTrace();
        }
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        nvgBeginFrame(vg, WINDOW_WIDTH, WINDOW_HEIGHT, 1f);

        if (this.world == null) {
            nvgEndFrame(vg);
            glfwSwapBuffers(window);
            return;
        }

        float maxPheromoneForDisplay = 3.0f; // Use isso para normalizar a opacidade

        // Itera por cada célula do mapa
        for (int i = 0; i < world.getMapSize(); i++) {
            for (int j = 0; j < world.getMapSize(); j++) {
                MapTileDTO tile = world.getPosition(i, j);
                if (tile == null)
                    continue;

                float x = i * TILE_SIZE;
                float y = j * TILE_SIZE;

                // --- CAMADA 1: DESENHA O FUNDO DO TILE ---
                if (tile.getIsHome()) {
                    nvgRGBAf(0.8f, 0.2f, 0.2f, 1.0f, color); // Vermelho escuro para a casa
                } else if (tile.getIsFood()) {
                    nvgRGBAf(0.2f, 0.8f, 0.2f, 1.0f, color); // Verde para a comida
                } else if (tile.getIsBlock()) {
                    nvgRGBAf(0.4f, 0.3f, 0.2f, 1.0f, color); // Marrom para blocos
                } else {
                    // Lógica para desenhar feromônios ou um tile vazio
                    float foodPheromone = tile.getPheromoneFoundFood();
                    float exploringPheromone = tile.getPheromoneExploring();

                    if (foodPheromone > 0.01f || exploringPheromone > 0.01f) {
                        float strength = Math.min((foodPheromone + exploringPheromone) / maxPheromoneForDisplay, 1.0f);

                        // Cores base
                        float emptyR = 0.9f, emptyG = 0.9f, emptyB = 0.9f;
                        float pheroR, pheroG, pheroB;

                        // NOVA LÓGICA DE COR
                        if (foodPheromone > 0.01f && exploringPheromone > 0.01f) {
                            // Ambos os feromônios estão presentes -> ROXO
                            pheroR = 0.6f;
                            pheroG = 0.2f;
                            pheroB = 0.8f;
                        } else if (foodPheromone > exploringPheromone) {
                            // Apenas feromônio de comida -> AZUL
                            pheroR = 0.3f;
                            pheroG = 0.3f;
                            pheroB = 0.9f;
                        } else {
                            // Apenas feromônio de exploração -> AMARELO
                            pheroR = 0.9f;
                            pheroG = 0.9f;
                            pheroB = 0.3f;
                        }

                        // A interpolação para o efeito de "clarear" continua a mesma
                        float finalR = emptyR * (1 - strength) + pheroR * strength;
                        float finalG = emptyG * (1 - strength) + pheroG * strength;
                        float finalB = emptyB * (1 - strength) + pheroB * strength;

                        nvgRGBAf(finalR, finalG, finalB, 1.0f, color);

                    } else {
                        nvgRGBAf(0.9f, 0.9f, 0.9f, 1.0f, color); // Célula vazia (Cinza claro)
                    }
                }

                // Desenha o retângulo de fundo
                nvgBeginPath(vg);
                nvgRect(vg, x, y, TILE_SIZE, TILE_SIZE);
                nvgFillColor(vg, color);
                nvgFill(vg);

                // --- CAMADA 2: DESENHA A FORMIGA (SE EXISTIR) POR CIMA ---
                if (tile.getAnts() > 0) {
                    // Desenha a formiga como um quadrado preto um pouco menor no centro do tile
                    float antSize = TILE_SIZE * 0.8f;
                    float offset = (TILE_SIZE - antSize) / 2.0f;

                    nvgBeginPath(vg);
                    nvgRect(vg, x + offset, y + offset, antSize, antSize);
                    nvgFillColor(vg, nvgRGBAf(0.1f, 0.1f, 0.1f, 1.0f, color)); // Cor da formiga
                    nvgFill(vg);
                }
            }
        }

        nvgEndFrame(vg);
        glfwSwapBuffers(window);
    }

    private void cleanup() {
        // Libera o objeto de cor
        color.free();

        // Libera os outros recursos
        nvgDelete(vg);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        GLFWErrorCallback prevCallback = glfwSetErrorCallback(null);
        if (prevCallback != null) {
            prevCallback.free();
        }
    }

    public static void main(String[] args) {
        new SimulationWindow().run(args);
    }
}