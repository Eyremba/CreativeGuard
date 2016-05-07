package creativeguard;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

public class EventListnerer implements Listener
{
	private CreativeGuard creativeGuard;

	public EventListnerer(CreativeGuard creativeGuard)
	{
		this.creativeGuard = creativeGuard;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();

		if(event.getBlockPlaced().getType() == Material.COMMAND || event.getBlockPlaced().getType() == Material.COMMAND_MINECART)
		{
			if(player.hasPermission(creativeGuard.PERMISSION_COMMAND_BLOCK))
				return;

			creativeGuard.getServer().broadcast(ChatColor.RED + player.getName() + " tried to place CommandBlock!", creativeGuard.PERMISSION_NOTIFY);
			if(!creativeGuard.getConfig().getString("messageNoPermissionCommandBlock").equals(""))
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', creativeGuard.getConfig().getString("messageNoPermissionCommandBlock")));
			player.getInventory().remove(Material.COMMAND);
			player.getInventory().remove(Material.COMMAND_MINECART);
			player.updateInventory();
			event.setCancelled(true);
			return;
		}

		if(event.getBlockPlaced().getType() == Material.BARRIER)
		{
			if(player.hasPermission(creativeGuard.PERMISSION_BARRIER))
				return;

			creativeGuard.getServer().broadcast(ChatColor.RED + player.getName() + " tried to place Barrier!", creativeGuard.PERMISSION_NOTIFY);
			if(!creativeGuard.getConfig().getString("messageNoPermissionBarrier").equals(""))
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', creativeGuard.getConfig().getString("messageNoPermissionBarrier")));
			player.getInventory().remove(Material.BARRIER);
			player.updateInventory();
			event.setCancelled(true);
			return;
		}
	}

	/***********************************************************/

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		Player player = (Player) event.getPlayer();
		ItemStack itemStack = (ItemStack) event.getItem();

		if(player == null || itemStack == null || itemStack.getType() == Material.AIR)
			return;

		if(creativeGuard.isInvalidItem(player, itemStack))
		{
			player.getInventory().remove(itemStack);
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event)
	{
		Item item = event.getItem();
		if(item == null)
			return;

		ItemStack itemStack = item.getItemStack();
		if(itemStack == null || itemStack.getType() == Material.AIR)
			return;

		Player player = event.getPlayer();

		if(creativeGuard.isInvalidItem(player, itemStack))
		{
			event.getItem().remove();
			event.setCancelled(true);
			return;
		}
	}

	public void onPlayerDropItemEvent(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();

		Item item = event.getItemDrop();
		ItemStack itemStack = item.getItemStack();

		if(creativeGuard.isInvalidItem(player, itemStack))
		{
			event.getItemDrop().remove();
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onInventoryCreativeEvent(InventoryCreativeEvent event)
	{
		if(!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();
		ItemStack itemStack = event.getCursor();

		if(itemStack == null || itemStack.getType() == Material.AIR)
			itemStack = event.getCurrentItem();

		if(creativeGuard.isInvalidItem(player, itemStack))
		{
			event.setCurrentItem(new ItemStack(Material.AIR));
			event.setCursor(new ItemStack(Material.AIR));
			event.setCancelled(true);
			return;
		}

	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event)
	{
		if(!(event.getPlayer() instanceof Player))
			return;

		Player player = (Player) event.getPlayer();

		for(ItemStack itemStack : player.getInventory())
		{
			if(creativeGuard.isInvalidItem(player, itemStack))
				player.getInventory().remove(itemStack);
		}
		player.updateInventory();
	}

	/***********************************************************/

	@EventHandler
	public void onPlayerPortalEvent(PlayerPortalEvent event)
	{
		Player player = event.getPlayer();
		if (player == null)
			return;

		if(!player.hasPermission(creativeGuard.PERMISSION_NETHER_PORTAL_TO_SPAWN))
			return;

		if(!creativeGuard.getConfig().getString("messageTeleportingToSpawn").equals(""))
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', creativeGuard.getConfig().getString("messageTeleportingToSpawn")));
		player.teleport(player.getWorld().getSpawnLocation());
		event.setCancelled(true);
	}

	/***********************************************************/






}
