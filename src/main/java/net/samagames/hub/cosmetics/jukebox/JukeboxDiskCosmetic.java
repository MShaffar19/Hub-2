package net.samagames.hub.cosmetics.jukebox;

import com.xxmicloxx.NoteBlockAPI.Song;
import net.samagames.hub.Hub;
import net.samagames.hub.cosmetics.common.AbstractCosmetic;
import net.samagames.hub.cosmetics.common.CosmeticAccessibility;
import net.samagames.hub.cosmetics.common.CosmeticRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

class JukeboxDiskCosmetic extends AbstractCosmetic
{
    private final Song song;
    private final int seconds;

    JukeboxDiskCosmetic(Hub hub, int storageId, Song song, int seconds) throws Exception
    {
        super(hub, storageId);

        this.song = song;
        this.seconds = seconds;
    }

    public Song getSong()
    {
        return this.song;
    }

    public int getSeconds()
    {
        return this.seconds;
    }
}
