package creativeguard;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandCheckNBT implements CommandExecutor
{
	private CreativeGuard creativeGuard;

	public CommandCheckNBT(CreativeGuard creativeGuard)
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

		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "This command is available only ingame...");
			return true;
		}

		Player player = (Player) sender;

		ItemStack itemStack = player.getItemInHand();
		if(itemStack == null || itemStack.getType() == Material.AIR)
		{
			sender.sendMessage(ChatColor.GOLD + "You're holding something strange... or nothing at all.");
			return true;
		}

		String temp = "";
		try
		{
			NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(itemStack);
			for(String key : compound.getKeys())
			{
				if(!temp.equalsIgnoreCase(""))
					temp += ", ";
				temp += key;
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return false;
		}

		if(temp.equalsIgnoreCase(""))
			sender.sendMessage(ChatColor.GREEN + "Item in your hand ("+itemStack.getType().toString()+":"+itemStack.getDurability()+") has no NBT tags.");
		else
			sender.sendMessage(ChatColor.GOLD + "Item in your hand ("+itemStack.getType().toString()+":"+itemStack.getDurability()+") has the following NBT tags: " + ChatColor.RED + temp);

		return true;
	}
}
