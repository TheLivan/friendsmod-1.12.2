package com.thelivan.friendmod.network;

import java.util.UUID;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PackageMessage implements IMessage {
	
	String playerName;
	
	public PackageMessage() {}
	
	public PackageMessage(String playerName) {
		this.playerName = playerName;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		playerName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, playerName);
	}
	
	public static class Handler implements IMessageHandler<PackageMessage, IMessage> {

		@Override
		public IMessage onMessage(PackageMessage packet, MessageContext message) {
			if (message.side.isClient())
				act(packet);
			else
				act(message.getServerHandler().player, packet);
			return null;
		}

		private void act(PackageMessage packet) {
			String playerName = packet.playerName;
			clientPlayer().sendMessage(new TextComponentString(I18n.format("friendmod.language.pmessage", TextFormatting.DARK_AQUA)+playerName+I18n.format("friendmod.language.addmessage", TextFormatting.DARK_AQUA)));
		}

		private void act(EntityPlayerMP p, PackageMessage packet) {}
	}

	@SideOnly(Side.CLIENT)
	private static EntityPlayer clientPlayer() {
		return Minecraft.getMinecraft().player;
	}
}
