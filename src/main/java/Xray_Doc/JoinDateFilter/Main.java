package Xray_Doc.JoinDateFilter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.multiplayer.ServerData;

@Mod(modid=Main.MOD_ID, name=Main.NAME, version=Main.VERSION)

public class Main {
	
	public static final String MOD_ID="joindatefilter";
	public static final String NAME="Join Date Filter";
	public static final String VERSION="1.0.4";
	
	@EventHandler
	public void init(FMLInitializationEvent event) throws IOException
	{
		FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        createFile initFile= new createFile();
        initFile.createDataFiles();
	}
	
	String name = null;
	String date = null;
	int playercheck=1;
    writeJoinDate newDate=new writeJoinDate();
	getJoinDate searchDate=new getJoinDate();
	int filter=0;
	int server=0;
    
	@SubscribeEvent
	public void clientH(ClientConnectedToServerEvent event) throws IOException, InterruptedException {
		server=0;
		String ip = Minecraft.getMinecraft().getCurrentServerData().serverIP;
		if (ip.equalsIgnoreCase("constantiam.net")) {
			server=1;
		}
	}
	
    @SubscribeEvent
	public void clientChat(ClientChatReceivedEvent event) throws IOException, InterruptedException {
    	
		if(config.mod_enabled && server==1) {
				
			String message = event.getMessage().getUnformattedText();
			String myName=Minecraft.getMinecraft().player.getName();
			
			if(message.contains("<"+myName+">") || 
					message.contains("[P] <") || 
					(message.contains(" whispers: ") && !message.contains("<")) || 
					(message.contains("[server]") && !message.contains("<"))) {
				
			}
			
			else if(message.contains("<")) {
				playercheck=0;
				int startIndex = message.indexOf("<");
				int endIndex = message.indexOf(">");
				name=message.substring(startIndex+1,endIndex);
			
				filter=searchDate.findJoinDate(name);
				
				if(filter==1) {
					playercheck=1;
					filter=0;
					event.setMessage(null);
				}
				if(filter==2) {
					playercheck=1;
				}
			}
			
			else if(message.contains("Have they joined before?")) {
				LocalDateTime currentDate = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				String strCurrentDate = currentDate.format(formatter);
				
				newDate.newDateAppend(name,strCurrentDate);
				playercheck=1;
				event.setMessage(null);
			}
			
			else if(message.contains("Player: ") && playercheck==0) {
				int index=message.lastIndexOf(":");
				name=message.substring(index+2);
				event.setMessage(null);
			}
			
			else if(message.contains("First Joined: ") && playercheck==0) {
				int index=message.indexOf(":");
				date=message.substring(index+2,index+12);
				event.setMessage(null);
				newDate.newDateAppend(name,date);
			}
			
			else if(message.contains("Last Seen: ") && playercheck==0) {
				playercheck=1;
				event.setMessage(null);
			}
		}
	}
}
