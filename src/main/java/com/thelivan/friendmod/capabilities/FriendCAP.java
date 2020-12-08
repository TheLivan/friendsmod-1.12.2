package com.thelivan.friendmod.capabilities;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import com.thelivan.Division;
import com.thelivan.friendmod.FriendMod;
import com.thelivan.friendmod.network.PackageFriendPropertySync;
import com.thelivan.friendmod.network.PackageMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class FriendCAP implements IFriendCAP {

	public static final String NAME = CapabilityHandler.FRIENDCAP_NAME.toString();	
	private HashMap<UUID, String> friends = new HashMap<>();

	@Override
	public void addFriend(String playerName, UUID playerID, String friendName, UUID friendID) {
		if(!Division.SERVER) return;
		if (contains(friendID)) return;
		if (FriendMod.timehandler.contains(friendID, playerID)) {
			EntityPlayer friendPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getPlayerEntityByName(friendName);
			if (friendPlayer == null) return;
			FriendMod.timehandler.remove(friendID, playerID);
			friends.put(friendID, friendName);
			IFriendCAP cap = friendPlayer.getCapability(FriendProvider.FRIEND_CAP, null);
			cap.addFriendForcibly(playerName, playerID);
		} else {
			if(!FriendMod.timehandler.containsOneArg(friendID)){
				FriendMod.timehandler.add(playerID, friendID);
				EntityPlayer friendPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getPlayerEntityByName(friendName);
				if (friendPlayer == null) return;
				FriendMod.network.sendTo(new PackageMessage(playerName), (EntityPlayerMP) friendPlayer);
			}else {
				FriendMod.timehandler.add(playerID, friendID);
			}
		}
	}

	@Override
	public void addFriendForcibly(String friendName, UUID friendID) {
		if(!Division.SERVER) return;
		if (friends.containsKey(friendID))
			return;

		friends.put(friendID, friendName);	
	}

	@Override
	public void removeFriend(String playerName, UUID playerID, String friendName, UUID friendID) {
		if(!Division.SERVER) return;
		if (!contains(friendID)) return;
		EntityPlayer friendPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getPlayerEntityByUUID(friendID);
		if (friendPlayer != null) {	
			IFriendCAP cap = friendPlayer.getCapability(FriendProvider.FRIEND_CAP, null);
			cap.removeFriendForcibly(playerName, playerID);
			cap.markDirty(friendPlayer);
		} else {
			try {
				String path = FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0].getSaveHandler().getWorldDirectory().getPath() + "/playerdata/";
				File file = new File(path, friendID.toString().toLowerCase() + ".dat");
	            if (file.getParentFile() != null) file.getParentFile().mkdirs();
	            FileInputStream fileinputstream = new FileInputStream(file);
	            NBTTagCompound data = CompressedStreamTools.readCompressed(fileinputstream);
	            NBTTagCompound tag = data.getCompoundTag("ForgeCaps").getCompoundTag(NAME);
	            fileinputstream.close();
	            
				FriendCAP cap = new FriendCAP();
				cap.loadNBTData(tag);
				cap.removeFriendForcibly(playerName, playerID);
				tag = (NBTTagCompound) cap.saveNBTData();
				data.getCompoundTag("ForgeCaps").removeTag(NAME);
				data.getCompoundTag("ForgeCaps").setTag(NAME, tag);
				
				FileOutputStream fileoutputstream = new FileOutputStream(file);
                CompressedStreamTools.writeCompressed(data, fileoutputstream);
                fileoutputstream.close();
	            
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		friends.remove(friendID, friendName);
	}

	@Override
	public void removeFriendForcibly(String friendName, UUID friendID) {
		if(!Division.SERVER) return;
		if (!contains(friendID)) return;
		friends.remove(friendID);
	}

	@Override
	public boolean contains(UUID ID) {
		return friends.containsKey(ID);
	}

	@Override
	public HashMap<UUID, String> getFriends() {
		return this.friends;
	}

	@Override
	public void resetFriends() {
		friends = new HashMap<>();
	}

	@Override
	public void checkUpdate() {
	}

	@Override
	public void markDirty(EntityPlayer player) {
		if (!player.world.isRemote){
            try {
                FriendMod.network.sendTo(new PackageFriendPropertySync(saveNBTData(), player.getEntityId()), (EntityPlayerMP) player);
            }
            catch (NoSuchMethodError e){
                e.printStackTrace();
            }
        }
	}

	@Override
	public String getName() {
		return this.NAME;
	}

	@Override
	public NBTBase saveNBTData() {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagCompound data = new NBTTagCompound();

		NBTTagList list = new NBTTagList();
		List<UUID> keys = new ArrayList<>(friends.keySet());
		for (UUID ID : keys) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("friendID", ID.toString());
			tag.setString("friendName", friends.get(ID));
			list.appendTag(tag);
		}
		data.setTag("friends", list);
		compound.setTag(NAME, data);
		return compound;
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		if (compound.hasKey(NAME, Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound data = compound.getCompoundTag(NAME);
			resetFriends();
			NBTTagList list = (NBTTagList) data.getTag("friends");
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				friends.put(UUID.fromString(tag.getString("friendID")), tag.getString("friendName"));
			}
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				checkUpdate();
		}
	}
}
