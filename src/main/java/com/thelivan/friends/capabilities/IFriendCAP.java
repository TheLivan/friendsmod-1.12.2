package com.thelivan.friends.capabilities;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public interface IFriendCAP {
	 public void addFriend(String playerName, UUID playerID, String friendName, UUID friendID);
	 public void addFriendForcibly(String friendName, UUID friendID);
	 public void removeFriend(String playerName, UUID playerID, String friendName, UUID friendID);
	 public void removeFriendForcibly(String friendName, UUID friendID);
	 public boolean contains(UUID ID);
	 public HashMap<UUID, String> getFriends();
	 public void resetFriends();
	 void checkUpdate();
	 public String getName();
	 public void markDirty(EntityPlayer player);
	 
	 public NBTBase saveNBTData();
	 public void loadNBTData(NBTTagCompound compound);
}
