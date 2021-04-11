package com.thelivan.friends.network;

import java.util.UUID;

import com.thelivan.Division;
import com.thelivan.friends.FriendMOD;
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

public class PackageRemoveFriendCS implements IMessage, IMessageHandler<PackageRemoveFriendCS, IMessage> {
	public String friendName;
	public UUID friendID;

	public PackageRemoveFriendCS() { }

	public PackageRemoveFriendCS(UUID friendID, String friendName) {
		this.friendID = friendID;
		this.friendName = friendName;
	}

	@Override
	public IMessage onMessage(PackageRemoveFriendCS packet, MessageContext message) {
		if (!Division.SERVER) return null;
		EntityPlayer p = message.getServerHandler().player;
		IFriendCAP cap = p.getCapability(FriendProvider.FRIEND_CAP, null);
		cap.removeFriend(p.getDisplayNameString(), p.getUniqueID(), packet.friendName, packet.friendID);
		cap.markDirty(p);
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
