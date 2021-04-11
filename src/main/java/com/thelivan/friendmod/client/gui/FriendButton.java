package com.thelivan.friendmod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public abstract class FriendButton extends Gui {
    private Minecraft mc; 
    private int x;
    private int y;
    private int width;
    private int height;
    private int borderWidth;
    private int borderColor;
    private int backgroundColor;
    private int textColor;
    private String text;

    private boolean isHover;
    private int timer;

    public FriendButton(int x, int y, String text){
        this(x, y, 120, 25, 2, 0x808080, 0x191919, 0xE5E5E5, text);
    }

    public FriendButton(int x, int y, int width, int height, int borderWidth, int borderColor, int backgroundColor, int textColor, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.text = text;
        this.mc = Minecraft.getMinecraft();
    }

    public void draw(int mouseX, int mouseY){
        isHover = mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        int factor = new ScaledResolution(mc).getScaleFactor();
        Tessellator tes = Tessellator.getInstance();
        RenderHelper.enableGUIStandardItemLighting();
        // Ниже обводка. Это было красиво, но мне сказали это убрать, но всё же, я оставлю это сдесь
        /*if (timer > 0) {
            GL11.glLineWidth(borderWidth);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tes.startDrawingQuads();
            tes.setColorRGBA_I(backgroundColor, 175);
            tes.addVertex(x, y, 0);
            tes.addVertex(x, y + height, 0);
            tes.addVertex(x + width, y + height, 0);
            tes.addVertex(x + width, y, 0);
            tes.draw();

            int localX = x + borderWidth * factor / 4;
            int localY = y + borderWidth * factor / 4;
            int localWidth = width - borderWidth * factor + borderWidth;
            int localHeight = height - borderWidth * factor + borderWidth;

            tes.startDrawing(GL11.GL_LINE_LOOP);
            tes.setColorRGBA_I(borderColor, 255);
            tes.addVertex(localX, localY, 0);
            tes.addVertex(localX, localY + localHeight, 0);
            tes.addVertex(localX + localWidth, localY + localHeight, 0);
            tes.addVertex(localX + localWidth, localY, 0);
            tes.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }*/ 
        int localX = width/2 - mc.fontRenderer.getStringWidth(text)/2;
        int localY = height/2 - mc.fontRenderer.FONT_HEIGHT/2;
        mc.fontRenderer.drawString(text, localX, localY, textColor);
    }

    public boolean mouseClick(){
        if (isHover){
            if (timer > 0){
                timer = 0;
                onMouseClick(text);
                return true;
            }
            else {
                timer = 20;
                firstClick(text);
            }
        }
        return false;
    }

    public String getText(){return text;}
    public abstract void firstClick(String text);
    public abstract void onMouseClick(String text);

    public void update(){
        timer--;
    }

    public void resetTimer(){
        timer = 0;
    }
}
