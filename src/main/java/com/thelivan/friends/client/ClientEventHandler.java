package com.thelivan.friends.client;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.opengl.GL11;

import com.thelivan.friends.FriendMOD;
import com.thelivan.friends.capabilities.FriendProvider;
import com.thelivan.friends.capabilities.IFriendCAP;
import com.thelivan.friends.client.gui.GuiFriendMenu;
import com.thelivan.friends.network.PackageAddFriendCS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class ClientEventHandler {
	private static final Minecraft MC = Minecraft.getMinecraft();
	
	private static final String toAddMessage = "[G] \u0432 \u0434\u0440\u0443\u0437\u044C\u044F";
	
	@SubscribeEvent
	public void Key(KeyInputEvent ev) {
		 EntityPlayer player = MC.player;
		if (KeysRegister.addFriend.isKeyDown()) {
			Entity entity = MC.objectMouseOver.entityHit;
	        if (entity != null && entity instanceof EntityPlayer) {
	        	EntityPlayer p = (EntityPlayer) entity;
	        	FriendMOD.network.sendToServer(new PackageAddFriendCS(p.getUniqueID(), p.getDisplayNameString()));
	            return;
	        }
		}
		if (KeysRegister.friendMenu.isKeyDown()) {
			MC.displayGuiScreen(new GuiFriendMenu());
		}
	}
	
    @SubscribeEvent
    public void renderNick(RenderLivingEvent.Specials.Pre ev) {
        if (ev.getEntity() instanceof EntityPlayer && ev.getEntity() != MC.player) {
            EntityPlayer player = MC.player;
            EntityPlayer renderPlayer = (EntityPlayer) ev.getEntity();
            IFriendCAP cap = player.getCapability(FriendProvider.FRIEND_CAP, null);
            
            boolean isFriend = cap.contains(renderPlayer.getUniqueID());
            if (isFriend) ev.setCanceled(true);
            if (!isEnabled(renderPlayer)) return;

            glAlphaFunc(GL_GREATER, 0.1F);
            
            double d3 = renderPlayer.getDistanceSq(player);
            
            if (d3 <= 4096) {
                String p_147906_2_ = renderPlayer.getDisplayNameString();
                FontRenderer fontrenderer = MC.fontRenderer;
                float f = 1.6F;
                float f1 = 0.016666668F * f;
                glPushMatrix();
                glTranslatef((float) ev.getX() + 0.0F, (float) ev.getY() + renderPlayer.height + 0.5F, (float) ev.getZ());
                glNormal3f(0.0F, 1.0F, 0.0F);
                glRotatef(-player.rotationYawHead, 0.0F, 1.0F, 0.0F);
                glRotatef(player.rotationPitch, 1.0F, 0.0F, 0.0F);
                glScalef(-f1, -f1, f1);
                glDisable(GL_LIGHTING);
                glDepthMask(false);
                glDisable(GL_DEPTH_TEST);
                glEnable(GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                Tessellator tes = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tes.getBuffer();
                byte b0 = 0;
                
                glDisable(GL_TEXTURE_2D);
                int j = fontrenderer.getStringWidth(p_147906_2_) / 2;
                
                boolean isHover = MC.objectMouseOver != null && MC.objectMouseOver.entityHit instanceof EntityPlayer && MC.objectMouseOver.entityHit == renderPlayer;
                
                glEnable(GL_TEXTURE_2D);
                
                if (isFriend)
                   fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, isFriend ? 0x00ff00 : 0xffd700);
                if (isHover && !isFriend){
                    GL11.glTranslatef(0, 0.25f/f1, 0);
                    fontrenderer.drawString(toAddMessage, -fontrenderer.getStringWidth(toAddMessage) / 2, b0, 0x99ffffff);
                }
                glEnable(GL_DEPTH_TEST);
                glDepthMask(true);
                glEnable(GL_LIGHTING);
                glEnable(GL_BLEND);
                glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                glPopMatrix();
            }
        }
		
    }
    
    private boolean isEnabled(EntityLivingBase entity) {
        return Minecraft.isGuiEnabled() && !entity.isInvisibleToPlayer(MC.player); //&& e.riddenByEntities == null
    }
}
