package com.thelivan.friendmod.network;

import com.thelivan.friendmod.capabilities.FriendProvider;
import com.thelivan.friendmod.capabilities.IFriendCAP;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PackageFriendPropertySync implements IMessage {

	public NBTTagCompound data;
	 public int playerId;

	public PackageFriendPropertySync() {
	}

	public PackageFriendPropertySync(NBTBase data, int id) {
		this.data = (NBTTagCompound) data;
		playerId = id;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data);
		buf.writeInt(playerId);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = ByteBufUtils.readTag(buf);
		playerId = buf.readInt();
	}

	public static class Handler implements IMessageHandler<PackageFriendPropertySync, IMessage> {

		@Override
		public IMessage onMessage(PackageFriendPropertySync packet, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable(){  public void run() {
                World world = FMLClientHandler.instance().getClient().world;
                if (world==null) return;
                Entity p = world.getEntityByID(packet.playerId);
                if (p !=null && p instanceof EntityPlayer) {
                	NBTTagCompound data = packet.data;
        			IFriendCAP cap = p.getCapability(FriendProvider.FRIEND_CAP, null);
        			cap.loadNBTData(data);
                }
            }});
            return null;
		}
	}
}
