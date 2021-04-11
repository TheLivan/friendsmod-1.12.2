package com.thelivan.friendmod.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.thelivan.friendmod.FriendMOD;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class StorageFriends implements IStorage<IFriendCAP>{
	@Override
	public NBTBase writeNBT(Capability<IFriendCAP> capability, IFriendCAP instance, EnumFacing side) {
		return instance.saveNBTData();
	}

	@Override
	public void readNBT(Capability<IFriendCAP> capability, IFriendCAP instance, EnumFacing side, NBTBase nbt) {
		instance.loadNBTData((NBTTagCompound) nbt);
	}
}
