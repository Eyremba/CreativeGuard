package creativeguard;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSearchAndSeizure implements CommandExecutor
{
	private CreativeGuard creativeGuard;

	public CommandSearchAndSeizure(CreativeGuard creativeGuard)
	{
		this.creativeGuard = creativeGuard;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!sender.hasPermission(cmd.getPermission()))
		{
			if(!creativeGuard.getConfig().getString("messageCommandNoPermission").equals(""))
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', creativeGuard.getConfig().getString("messageCommandNoPermission")));
			return true;
		}

		Material material = null;
		if(args.length == 1)
		{
			material = Material.getMaterial(args[0].toUpperCase());
		}

		if(args.length == 1 && material == null)
		{
			if(!creativeGuard.getConfig().getString("messageInvalidMaterialType").equals(""))
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', creativeGuard.getConfig().getString("messageInvalidMaterialType")));
			return true;
		}

		if(material != null)
			sender.sendMessage(ChatColor.GREEN + "Searching and sizing (online players only) "+material.toString()+"...");
		else
			sender.sendMessage(ChatColor.GREEN + "Checking online players for invalid items...");

		for(Player player : creativeGuard.getServer().getOnlinePlayers())
		{
			// inventory
			for(ItemStack itemStack : player.getInventory())
			{
				if((material != null && itemStack != null && itemStack.getType() == material) || creativeGuard.isInvalidItem(player, itemStack))
				{
					player.getInventory().remove(itemStack);
					creativeGuard.getServer().broadcast(ChatColor.RED + "Removed invalid items ("+itemStack.getType().toString()+") from "+player.getName(), creativeGuard.PERMISSION_NOTIFY);
				}
				player.updateInventory();
			}
			// enderchest
			for(ItemStack itemStack : player.getEnderChest())
			{
				if((material != null && itemStack != null && itemStack.getType() == material) || creativeGuard.isInvalidItem(player, itemStack))
				{
					player.getEnderChest().remove(itemStack);
					creativeGuard.getServer().broadcast(ChatColor.RED + "Removed invalid items ("+itemStack.getType().toString()+") from "+player.getName()+" (EnderChest)", creativeGuard.PERMISSION_NOTIFY);
				}
			}
		}

		return true;
	}
}
