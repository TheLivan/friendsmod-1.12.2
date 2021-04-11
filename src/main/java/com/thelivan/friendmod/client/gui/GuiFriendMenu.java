package com.thelivan.friendmod.client.gui;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.thelivan.friendmod.FriendMOD;
import com.thelivan.friendmod.capabilities.FriendProvider;
import com.thelivan.friendmod.network.PackageRemoveFriendCS;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiFriendMenu extends GuiScreen {
    private List<FriendButton> buttons;
    private Slider slider;
    private GuiButton closeButton;
    private List<String> ignore;

    @Override
    public void initGui(){
        width = mc.displayWidth;
        height = mc.displayHeight;
        super.initGui();
        List<String> names = new ArrayList<>(mc.player.getCapability(FriendProvider.FRIEND_CAP, null).getFriends().values());
        buttons = new ArrayList<>();
        ignore = new ArrayList<>();
        for (String name : names)
            buttons.add(new FriendButton(0,0, name) {
                @Override
                public void firstClick(String text) {
                    for (FriendButton mas : buttons)
                        if (mas != null && !mas.getText().equals(text))
                            mas.resetTimer();
                }

                @Override
                public void onMouseClick(String text) {
                    FriendMOD.network.sendToServer(new PackageRemoveFriendCS(getKeyByValue(mc.player.getCapability(FriendProvider.FRIEND_CAP, null).getFriends(), text), text));
                }
            });

        slider = new Slider(120, 0, buttons.size()*25);
        closeButton = new GuiButton(0,0,260, "\u0417\u0430\u043a\u0440\u044b\u0442\u044c");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawDefaultBackground();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tes.getBuffer();
        ScaledResolution res = new ScaledResolution(mc);
        glEnable(GL_BLEND);

        if (buttons.size() != 0) {
            if (res.getScaledHeight() - 55 < buttons.size() * 25) {
                // Slider
                int tX = res.getScaledWidth() / 2 + 60;
                int tY = 15;
                glTranslatef(tX, tY, 0);
                slider.mouseWheel();
                slider.draw(mouseX - tX, mouseY - tY);
                slider.setHeight(res.getScaledHeight() - 55);
                glTranslatef(-tX, -tY, 0);
            }
            {   // Buttons
                glPushMatrix();
                int tX = res.getScaledWidth() / 2 - 60;
                int tY = res.getScaledHeight() - 55 > buttons.size() * 25 ? 15 + (res.getScaledHeight() - 55 - buttons.size() * 25) / 2 : 15;
                glTranslatef(tX, tY, 0);
                for (int i = 0; i < buttons.size(); i++) {
                    buttons.get(i).draw(mouseX - tX, mouseY - 25 * i - tY + slider.getOffset());
                    glTranslatef(0, 25, 0);
                }
                glPopMatrix();
                glTranslatef(0, slider.getOffset(), 0);
            }
        }
        {   // Frames
            glDisable(GL_TEXTURE_2D);
          //bufferbuilder.pos().endVertex();
           // tes.startDrawingQuads();
          //  tes.setColorRGBA_I(0x080808, 255);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0, 0, 0).endVertex();
            bufferbuilder.pos(0, 15, 0).endVertex();
            bufferbuilder.pos(res.getScaledWidth(), 15, 0).endVertex();
            bufferbuilder.pos(res.getScaledWidth(), 0, 0).endVertex();
            tes.draw();
            //tes.startDrawingQuads();
            //tes.setColorRGBA_I(0x080808, 255);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0, res.getScaledHeight() - 40, 0).endVertex();
            bufferbuilder.pos(0, res.getScaledHeight(), 0).endVertex();
            bufferbuilder.pos(res.getScaledWidth(), res.getScaledHeight(), 0).endVertex();
            bufferbuilder.pos(res.getScaledWidth(), res.getScaledHeight() - 40, 0).endVertex();
            tes.draw();
            glEnable(GL_TEXTURE_2D);
        }
        {   // Other
            closeButton.x = (res.getScaledWidth() - closeButton.getButtonWidth()) / 2;
            closeButton.y = res.getScaledHeight() - 30;
            closeButton.drawButton(mc, mouseX, mouseY, partialTicks);

            String str = "\u0414\u0432\u043e\u0439\u043d\u043e\u0439 \u043a\u043b\u0438\u043a \u043f\u043e \u043d\u0438\u043a\u0443 \u0434\u043b\u044f \u0443\u0434\u0430\u043b\u0435\u043d\u0438\u044f";
            fontRenderer.drawString(str, (res.getScaledWidth() - fontRenderer.getStringWidth(str)) / 2, 2, 0xffffff);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int p_73864_3_){
        FriendButton toRemove = null;
        for (FriendButton button : buttons){
            if (button.mouseClick())
                toRemove = button;
        }
        if (toRemove != null)
            buttons.remove(toRemove);

        slider.mouseClick();
        ScaledResolution res = new ScaledResolution(mc);
        mouseX /= res.getScaleFactor();
        mouseY /= res.getScaleFactor();
        if (closeButton.mousePressed(mc, mouseX, mouseY)){
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int p_146273_3_, long p_146273_4_) {
        slider.mouseMove();
    }

    @Override
    public void updateScreen() {
        for (FriendButton button : buttons){
            button.update();
        }
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
