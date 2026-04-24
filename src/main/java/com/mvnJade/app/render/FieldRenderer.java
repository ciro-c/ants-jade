package com.mvnJade.app.render;

import org.lwjgl.opengl.GL11;

public class FieldRenderer {

    public void render(int width, int height) {
        // Define a cor para branco e aumenta a espessura da linha
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glLineWidth(2.0f);

        // Define as margens (para o campo não ficar colado na borda da janela)
        float margin = 30.0f;
        float topMargin = 60.0f; // Espaço extra no topo por causa da barra do NanoVG

        float leftX = margin;
        float rightX = width - margin;
        float topY = topMargin;
        float bottomY = height - margin;

        float centerX = width / 2.0f;
        float centerY = topMargin + ((height - topMargin - margin) / 2.0f);

        // 1. Contorno do Campo (Linhas laterais e de fundo)
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(leftX, topY);
        GL11.glVertex2f(rightX, topY);
        GL11.glVertex2f(rightX, bottomY);
        GL11.glVertex2f(leftX, bottomY);
        GL11.glEnd();

        // 2. Linha de Meio-Campo
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(centerX, topY);
        GL11.glVertex2f(centerX, bottomY);
        GL11.glEnd();

        // 3. Círculo Central (Raio de 50 pixels)
        drawCircle(centerX, centerY, 50.0f, 40);

        // 4. Grande Área - Esquerda
        float penaltyWidth = 100.0f;
        float penaltyHeight = 220.0f;
        float penaltyTopY = centerY - (penaltyHeight / 2.0f);
        float penaltyBottomY = centerY + (penaltyHeight / 2.0f);

        GL11.glBegin(GL11.GL_LINE_STRIP); // LINE_STRIP desenha linhas conectadas sem fechar o polígono
        GL11.glVertex2f(leftX, penaltyTopY);
        GL11.glVertex2f(leftX + penaltyWidth, penaltyTopY);
        GL11.glVertex2f(leftX + penaltyWidth, penaltyBottomY);
        GL11.glVertex2f(leftX, penaltyBottomY);
        GL11.glEnd();

        // 5. Grande Área - Direita
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(rightX, penaltyTopY);
        GL11.glVertex2f(rightX - penaltyWidth, penaltyTopY);
        GL11.glVertex2f(rightX - penaltyWidth, penaltyBottomY);
        GL11.glVertex2f(rightX, penaltyBottomY);
        GL11.glEnd();

        // Restaura a espessura padrão do OpenGL para não afetar outros desenhos
        GL11.glLineWidth(1.0f);
    }

    // Método auxiliar para desenhar círculos usando trigonometria
    private void drawCircle(float cx, float cy, float r, int numSegments) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i < numSegments; i++) {
            float theta = 2.0f * (float) Math.PI * (float) i / (float) numSegments; // Calcula o ângulo
            float x = r * (float) Math.cos(theta); // Ponto X na circunferência
            float y = r * (float) Math.sin(theta); // Ponto Y na circunferência
            GL11.glVertex2f(x + cx, y + cy);
        }
        GL11.glEnd();
    }
}