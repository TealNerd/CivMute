package com.biggestnerd.civmute;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="civmute", name="Civcraft Mute Utility", version="v1.0.0")
public class CivMute {

	private ArrayList<String> muted;
	private Minecraft mc = Minecraft.getMinecraft();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		muted = new ArrayList<String>();
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new MuteCommand());
	}
	
	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent event) {
		String msg = event.message.getUnformattedText();
		Pattern pm = Pattern.compile("^From ([A-Za-z0-9]+): .*$");
		Matcher pmMatcher = pm.matcher(msg);
		while(pmMatcher.find()) {
			String name = pmMatcher.group(1);
			if(muted.contains(name.toLowerCase())) {
				event.setCanceled(true);
				mc.thePlayer.sendChatMessage("/tell " + name + " Sorry, but I have you muted!");
			}
		}
		Pattern chat = Pattern.compile("^([A-Za-z0-9]+): .*$");
		Matcher chatMatcher = chat.matcher(msg);
		while(chatMatcher.find()) {
			String name = chatMatcher.group(1);
			if(muted.contains(name.toLowerCase())) {
				event.setCanceled(true);
			}
		}
	}
	
	class MuteCommand extends CommandBase {

		@Override
		public String getCommandName() {
			return "mute";
		}

		@Override
		public String getCommandUsage(ICommandSender p_71518_1_) {
			return "/mute <player>";
		}

		@Override
		public void processCommand(ICommandSender sender,
				String[] args) {
			if(args.length == 0) {
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Correct usage for the command is /mute <player>"));
				return;
			}
			if(muted.contains(args[0])) {
				muted.remove(args[0].toLowerCase());
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You have unmuted " + args[0]));
				return;
			} else {
				muted.add(args[0].toLowerCase());
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You have muted " + args[0]));
				return;
			}
		}
		
		@Override
		public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
			return true;
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 1;
		}
	}
}
