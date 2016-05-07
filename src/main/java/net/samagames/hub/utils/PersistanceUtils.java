package net.samagames.hub.utils;

import net.samagames.api.shops.IItemDescription;
import net.samagames.tools.MojangShitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PersistanceUtils
{
    /**
     * Create an ItemStack via the database data.
     *
     * The first character sets the type :
     * - B: Basic
     * - P: Potion
     * - E: Monster Egg
     *
     * Because of there are multiple way to create
     * the different ItemStack, we needed to set the
     * first character as a type.
     *
     * To create a simple dirt block, this has to be
     * stored like that: B:DIRT:1:0
     *
     * For a strength potion: P:strength:false:false
     *
     * For a monster egg: E:OCELOT
     *
     * @param itemDescription The item description from the database
     *
     * @return An ItemStack
     */
    public static ItemStack makeStack(IItemDescription itemDescription)
    {
        String[] itemData = itemDescription.getItemDesc().split(":");
        ItemStack stack;

        if (itemData[0].equalsIgnoreCase("P"))
        {
            String nmsPotionName = itemData[1].toLowerCase();
            boolean isSplash = Boolean.parseBoolean(itemData[2]);
            boolean isLingering = Boolean.parseBoolean(itemData[3]);

            stack = MojangShitUtils.getPotion(nmsPotionName, isSplash, isLingering);
        }
        else if (itemData[0].equalsIgnoreCase("E"))
        {
            EntityType entityType = EntityType.valueOf(itemData[1].toUpperCase());

            stack = MojangShitUtils.getMonsterEgg(entityType);
        }
        else
        {
            Material material = Material.valueOf(itemData[1].toUpperCase());
            int size = Integer.parseInt(itemData[2]);
            byte durability = Byte.parseByte(itemData[3]);

            stack = new ItemStack(material, size, durability);
        }

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.translateAlternateColorCodes('&', itemDescription.getItemName()));

        List<String> lore = new ArrayList<>();

        if (itemDescription.getItemDesc() != null)
            for (String str : itemDescription.getItemDesc().split("\n"))
                lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', str));

        meta.setLore(lore);
        stack.setItemMeta(meta);

        return stack;
    }
}
