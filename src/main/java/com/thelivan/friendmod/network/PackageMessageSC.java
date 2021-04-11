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

public class PackageMessageSC implements IMessage, IMessageHandler<PackageMessageSC, IMessage> {
	public String playerName;
	
	public PackageMessageSC() { }
	
	public PackageMessageSC(String playerName) {
		this.playerName = playerName;
	}
	
	@Override
	public IMessage onMessage(PackageMessageSC packet, MessageContext message) {
		String playerName = packet.playerName;
		clientPlayer().sendMessage(new TextComponentString(I18n.format("friendmod.language.pmessage", TextFormatting.DARK_AQUA)+playerName+I18n.format("friendmod.language.addmessage", TextFormatting.DARK_AQUA)));
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		playerName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, playerName);
	}

	@SideOnly(Side.CLIENT)
	private static EntityPlayer clientPlayer() {
		return Minecraft.getMinecraft().player;
	}
}
