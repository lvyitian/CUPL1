package top.dsbbs2.cupl1;

import java.io.File;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import top.dsbbs2.common.command.CommandRegistry;
import top.dsbbs2.common.config.SimpleConfig;
import top.dsbbs2.common.lambda.INoThrowsRunnable;
import top.dsbbs2.cupl1.Config.InvType;
import top.dsbbs2.cupl1.de.tr7zw.changeme.nbtapi.NBTContainer;
import top.dsbbs2.cupl1.de.tr7zw.changeme.nbtapi.NBTItem;

public class Main extends JavaPlugin implements Listener {
	public final SimpleConfig<Config> config = new SimpleConfig<Config>(
			this.getDataFolder().getAbsolutePath() + File.separator + "config.json", "UTF8", Config.class) {
		{
			INoThrowsRunnable.invoke(this::loadConfig);
		}
	};
	{
		CommandRegistry.regCom(this.getName(), CommandRegistry.setComUsa(CommandRegistry.setComAlias(CommandRegistry.newPluginCommand("ware", this),"ck","cangku"),"/<command>"));
	}
	public final Vector<Inventory> invs=new Vector<>();
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		if (this.invs.contains(e.getInventory())) {
			Player p=(Player)e.getInventory().getHolder();
			InvType type=this.config.getConfig().type.get(p.getName());
			if(type.incomplete)
			{
				if (type==InvType.TWO) {
					if ((e.getRawSlot()>=0 && e.getRawSlot()<=1) || (e.getRawSlot()>=9 && e.getRawSlot()<=10)) {
						Map<Integer,String> tm=this.config.getConfig().data.getOrDefault(p.getName(), new ConcurrentHashMap<>());
						tm.clear();
						for(int i=0;i<=1;i++)
						{
							if(e.getInventory().getContents()[i]!=null)
								tm.put(i, NBTItem.convertItemtoNBT(e.getInventory().getContents()[i]).toString());
						}
						for(int i=9;i<=10;i++)
						{
							if(e.getInventory().getContents()[i]!=null)
								tm.put(i, NBTItem.convertItemtoNBT(e.getInventory().getContents()[i]).toString());
						}
						this.config.getConfig().data.put(p.getName(), tm);
						INoThrowsRunnable.invoke(config::saveConfig);
					}else {
						e.setCancelled(true);
						return;
					}
				}else if (type==InvType.THREE) {
					if ((e.getRawSlot()>=0 && e.getRawSlot()<=2) || (e.getRawSlot()>=9 && e.getRawSlot()<=11) || (e.getRawSlot()>=18 && e.getRawSlot()<=20)) {
						Map<Integer,String> tm=this.config.getConfig().data.getOrDefault(p.getName(), new ConcurrentHashMap<>());
						tm.clear();
						for(int i=0;i<=2;i++)
						{
							if(e.getInventory().getContents()[i]!=null)
								tm.put(i, NBTItem.convertItemtoNBT(e.getInventory().getContents()[i]).toString());
						}
						for(int i=9;i<=11;i++)
						{
							if(e.getInventory().getContents()[i]!=null)
								tm.put(i, NBTItem.convertItemtoNBT(e.getInventory().getContents()[i]).toString());
						}
						for(int i=18;i<=20;i++)
						{
							if(e.getInventory().getContents()[i]!=null)
								tm.put(i, NBTItem.convertItemtoNBT(e.getInventory().getContents()[i]).toString());
						}
						this.config.getConfig().data.put(p.getName(), tm);
						INoThrowsRunnable.invoke(config::saveConfig);
					}else {
						e.setCancelled(true);
						return;
					}
				}
			}else {
				Map<Integer,String> tm=this.config.getConfig().data.getOrDefault(p.getName(), new ConcurrentHashMap<>());
				tm.clear();
				for(int i=0;i<type.size;i++)
				{
					if(e.getInventory().getContents()[i]!=null)
						tm.put(i, NBTItem.convertItemtoNBT(e.getInventory().getContents()[i]).toString());
				}
				this.config.getConfig().data.put(p.getName(), tm);
				INoThrowsRunnable.invoke(config::saveConfig);
			}
			
		}
	}
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryCloseEvent e) {
		this.invs.remove(e.getInventory());
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("ware")) {
			if (sender instanceof Player) {
				Player p=(Player)sender;
				Config.InvType type=config.getConfig().type.getOrDefault(p.getName(),null);
				if (type==null) {
					if(p.hasPermission("ware.use.big")){
						config.getConfig().type.put(p.getName(), InvType.BIG);
					}else if(p.hasPermission("ware.use.small")){
						config.getConfig().type.put(p.getName(), InvType.SMALL);
					}else if (p.hasPermission("ware.use.3")) {
						config.getConfig().type.put(p.getName(), InvType.THREE);
					}if (p.hasPermission("ware.use.2")) {
						config.getConfig().type.put(p.getName(), InvType.TWO);
					}else {
						p.sendMessage("无权限");
						return true;
					}
					INoThrowsRunnable.invoke(config::saveConfig);
					type=config.getConfig().type.get(p.getName());
				}
				Inventory inv=Bukkit.createInventory(p, type.size,"储物柜");
				this.invs.add(inv);
				this.config.getConfig().data.getOrDefault(p.getName(),new ConcurrentHashMap<>()).entrySet().parallelStream().forEach(i->{
					inv.setItem(i.getKey(), NBTItem.convertNBTtoItem(new NBTContainer(i.getValue())));
				});
				if (type.incomplete) {
					ItemStack item=new ItemStack(Material.STAINED_GLASS_PANE);
					item.setDurability((short)15);
					if (type==InvType.TWO) {
						for(int i=2;i<=8;i++)
							inv.setItem(i, item);
						for(int i=11;i<=17;i++)
							inv.setItem(i, item);
					}else if (type==InvType.THREE) {
						for(int i=3;i<=8;i++)
							inv.setItem(i, item);
						for(int i=12;i<=17;i++)
							inv.setItem(i, item);
						for(int i=21;i<=26;i++)
							inv.setItem(i, item);
					}
				}
			}
			return true;
		}
		return super.onCommand(sender, command, label, args);
	}
	
}
