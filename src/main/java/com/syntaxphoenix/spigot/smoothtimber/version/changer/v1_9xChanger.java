package com.syntaxphoenix.spigot.smoothtimber.version.changer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.syntaxphoenix.spigot.smoothtimber.config.CutterConfig;
import com.syntaxphoenix.spigot.smoothtimber.utilities.Lists;
import com.syntaxphoenix.spigot.smoothtimber.utilities.Storage;
import com.syntaxphoenix.spigot.smoothtimber.version.manager.VersionChanger;
import com.syntaxphoenix.spigot.smoothtimber.version.manager.VersionExchanger;
import com.syntaxphoenix.spigot.smoothtimber.version.manager.WoodType;

@SuppressWarnings("deprecation")
public class v1_9xChanger implements VersionChanger {

	@Override
	public boolean hasCuttingItemInHand(Player player) {
		return CutterConfig.cutterMaterials.contains(getItemInHand(player).getType().name());
	}

	@Override
	public ItemStack removeDurabilityFromItem(ItemStack stack) {
		Integer dur = Integer.valueOf(stack.getDurability() + 1);
		if (stack.getType().getMaxDurability() < dur) {
			stack.setAmount(0);
			return null;
		}
		stack.setDurability(dur.shortValue());
		return stack;
	}

	@Override
	public void setItemInPlayerHand(Player player, ItemStack stack) {
		player.getEquipment().setItemInMainHand(stack);
	}

	@Override
	public boolean isWoodBlock(Block block) {
		return (block.getType().name().replace("_2", "").equals("LOG") || block.getType().name().equals("FENCE"));
	}

	@Override
	public void setupConfig() {
		CutterConfig.cutterMaterials
				.addAll(Lists.asList("WOOD_AXE", "STONE_AXE", "IRON_AXE", "GOLD_AXE", "DIAMOND_AXE"));
	}

	@Override
	public boolean hasPermissionForWood(Player p, Block b) {
		if (!CutterConfig.permissionsEnabled) {
			return true;
		}
		WoodType type = WoodType.OAK;
		Material mat = b.getType();
		int id = b.getData();
		if (mat == Material.valueOf("LOG")) {
			if (id == 1 || id == 5 || id == 9 || id == 13) {
				type = WoodType.SPRUCE;
			} else if (id == 2 || id == 6 || id == 10 || id == 14) {
				type = WoodType.BIRCH;
			} else if (id == 3 || id == 7 || id == 11 || id == 15) {
				type = WoodType.JUNGLE;
			}
		} else if (mat == Material.valueOf("LOG_2")) {
			if (id == 1 || id == 3 || id == 5 || id == 7 || id == 9 || id == 11 || id == 13 || id == 15) {
				type = WoodType.DARKOAK;
			} else if (id == 0 || id == 2 || id == 4 || id == 6 || id == 8 || id == 10 || id == 12 || id == 14) {
				type = WoodType.ACACIA;
			}
		}
		return VersionExchanger.checkPermission(type, p);
	}

	@Override
	public ItemStack getItemInHand(Player p) {
		return p.getEquipment().getItemInMainHand();
	}

	@Override
	public ItemStack getAirItem() {
		return new ItemStack(Material.AIR);
	}

	@Override
	public Entity toFallingBlock(Block block) {
		Material type = block.getType();
		byte data = block.getData();
		block.setType(Material.AIR);
		return block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, 0.2, 0.5), type, data);
	}

	@Override
	public EntityType getFallingBlockType() {
		return EntityType.FALLING_BLOCK;
	}

	@Override
	public void dropItemByFallingBlock(FallingBlock block) {
		block.getWorld().dropItemNaturally(block.getLocation(),
				new MaterialData((Material) Storage.MATERIAL.run("type", Storage.FALLING_BLOCK.run(block, "id")),
						(byte) Storage.FALLING_BLOCK.run(block, "data")).toItemStack());
	}

}