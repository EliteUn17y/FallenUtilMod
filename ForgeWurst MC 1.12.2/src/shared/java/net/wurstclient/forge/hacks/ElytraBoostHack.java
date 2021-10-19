/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.PlayerUtils;

public final class ElytraBoostHack extends Hack {
	private final SliderSetting speed =
			new SliderSetting("BaseSpeed", 1, 0.0001, 6, 0.001, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting down =
			new SliderSetting("DownSpeed", 1, 0.0001, 6, 0.001, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting up =
			new SliderSetting("UpSpeed", 1, 0.0001, 6, 0.001, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting ncp =
			new CheckboxSetting("NCP-Strict",
					false);

	private final CheckboxSetting still =
			new CheckboxSetting("Take no velocity",
					false);

	private final CheckboxSetting auto =
			new CheckboxSetting("AutoForward",
					false);

	private final CheckboxSetting packet =
			new CheckboxSetting("PacketStrict",
					false);


	private final SliderSetting animations =
			new SliderSetting("Animation Limb", 1, 0.01, 6, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	public ElytraBoostHack() {
		super("ElytraBoost", "Elytra fly without slowing down.");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
		addSetting(up);
		addSetting(down);
		addSetting(ncp);
		addSetting(animations);
		addSetting(still);
		addSetting(auto);
		addSetting(packet);
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
	public void onUpdate(WUpdateEvent event) {
		if (!Minecraft.getMinecraft().player.isElytraFlying()) return;
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		float pitch = Minecraft.getMinecraft().player.rotationPitch;
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() && !packet.isChecked()) {
			Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * speed.getValue();
			Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * speed.getValue();
		}
		if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() && !packet.isChecked())
			Minecraft.getMinecraft().player.motionY += up.getValue();
		if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown() && !packet.isChecked())
			Minecraft.getMinecraft().player.motionY -= down.getValue();

		if (ncp.isChecked()) {
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, PlayerUtils.GetLocalPlayerPosFloored(), EnumFacing.DOWN));
		}
		mc.player.limbSwingAmount += animations.getValue();

		if (still.isChecked()) {
			if (!Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() && !Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() && !Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
				mc.player.setVelocity(0, 0, 0);
		}

		if (auto.isChecked() && !packet.isChecked()) {
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
		}

		if (packet.isChecked()) {
			if (mc.player.onGround) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
			}
			if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
				Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * speed.getValue();
				Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * speed.getValue();
			} else
				mc.player.setVelocity(0, 0, 0);
		}
	}
}