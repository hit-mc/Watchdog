package com.keuin.watchdog;

import com.keuin.watchdog.monitor.Monitor;
import com.keuin.watchdog.monitor.plugin.ItemAutoCleanerPlugin;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.Collections;

public class WatchdogMod implements DedicatedServerModInitializer {
	public static MinecraftServer server;
	@Override
	public void onInitializeServer() {
		Monitor.initialize(Collections.singletonList(new ItemAutoCleanerPlugin()));
		ServerLifecycleEvents.SERVER_STARTED.register(server -> WatchdogMod.server = server);
	}
}
