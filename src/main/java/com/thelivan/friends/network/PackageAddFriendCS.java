package com.thelivan.friends.network;

import java.util.UUID;

import com.thelivan.Division;
import com.thelivan.friends.capabilities.FriendProvider;
import com.thelivan.friends.capabilities.IFriendCAP;

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

public class PackageAddFriendCS implements IMessage, IMessageHandler<PackageAddFriendCS, IMessage> {
	private String friendName;
	private UUID friendID;

	public PackageAddFriendCS() { }

	public PackageAddFriendCS(UUID friendID, String friendName) {
		this.friendID = friendID;
		this.friendName = friendName;
	}

	@Override
	public IMessage onMessage(PackageAddFriendCS packet, MessageContext message) {
		if (!Division.SERVER) return null;
		EntityPlayer p = message.getServerHandler().player;
		EntityPlayer playerToAdd = p.world.getPlayerEntityByName(packet.friendName);
		if (playerToAdd != null) {
			IFriendCAP cap = p.getCapability(FriendProvider.FRIEND_CAP, null);
			cap.addFriend(p.getDisplayName().getFormattedText(), p.getUniqueID(), packet.friendName, packet.friendID);
			cap.markDirty(p);
			IFriendCAP caps = playerToAdd.getCapability(FriendProvider.FRIEND_CAP, null);
			caps.markDirty(playerToAdd);
		}
		return null;
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
}
