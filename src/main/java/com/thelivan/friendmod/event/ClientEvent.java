package com.thelivan.friendmod.event;


import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import com.thelivan.friendmod.FriendMod;
import com.thelivan.friendmod.capabilities.FriendProvider;
import com.thelivan.friendmod.capabilities.IFriendCAP;
import com.thelivan.friendmod.client.Keys;
import com.thelivan.friendmod.gui.ModGuiHandler;
import com.thelivan.friendmod.network.PackageAddFriend;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class ClientEvent {
	Minecraft mc = Minecraft.getMinecraft();
	
	private static final String toAddMessage = "[G] \u0432 \u0434\u0440\u0443\u0437\u044C\u044F";
	
	@SubscribeEvent
	public void Key(KeyInputEvent event) {
		 EntityPlayer player = mc.player;
		if (Keys.addfriend.isKeyDown()) {
			Entity e = mc.objectMouseOver.entityHit;
	        if (e != null && e instanceof EntityPlayer) {
	        	EntityPlayer p = (EntityPlayer) e;
	        	FriendMod.network.sendToServer(new PackageAddFriend(p.getUniqueID(), p.getDisplayNameString()));
	            return;
	        }
		}
		if (Keys.removefriend.isKeyDown()) {
			mc.player.openGui(FriendMod.instance, ModGuiHandler.FRIEND_SCREEN, mc.world, 0, 0, 0);
		}
	}
	
    @SubscribeEvent
    public void renderNick(RenderLivingEvent.Specials.Pre e) {
        if (e.getEntity() instanceof EntityPlayer && e.getEntity() != Minecraft.getMinecraft().player) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            EntityPlayer renderPlayer = (EntityPlayer) e.getEntity();
            IFriendCAP cap = player.getCapability(FriendProvider.FRIEND_CAP, null);
            
            boolean isFriend = cap.contains(renderPlayer.getUniqueID());
            if (isFriend)
                e.setCanceled(true);
            if (!isEnabled(renderPlayer))
                return;

            glAlphaFunc(GL_GREATER, 0.1F);
            
            double d3 = renderPlayer.getDistanceSq(player);
            
            if (d3 <= 4096) {
                String p_147906_2_ = renderPlayer.getDisplayNameString();
                FontRenderer fontrenderer = mc.fontRenderer;
                float f = 1.6F;
                float f1 = 0.016666668F * f;
                glPushMatrix();
                glTranslatef((float) e.getX() + 0.0F, (float) e.getY() + renderPlayer.height + 0.5F, (float) e.getZ());
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
                
                boolean isHover = mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityPlayer && mc.objectMouseOver.entityHit == renderPlayer;
                
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
    
    protected boolean isEnabled(EntityLivingBase e) {
        Minecraft mc = Minecraft.getMinecraft();
        return Minecraft.isGuiEnabled() && !e.isInvisibleToPlayer(mc.player);//&& e.riddenByEntities == null
    }
	
}
