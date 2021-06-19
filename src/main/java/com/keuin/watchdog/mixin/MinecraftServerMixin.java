package com.keuin.watchdog.mixin;

import com.keuin.watchdog.monitor.Monitor;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	private static final Deque<Long> deque = new LinkedList<>();
	@Inject(method = "tick", at = @At("HEAD"))
	protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		final long measureDuration = 1000L;
		final long currentMillis = Util.getMeasuringTimeMs();

		// we firstly put current time into queue, to make sure peek() won't return a null value
		deque.add(currentMillis);

		// remove elements out of the scope
		boolean isStable = false;
		while (currentMillis - deque.peek() > measureDuration) {
			isStable = true;
			deque.remove();
		}

		// if we have removed one or more elements,
		// the time scope is filled up and the
		// tick est. will be accurate enough.
		// otherwise if we do not perform this check,
		// the low tps at startup phase will be reported,
		// which is inaccurate.
		if (isStable) {
			// get the time elapsed in scope (both sides of the queue)
			long timeElapsed = currentMillis - deque.peek();
			// -1 to exclude current tick (left inclusive, right exclusive)
			var tps = (deque.size() - 1) * 1.0 / timeElapsed * 1000;
			if (Double.isNaN(tps))
				tps = 0;
			var monitor = Monitor.getInstance();
			if (monitor != null)
				monitor.accept(tps);
//			System.out.println("TPS: " + tps + " Time: " + timeElapsed + "ms Size: " + deque.size());
		}
	}
}
