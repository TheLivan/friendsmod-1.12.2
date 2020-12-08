package com.thelivan.friendmod.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class FriendProvider implements ICapabilitySerializable<NBTBase> {
	
	@CapabilityInject(IFriendCAP.class)
	public static final Capability<IFriendCAP> FRIEND_CAP = null;

	private IFriendCAP instance = FRIEND_CAP.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == FRIEND_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == FRIEND_CAP ? FRIEND_CAP.<T>cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return FRIEND_CAP.getStorage().writeNBT(FRIEND_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		FRIEND_CAP.getStorage().readNBT(FRIEND_CAP, this.instance, null, nbt);
	}

}
