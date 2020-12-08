package com.thelivan.friendmod.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keys {
	
	public static String category = "Friend Keys";
	public static KeyBinding addfriend, removefriend;
	
	
	public static void load() 
	{
		addfriend = new KeyBinding("addfriend", Keyboard.KEY_G, category);
		removefriend = new KeyBinding("removefriend", Keyboard.KEY_P, category);
		
		ClientRegistry.registerKeyBinding(addfriend);
		ClientRegistry.registerKeyBinding(removefriend);
	}
}
