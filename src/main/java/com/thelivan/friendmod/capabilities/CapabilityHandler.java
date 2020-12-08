package com.thelivan.friendmod.capabilities;

import java.util.ArrayList;
import java.util.List;

import com.thelivan.friendmod.FriendMod;
import com.thelivan.friendmod.network.PackageFriendPropertySync;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CapabilityHandler {

	public static final ResourceLocation FRIENDCAP_NAME = new ResourceLocation(FriendMod.MODID, "friendcap");

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent event) {
		if (!(event.getObject() instanceof EntityPlayer)) return;

		event.addCapability(FRIENDCAP_NAME, new FriendProvider());
	}

	@SubscribeEvent
	public void onClonePlayer(PlayerEvent.Clone e) {
		NBTTagCompound compound = new NBTTagCompound();
		IFriendCAP caporiginal = e.getOriginal().getCapability(FriendProvider.FRIEND_CAP, null);
		IFriendCAP cap = e.getEntityPlayer().getCapability(FriendProvider.FRIEND_CAP, null);
		caporiginal.saveNBTData();
		cap.loadNBTData(compound);
		cap.markDirty(e.getEntityPlayer());

	}

	@SubscribeEvent
	public void playerStartedTracking(PlayerEvent.StartTracking e) {
		IFriendCAP cap = e.getTarget().getCapability(FriendProvider.FRIEND_CAP, null);
		if (cap != null)
			FriendMod.network.sendTo(new PackageFriendPropertySync(cap.saveNBTData(), e.getTarget().getEntityId()),(EntityPlayerMP) e.getEntityPlayer());
	}

	@SubscribeEvent
	public void entityJoinWorld(EntityJoinWorldEvent e) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT || !(e.getEntity() instanceof EntityPlayer)) return;

		EntityPlayer player = (EntityPlayer) e.getEntity();
		IFriendCAP cap = player.getCapability(FriendProvider.FRIEND_CAP, null);
		if (cap != null && player != null)
			FriendMod.network.sendTo(new PackageFriendPropertySync(cap.saveNBTData(), player.getEntityId()), (EntityPlayerMP) player);
	}

	@SubscribeEvent
	public void PlayerTickEvent(PlayerTickEvent e) {
		if (e.player.world.isRemote) return;
		FriendMod.timehandler.tick();
	}
}
