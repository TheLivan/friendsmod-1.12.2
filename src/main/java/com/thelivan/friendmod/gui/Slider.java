package com.thelivan.friendmod.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Slider extends Gui {
    private int x;
    private int y;
    private int width;
    private int height;
    private int pageHeight;
    private int sliderHeight;
    private int pageOffset;
    private int sliderOffset;
    private int backgroundColor;
    private int sliderColor;
    private float activePosition;
    private float deltaFactor;
    private boolean isSliderHover;
    private boolean isHover;
    private int mouseY;

    public Slider(int x, int y, int pageHeight){
        this(x, y, 10, 250, 0x191919, 0xC0C0C0, pageHeight);
    }

    public Slider(int x, int y, int width, int height, int backgroundColor, int sliderColor, int pageHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.sliderColor = sliderColor;
        this.pageHeight = pageHeight;
        this.sliderHeight = pageHeight <= height ? height : height*height/pageHeight;
        this.deltaFactor = height - sliderHeight;
    }

    public void setHeight(int height){
        this.height = height;
        this.sliderHeight = pageHeight <= height ? height : height*height/pageHeight;
        this.deltaFactor = height - sliderHeight;
    }

    public int getOffset(){
        return pageOffset;
    }

    public void draw(int mouseX, int mouseY){
        isSliderHover = mouseX > x && mouseX < x + width && mouseY > y + sliderOffset && mouseY < y + sliderHeight + sliderOffset;
        isHover = mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        this.mouseY = mouseY;
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tes.getBuffer();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        //tes.startDrawingQuads();
       // tes.setColorRGBA_I(backgroundColor, 150);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y, 0).endVertex();
        bufferbuilder.pos(x, y+height, 0).endVertex();
        bufferbuilder.pos(x+width, y+height, 0).endVertex();
        bufferbuilder.pos(x+width, y, 0).endVertex();
        bufferbuilder.pos(x+width, y, 0).endVertex();
        tes.draw();
        //bufferbuilder.pos().endVertex();
        //tes.startDrawingQuads();
       // tes.setColorRGBA_I(sliderColor, 255);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, sliderOffset, 0).endVertex();
        bufferbuilder.pos(x, sliderOffset+sliderHeight, 0).endVertex();
        bufferbuilder.pos(x+width, sliderOffset+sliderHeight, 0).endVertex();
        bufferbuilder.pos(x+width, sliderOffset, 0).endVertex();
        tes.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glTranslatef(0, -pageOffset, 0);
    }

    public void mouseClick(){
        if (isHover && !isSliderHover){
            float newActivePosition = mouseY/deltaFactor - sliderHeight/deltaFactor/2f;
            if (newActivePosition < 0)
                activePosition = 0;
            else if (newActivePosition > 1)
                activePosition = 1;
            else
                activePosition = newActivePosition;

            pageOffset = (int)((pageHeight-height)*activePosition);
            sliderOffset = (int)((height-sliderHeight)*activePosition);
        }
    }

    public void mouseMove(){
        if(isSliderHover){
            float deltaY = -Mouse.getDY()/deltaFactor*1.5f;
            float newActivePosition = activePosition + deltaY;
            if (newActivePosition < 0)
                activePosition = 0;
            else if (newActivePosition > 1)
                activePosition = 1;
            else
                activePosition = newActivePosition;

            pageOffset = (int)((pageHeight-height)*activePosition);
            sliderOffset = (int)((height-sliderHeight)*activePosition);
        }
    }

    public void mouseWheel(){
        float deltaY = -Mouse.getDWheel()/deltaFactor/10f;
        if (deltaY == 0)
            return;

        float newActivePosition = activePosition + deltaY;
        if (newActivePosition < 0)
            activePosition = 0;
        else if (newActivePosition > 1)
            activePosition = 1;
        else
            activePosition = newActivePosition;

        pageOffset = (int)((pageHeight-height)*activePosition);
        sliderOffset = (int)((height-sliderHeight)*activePosition);
    }
}
