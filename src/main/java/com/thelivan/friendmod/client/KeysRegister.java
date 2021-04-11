package com.thelivan.friendmod.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeysRegister {

	public static String category = "Friend Keys";
	public static KeyBinding addFriend, friendMenu;

	public static void init() {
		addFriend = new KeyBinding("Add friend", Keyboard.KEY_G, category);
		ClientRegistry.registerKeyBinding(addFriend);
		friendMenu = new KeyBinding("Friend Menu", Keyboard.KEY_P, category);
		ClientRegistry.registerKeyBinding(friendMenu);
	}
}
