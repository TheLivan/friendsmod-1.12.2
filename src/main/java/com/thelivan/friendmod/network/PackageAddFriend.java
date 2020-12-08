package com.thelivan.friendmod.network;

import java.util.UUID;

import com.thelivan.Division;
import com.thelivan.friendmod.capabilities.FriendProvider;
import com.thelivan.friendmod.capabilities.IFriendCAP;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PackageAddFriend implements IMessage {
	private String friendName;
	private UUID friendID;

	public PackageAddFriend() {}

	public PackageAddFriend(UUID friendID, String friendName) {
		this.friendID = friendID;
		this.friendName = friendName;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, friendName);
		ByteBufUtils.writeUTF8String(buf, friendID.toString());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		friendName = ByteBufUtils.readUTF8String(buf);
		friendID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	public static class Handler implements IMessageHandler<PackageAddFriend, IMessage> {

		@Override
		public IMessage onMessage(PackageAddFriend packet, MessageContext message) {
			if (message.side.isClient())
				act(packet);
			else
				act(message.getServerHandler().player, packet);
			return null;
		}

		private void act(PackageAddFriend packet) {}

		private void act(EntityPlayerMP p, PackageAddFriend packet) {
			String friendName = packet.friendName;
			UUID friendID = packet.friendID;
			if(!Division.SERVER) return;
			EntityPlayer playerToAdd = p.world.getPlayerEntityByName(friendName);
	        if (playerToAdd != null){
	        	IFriendCAP cap = p.getCapability(FriendProvider.FRIEND_CAP, null);
	        	cap.addFriend(p.getDisplayName().getFormattedText(), p.getUniqueID(), friendName, friendID);
	        	cap.markDirty(p);
	            IFriendCAP caps = playerToAdd.getCapability(FriendProvider.FRIEND_CAP, null);
	            caps.markDirty(playerToAdd);
	        }
		}
	}

	@SideOnly(Side.CLIENT)
	private static EntityPlayer clientPlayer() {
		return Minecraft.getMinecraft().player;
	}
}
