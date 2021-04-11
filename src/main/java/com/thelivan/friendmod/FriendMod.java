package com.thelivan.friendmod;

import com.thelivan.friendmod.capabilities.CapabilityHandler;
import com.thelivan.friendmod.capabilities.FriendCAP;
import com.thelivan.friendmod.capabilities.IFriendCAP;
import com.thelivan.friendmod.capabilities.StorageFriends;
import com.thelivan.friendmod.capabilities.TimeHandler;
import com.thelivan.friendmod.client.ClientEventHandler;
import com.thelivan.friendmod.client.KeysRegister;
import com.thelivan.friendmod.network.PackageAddFriendCS;
import com.thelivan.friendmod.network.PackageFriendPropertySyncSC;
import com.thelivan.friendmod.network.PackageMessageSC;
import com.thelivan.friendmod.network.PackageRemoveFriendCS;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FriendMOD.MODID, name = FriendMOD.NAME, version = FriendMOD.VERSION)
public class FriendMOD {
	
	public static final String MODID = "friendmod",
			NAME = "Friend Mod",
			VERSION = "2.1";
    
    public static TimeHandler timehandler = new TimeHandler();
    
    public static SimpleNetworkWrapper network; 
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
    	this.network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    	
		this.network.registerMessage(PackageAddFriendCS.class, PackageAddFriendCS.class, 0, Side.SERVER);
		this.network.registerMessage(PackageRemoveFriendCS.class, PackageRemoveFriendCS.class, 1, Side.SERVER);
		this.network.registerMessage(PackageFriendPropertySyncSC.class, PackageFriendPropertySyncSC.class, 2, Side.CLIENT);
		this.network.registerMessage(PackageMessageSC.class, PackageMessageSC.class, 3, Side.CLIENT);
    }
     
    @EventHandler
    public void init(FMLInitializationEvent e) {
    	CapabilityManager.INSTANCE.register(IFriendCAP.class, new StorageFriends(), FriendCAP.class);
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        
		if(FMLCommonHandler.instance().getSide().isClient()){
			MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
			KeysRegister.init();
		}
    }
}
