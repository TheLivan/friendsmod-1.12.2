package com.thelivan.friendmod.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.thelivan.friendmod.FriendMod;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class TimeHandler {
	
	private List<AddArgs> timeMap = new ArrayList<>();
	
	private static class AddArgs {
		private UUID player;
		private UUID friend;
		private int timer;

		public AddArgs(UUID player, UUID friend) {
			this.player = player;
			this.friend = friend;
			this.timer = 500;
		}

		private boolean tick() {
			timer -= 1;
			return timer < 0;
		}
		
		private static boolean contains(List<AddArgs> list, UUID player, UUID friend){
            for (AddArgs args : list)
                if (args.player.equals(player) && args.friend.equals(friend))
                    return true;
            return false;
        }
		
		private static boolean containsOneArg(List<AddArgs> list,UUID friend){
            for (AddArgs args : list)
                if (args.friend.equals(friend))
                    return true;
            return false;
        }

        private static void remove(List<AddArgs> list, UUID player, UUID friend){
            List<AddArgs> toRemove = new ArrayList<>();
            for (AddArgs args : list)
                if (args.player.equals(player) && args.friend.equals(friend))
                    toRemove.add(args);
            list.removeAll(toRemove);
        }
	}
	public void add(UUID playerID, UUID friendID) {
		timeMap.add(new AddArgs(playerID, friendID));
	}
	
	public boolean contains(UUID player, UUID friend) {
		return AddArgs.contains(timeMap, player, friend);
	}
	
	public boolean containsOneArg(UUID friend) {
		return AddArgs.containsOneArg(timeMap, friend);
	}
	
	public void remove(UUID player, UUID friend) {
		AddArgs.remove(timeMap, player, friend);
	}
	
	public void tick() {
		//System.out.println(timeMap);
        if (FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld() != null){
            List<AddArgs> toRemove = new ArrayList<>();
            for (AddArgs args : timeMap)
                if (args.tick()) {
                    toRemove.add(args);
                }
            timeMap.removeAll(toRemove);
        }
	}
}
