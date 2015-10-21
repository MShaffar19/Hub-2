package net.samagames.hub.cosmetics.jukebox;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class JukeboxAlbum
{
    private final String identifier;
    private final String name;
    private final ItemStack icon;
    private final ArrayList<JukeboxDiskCosmetic> disks;
    private int cost;

    public JukeboxAlbum(String identifier, String name, ItemStack icon)
    {
        this.identifier = identifier;
        this.name = name;
        this.icon = icon;
        this.disks = new ArrayList<>();
    }

    public void addDisk(JukeboxDiskCosmetic disk)
    {
        this.disks.add(disk);
        this.cost = 0;
        
        for(JukeboxDiskCosmetic d : this.disks)
            this.cost += d.getStarsCost();
        
        this.cost -= (500 * ((float) 5 / 100));
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public String getName()
    {
        return this.name;
    }

    public ItemStack getIcon()
    {
        ItemStack stack = icon.clone();
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.setDisplayName(this.name);

        ArrayList<String> lores = new ArrayList<>();
        lores.add(ChatColor.GRAY + "Contient :");
        lores.add("");

        lores.addAll(this.disks.stream().map(disk -> ChatColor.GRAY + "- " + disk.getSong().getTitle() + ", de " + disk.getSong().getAuthor()).collect(Collectors.toList()));

        lores.add("");
        lores.add(ChatColor.AQUA + "▶ Clic gauche pour parcourir l'album");

        int price = 0;

        for(JukeboxDiskCosmetic d : this.disks)
            price += d.getStarsCost();

        price -= (500 * ((float) 5 / 100));

        lores.add(ChatColor.GREEN + "▶ Clic droit pour acheter l'album pour " + price + " étoiles (-5%)");

        stackMeta.setLore(lores);
        stack.setItemMeta(stackMeta);

        return stack;
    }

    public ItemStack getSimpleIcon()
    {
        return this.icon;
    }

    public ArrayList<JukeboxDiskCosmetic> getDisks()
    {
        return this.disks;
    }

    public int getCost()
    {
        return this.cost;
    }
}
