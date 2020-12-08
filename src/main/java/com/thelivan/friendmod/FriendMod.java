package com.thelivan.friendmod;

import com.thelivan.friendmod.capabilities.CapabilityHandler;
import com.thelivan.friendmod.capabilities.FriendCAP;
import com.thelivan.friendmod.capabilities.IFriendCAP;
import com.thelivan.friendmod.capabilities.StorageFriends;
import com.thelivan.friendmod.capabilities.TimeHandler;
import com.thelivan.friendmod.client.Keys;
import com.thelivan.friendmod.event.ClientEvent;
import com.thelivan.friendmod.gui.ModGuiHandler;
import com.thelivan.friendmod.network.PackageAddFriend;
import com.thelivan.friendmod.network.PackageFriendPropertySync;
import com.thelivan.friendmod.network.PackageMessage;
import com.thelivan.friendmod.network.PackageRemoveFriend;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FriendMod.MODID, name = FriendMod.NAME, version = FriendMod.VERSION)
public class FriendMod {
	
	public static final String MODID = "friendmod";
    public static final String NAME = "Friend Mod";
    public static final String VERSION = "2.0";
    public static final String CLIENT_PROXY_CLASS = "com.thelivan.friendmod.client.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.thelivan.friendmod.CommonProxy";
    
    public static TimeHandler timehandler = new TimeHandler();
    
    @Mod.Instance()
    public static FriendMod instance;
    
    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper network; 
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
    	this.network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		this.network.registerMessage(PackageAddFriend.Handler.class, PackageAddFriend.class, 0, Side.CLIENT);
		this.network.registerMessage(PackageAddFriend.Handler.class, PackageAddFriend.class, 0, Side.SERVER);
		
		this.network.registerMessage(PackageRemoveFriend.Handler.class, PackageRemoveFriend.class, 1, Side.CLIENT);
		this.network.registerMessage(PackageRemoveFriend.Handler.class, PackageRemoveFriend.class, 1, Side.SERVER);
		
		this.network.registerMessage(PackageFriendPropertySync.Handler.class, PackageFriendPropertySync.class, 2, Side.CLIENT);
		this.network.registerMessage(PackageMessage.Handler.class, PackageMessage.class, 3, Side.CLIENT);
		
    	proxy.preInit(e);
    }
     
    @EventHandler
    public void init(FMLInitializationEvent e) {
    	CapabilityManager.INSTANCE.register(IFriendCAP.class, new StorageFriends(), FriendCAP.class);
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());
    	proxy.init(e);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
		if(FMLCommonHandler.instance().getSide().isClient()){
			MinecraftForge.EVENT_BUS.register(new ClientEvent());
			Keys.load();
		}
		
    	proxy.postInit(e);
    }
}
