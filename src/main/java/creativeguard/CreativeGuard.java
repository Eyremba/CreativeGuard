package creativeguard;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class CreativeGuard extends JavaPlugin
{
	public static String PERMISSION_COMMAND_BLOCK 				= "creativeguard.commandblock";
	public static String PERMISSION_BARRIER 					= "creativeguard.barrier";
	public static String PERMISSION_NBT_CUSTOM_POTION_EFFECTS 	= "creativeguard.custompotioneffects";
	public static String PERMISSION_NBT_STORED_ENCHANTS 		= "creativeguard.storedenchants";
	public static String PERMISSION_NBT_HIDE_FLAGS 				= "creativeguard.hideflags";
	public static String PERMISSION_NBT_UNBREAKABLE 			= "creativeguard.unbreakable";
	public static String PERMISSION_NBT_ATTRIBUTE_MODIFIERS 	= "creativeguard.attributemodifiers";
	public static String PERMISSION_INVALID_ENCHANTS 			= "creativeguard.invalidenchants";
	public static String PERMISSION_INVALID_ANVIL_DAMAGE		= "creativeguard.invalidanvildamage";
	public static String PERMISSION_NETHER_PORTAL_TO_SPAWN 		= "creativeguard.netherportaltospawn";
	public static String PERMISSION_NOTIFY 						= "creativeguard.notify";

	@Override
	public void onEnable()
	{
		handleConfig();

		getServer().getPluginManager().registerEvents(new EventListnerer(this), this);
		getCommand("searchandseizure").setExecutor(new CommandSearchAndSeizure(this));
		getCommand("checknbt").setExecutor(new CommandCheckNBT(this));
	}

	@Override
	public void onDisable()
	{
		HandlerList.unregisterAll(this);
	}

	public boolean isInvalidItem(Player player, ItemStack itemStack)
	{
		if(itemStack == null)
			return false;

		if(itemStack.getType() == Material.AIR)
			return false;

		/*********************************************************/
		// check invalid items
		if(!player.hasPermission(PERMISSION_COMMAND_BLOCK))
			if(itemStack.getType() == Material.COMMAND || itemStack.getType() == Material.COMMAND_MINECART)
				return true;

		if(!player.hasPermission(PERMISSION_BARRIER))
			if(itemStack.getType() == Material.BARRIER)
				return true;

		/*********************************************************/
		// check item name
		ItemMeta itemMeta = itemStack.getItemMeta();
		if(itemMeta != null && itemMeta.getDisplayName() != null)
		{
			if(itemMeta.getDisplayName().length() > 30)
			{
				getServer().broadcast(ChatColor.RED + player.getName() + " caught with item ("+itemStack.getType().toString()+") with invalid name (length)!", PERMISSION_NOTIFY);
				return true;
			}
		}

		/*********************************************************/
		// check enchants

		if(!player.hasPermission(PERMISSION_INVALID_ENCHANTS))
		{
			for (Map.Entry<Enchantment, Integer> enchantent : itemStack.getEnchantments().entrySet())
			{
				if (enchantent.getValue() < 1 || enchantent.getValue() > enchantent.getKey().getMaxLevel())
				{
					getServer().broadcast(ChatColor.RED + player.getName() + " caught with item (" + itemStack.getType().toString() + ") with invalid enchants!", PERMISSION_NOTIFY);
					return true;
				}
			}
		}

		/*********************************************************/
		// check anvil data

		if(!player.hasPermission(PERMISSION_INVALID_ANVIL_DAMAGE))
		{
			if(itemStack.getType().equals(Material.ANVIL) && itemStack.getDurability() != 0 && itemStack.getDurability() != 1 && itemStack.getDurability() != 2)
			{
				getServer().broadcast(ChatColor.RED + player.getName() + " caught with item (" + itemStack.getType().toString() + ":"+itemStack.getDurability()+") with invalid damage data!", PERMISSION_NOTIFY);
				return true;
			}
		}

		/*********************************************************/
		// check NBT data // http://minecraft.gamepedia.com/Player.dat_Format

		// CustomPotionEffects
		if(!player.hasPermission(PERMISSION_NBT_CUSTOM_POTION_EFFECTS))
		{
			try
			{
				NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(itemStack);
				if(compound.containsKey("CustomPotionEffects"))
				{
					getServer().broadcast(ChatColor.RED + player.getName() + " caught with item ("+itemStack.getType().toString()+") with invalid NBT data (CustomPotionEffects)!", PERMISSION_NOTIFY);
					return true;
				}
			}
			catch (Exception e)
			{
				//e.printStackTrace();
				return false;
			}
		}

		// StoredEnchantments
		if(!player.hasPermission(PERMISSION_NBT_STORED_ENCHANTS) && itemStack.getType() != Material.ENCHANTED_BOOK)
		{
			try
			{
				NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(itemStack);
				if(compound.containsKey("StoredEnchantments"))
				{
					getServer().broadcast(ChatColor.RED + player.getName() + " caught with item ("+itemStack.getType().toString()+") with invalid NBT data (StoredEnchantments)!", PERMISSION_NOTIFY);
					return true;
				}
			}
			catch (Exception e)
			{
				//e.printStackTrace();
				return false;
			}
		}

		// HideFlags
		if(!player.hasPermission(PERMISSION_NBT_HIDE_FLAGS))
		{
			try
			{
				NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(itemStack);
				if(compound.containsKey("HideFlags"))
				{
					getServer().broadcast(ChatColor.RED + player.getName() + " caught with item ("+itemStack.getType().toString()+") with invalid NBT data (HideFlags)!", PERMISSION_NOTIFY);
					return true;
				}
			}
			catch (Exception e)
			{
				//e.printStackTrace();
				return false;
			}
		}

		// Unbreakable
		if(!player.hasPermission(PERMISSION_NBT_UNBREAKABLE))
		{
			try
			{
				NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(itemStack);
				if(compound.containsKey("Unbreakable"))
				{
					getServer().broadcast(ChatColor.RED + player.getName() + " caught with item ("+itemStack.getType().toString()+") with invalid NBT data (Unbreakable)!", PERMISSION_NOTIFY);
					return true;
				}
			}
			catch (Exception e)
			{
				//e.printStackTrace();
				return false;
			}
		}

		// AttributeModifiers
		if(!player.hasPermission(PERMISSION_NBT_ATTRIBUTE_MODIFIERS))
		{
			try
			{
				NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(itemStack);
				if(compound.containsKey("AttributeModifiers"))
				{
					getServer().broadcast(player.getName() + " caught with item ("+itemStack.getType().toString()+") with invalid NBT data (AttributeModifiers)!", PERMISSION_NOTIFY);
					return true;
				}
			}
			catch (Exception e)
			{
				//e.printStackTrace();
				return false;
			}
		}

		/*********************************************************/

		return false;
	}

	public void handleConfig()
	{
		saveDefaultConfig();
		reloadConfig();

		if(!getConfig().isSet("messageCommandNoPermission")) 		getConfig().set("messageCommandNoPermission", "&cYou do not have permission to execute this command!");
		if(!getConfig().isSet("messageInvalidMaterialType")) 		getConfig().set("messageInvalidMaterialType", "&cInvalid Material type!");
		if(!getConfig().isSet("messageTeleportingToSpawn")) 		getConfig().set("messageTeleportingToSpawn", "&6Teleporting you to spawn to prevent portal trapping.");
		if(!getConfig().isSet("messageNoPermissionCommandBlock")) 	getConfig().set("messageNoPermissionCommandBlock", "&cYou do not have permission to place CommandBlocks!");
		if(!getConfig().isSet("messageNoPermissionBarrier")) 		getConfig().set("messageNoPermissionBarrier", "&cYou do not have permission to place Barriers!");

		saveConfig();
	}
}
