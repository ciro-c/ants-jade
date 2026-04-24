package com.mvnJade.app.render;

import com.mvnJade.app.model.GameState;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2; 

public class UIManager {
    private long vg;
    private long windowHandle;
    
    // Dimensões e posições da UI
    private final float BAR_HEIGHT = 50;
    private final float BTN_WIDTH = 120;
    private final float BTN_HEIGHT = 30;
    private final float BTN_Y = 10;

    public void init(long window) {
        this.windowHandle = window;
        
        // Inicializa o contexto do NanoVG com Anti-Aliasing (bordas suaves)
        vg = NanoVGGL2.nvgCreate(NanoVGGL2.NVG_ANTIALIAS);
        if (vg == 0) {
            throw new RuntimeException("Falha ao inicializar o NanoVG.");
        }
        
        // Carrega a fonte Roboto
        int fontId = NanoVG.nvgCreateFont(vg, "roboto", "src/main/resources/fonts/Roboto-Regular.ttf");
        if (fontId == -1) {
            System.err.println("AVISO: Não foi possível carregar a fonte. Verifique se o caminho src/main/resources/fonts/Roboto-Regular.ttf está correto!");
        }
    }

    public void render(int width, int height) {
        // Informa ao NanoVG o tamanho da tela para ele calcular as proporções
        NanoVG.nvgBeginFrame(vg, width, height, 1);

        // 1. Desenha a barra superior (Fundo escuro)
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRect(vg, 0, 0, width, BAR_HEIGHT);
        NanoVG.nvgFillColor(vg, rgba(40, 40, 40, 255));
        NanoVG.nvgFill(vg);

        // 2. Botão: Iniciar/Parar (Azul)
        drawButton(20, BTN_Y, BTN_WIDTH, BTN_HEIGHT, rgba(70, 130, 180, 255), "Iniciar/Parar");
        
        // 3. Botão: Reiniciar (Amarelo/Laranja)
        drawButton(160, BTN_Y, BTN_WIDTH, BTN_HEIGHT, rgba(200, 150, 50, 255), "Reiniciar");
        
        // 4. Botão: Sair (Vermelho)
        drawButton(width - 140, BTN_Y, BTN_WIDTH, BTN_HEIGHT, rgba(200, 50, 50, 255), "Sair");

        // Finaliza o desenho da UI neste frame
        NanoVG.nvgEndFrame(vg);
    }

    private void drawButton(float x, float y, float w, float h, NVGColor color, String text) {
        // Desenha o fundo do botão
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, y, w, h, 5); 
        NanoVG.nvgFillColor(vg, color);
        NanoVG.nvgFill(vg);
        
        // Desenha o texto do botão
        NanoVG.nvgFontSize(vg, 16.0f);
        NanoVG.nvgFontFace(vg, "roboto");
        
        // Alinhamento inteligente: centraliza o texto no meio exato do botão
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
        NanoVG.nvgFillColor(vg, rgba(255, 255, 255, 255)); // Texto Branco
        
        // O X e Y passam a ser o centro do botão por causa do nvgTextAlign
        NanoVG.nvgText(vg, x + (w / 2), y + (h / 2), text);
    }

    // Método chamado pelo GLFW quando o usuário clica na tela
    public void processClick(double mouseX, double mouseY, int screenWidth) {
        // Só processa se o clique foi na altura da barra superior
        if (mouseY >= BTN_Y && mouseY <= BTN_Y + BTN_HEIGHT) {
            
            // Verificou clique no botão Azul (Iniciar/Parar)
            if (mouseX >= 20 && mouseX <= 20 + BTN_WIDTH) {
                GameState.Status statusAtual = GameState.getInstance().getStatus();
                if (statusAtual == GameState.Status.RUNNING) {
                    GameState.getInstance().setStatus(GameState.Status.STOPPED);
                    System.out.println("UI: Jogo PARADO");
                } else {
                    GameState.getInstance().setStatus(GameState.Status.RUNNING);
                    System.out.println("UI: Jogo RODANDO");
                }
            }
            
            // Verificou clique no botão Amarelo (Reiniciar)
            else if (mouseX >= 160 && mouseX <= 160 + BTN_WIDTH) {
                System.out.println("UI: Reiniciar clicado (Lógica a ser implementada)");
            }
            
            // Verificou clique no botão Vermelho (Sair)
            else if (mouseX >= screenWidth - 140 && mouseX <= screenWidth - 140 + BTN_WIDTH) {
                System.out.println("UI: Saindo do simulador...");
                GLFW.glfwSetWindowShouldClose(windowHandle, true);
            }
        }
    }

    // Utilitário rápido para criar cores no formato que o NanoVG exige
    private NVGColor rgba(int r, int g, int b, int a) {
        NVGColor color = NVGColor.create();
        NanoVG.nvgRGBA((byte)r, (byte)g, (byte)b, (byte)a, color);
        return color;
    }
}