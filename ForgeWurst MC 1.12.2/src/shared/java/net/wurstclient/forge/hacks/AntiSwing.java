/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.server.SPacketAnimation;
import net.wurstclient.forge.utils.ChatUtils;

public final class AntiSwing extends Hack {
	public AntiSwing() {
		super("AntiSwing", "Cancels animation packets server-side.");
		setCategory(Category.COMBAT);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);

	}


	@SubscribeEvent
	public void onPacketOutput(WPacketOutputEvent event) {
		if (event.getPacket() instanceof CPacketAnimation)
			event.setCanceled(true);
		if (event.getPacket() instanceof SPacketAnimation)
			event.setCanceled(true);
	}


	@SubscribeEvent
	public void onPacketInput(WPacketInputEvent event) {
		if (event.getPacket() instanceof CPacketAnimation)
			event.setCanceled(true);
		if (event.getPacket() instanceof SPacketAnimation)
			event.setCanceled(true);
	}
}