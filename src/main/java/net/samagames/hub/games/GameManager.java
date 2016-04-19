package net.samagames.hub.games;

import net.samagames.api.SamaGamesAPI;
import net.samagames.hub.Hub;
import net.samagames.hub.common.hydroangeas.packets.PacketCallBack;
import net.samagames.hub.common.hydroangeas.packets.hubinfos.GameInfoToHubPacket;
import net.samagames.hub.common.hydroangeas.packets.queues.QueueInfosUpdatePacket;
import net.samagames.hub.common.managers.AbstractManager;
import net.samagames.hub.games.signs.GameSign;
import net.samagames.hub.games.types.*;
import net.samagames.tools.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class GameManager extends AbstractManager
{
    private final Map<String, AbstractGame> games;
    private final CopyOnWriteArrayList<UUID> playerHided;

    public GameManager(Hub hub)
    {
        super(hub);

        this.games = new HashMap<>();
        this.playerHided = new CopyOnWriteArrayList<>();

        this.registerGame(new BackEndGame(hub, "beta_vip", "VIP", LocationUtils.str2loc(hub.getConfig().getString("vip-zone"))));

        this.registerGame(new UppervoidGame(hub));
        this.registerGame(new QuakeGame(hub));
        this.registerGame(new DimensionsGame(hub));
        this.registerGame(new HeroBattleGame(hub));
        this.registerGame(new ChunkWarsGame(hub));

        // -----

        UHCZoneGame uhcZoneGame = new UHCZoneGame(hub);

        this.registerGame(uhcZoneGame);
        this.registerGame(new BackEndGame(hub, "uhc", "UHC", uhcZoneGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "uhcrun", "UHCRun", uhcZoneGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "switchrun", "SwitchRun", uhcZoneGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "doublerunner", "DoubleRunner", uhcZoneGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "uhcrandom", "UHCRandom", uhcZoneGame.getLobbySpawn()));

        // -----

        ArcadeGame arcadeGame = new ArcadeGame(hub);

        this.registerGame(arcadeGame);
        this.registerGame(new BackEndGame(hub, "craftmything", "CraftMyThing", arcadeGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "witherparty", "WitherParty", arcadeGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "hangovergames", "HangoverGames", arcadeGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "pacman", "PacMan", arcadeGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "burnthatchicken", "BurnThatChicken", arcadeGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "timberman", "Timberman", arcadeGame.getLobbySpawn()));
        this.registerGame(new BackEndGame(hub, "bomberman", "Bomberman", arcadeGame.getLobbySpawn()));
        this.registerGame(new AgarMCGame(hub));

        // -----

        this.registerGame(new BackEndGame(hub, "event", "Événement", this.hub.getPlayerManager().getSpawn()));

        hub.getHydroangeasManager().getPacketReceiver().registerCallBack(new PacketCallBack<GameInfoToHubPacket>(GameInfoToHubPacket.class)
        {
            @Override
            public void call(GameInfoToHubPacket packet)
            {
                for (AbstractGame game : games.values())
                {

                    for (List<GameSign> l : game.getSigns().values())
                    {
                        for (GameSign sign : l)
                        {
                            if (sign.getTemplate().equalsIgnoreCase(packet.getTemplateID()))
                            {
                                sign.setPlayerPerGame(packet.getPlayerMaxForMap());
                                sign.setPlayerWaitFor(packet.getPlayerWaitFor());
                                sign.setTotalPlayerOnServers(packet.getTotalPlayerOnServers());
                                sign.update();
                            }
                        }
                    }
                }
            }
        });

        hub.getHydroangeasManager().getPacketReceiver().registerCallBack(new PacketCallBack<QueueInfosUpdatePacket>(QueueInfosUpdatePacket.class)
        {
            @Override
            public void call(QueueInfosUpdatePacket packet)
            {
                try
                {
                    Player player = hub.getServer().getPlayer(packet.getPlayer().getUUID());

                    if (!packet.isSuccess() && (packet.getErrorMessage() != null && !packet.getErrorMessage().isEmpty()))
                    {
                        player.sendRawMessage(packet.getErrorMessage());
                        return;
                    }

                    if(player != null)
                    {
                        if(packet.getType().equals(QueueInfosUpdatePacket.Type.ADD))
                        {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.5F);

                            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                            player.sendMessage(ChatColor.GREEN + "Ajouté à la file d'attente de " + ChatColor.GOLD + packet.getGame() +  ChatColor.GREEN + " sur la map " + ChatColor.GOLD + packet.getMap() + ChatColor.GREEN + " !");
                            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        }
                        else if(packet.getType().equals(QueueInfosUpdatePacket.Type.REMOVE))
                        {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 0.8F);

                            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                            player.sendMessage(ChatColor.RED + "Retiré de la file d'attente de " + ChatColor.GOLD + packet.getGame() + ChatColor.RED + " sur la map " + ChatColor.GOLD + packet.getMap() + ChatColor.RED + " !");
                            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        }
                        else if(packet.getType().equals(QueueInfosUpdatePacket.Type.INFO) && packet.getMessage() != null)
                        {
                            if (!SamaGamesAPI.get().getSettingsManager().isEnabled(player.getUniqueId(), "queuenotifications", true))
                                return;

                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 10.0F, 2.0F);

                            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

                            for(String message : packet.getMessage())
                                player.sendMessage(ChatColor.YELLOW + message.replaceAll("<RESET>", String.valueOf(ChatColor.YELLOW)));

                            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        }
                    }
                }
                catch(Exception ignored) {}
            }
        });

        this.hub.getServer().getScheduler().runTaskTimerAsynchronously(hub, () ->
        {
            List<UUID> toHide = new ArrayList<>();

            for (AbstractGame game : this.getGames().values())
                for (List<GameSign> list : game.getSigns().values())
                    for (GameSign sign : list)
                        this.hub.getWorld().getNearbyEntities(sign.getSign().getLocation(), 1.0D, 1.0D, 1.0D).stream().filter(entity -> entity instanceof Player && !toHide.contains(entity.getUniqueId())).forEach(entity -> toHide.add(entity.getUniqueId()));

            for (UUID playerUUID : toHide)
            {
                Player player = this.hub.getServer().getPlayer(playerUUID);

                if (player == null)
                    continue;

                for (Player pPlayer : this.hub.getServer().getOnlinePlayers())
                    this.hub.getServer().getScheduler().runTask(hub, () -> pPlayer.hidePlayer(player));
            }

            this.playerHided.addAll(toHide);

            for (UUID playerUUID : this.playerHided)
            {
                Player player = this.hub.getServer().getPlayer(playerUUID);

                if (player == null)
                {
                    this.playerHided.remove(playerUUID);
                    continue;
                }

                if (!toHide.contains(playerUUID))
                {
                    this.playerHided.remove(playerUUID);

                    for (Player other : this.hub.getServer().getOnlinePlayers())
                        this.hub.getServer().getScheduler().runTask(hub, () -> other.showPlayer(player));
                }
            }
        }, 20L * 2, 20L * 2);
    }

    @Override
    public void onDisable()
    {
        this.games.values().stream().forEach(AbstractGame::clearSigns);
    }

    @Override
    public void onLogin(Player player) { /** Not needed **/ }

    @Override
    public void onLogout(Player player) {/** Not needed **/ }

    public void registerGame(AbstractGame game)
    {
        if(!this.games.containsKey(game.getCodeName()))
        {
            this.games.put(game.getCodeName(), game);
            this.log(Level.INFO, "Registered game '" + game.getCodeName() + "'");
        }
    }

    public AbstractGame getGameByIdentifier(String identifier)
    {
        if(this.games.containsKey(identifier.toLowerCase()))
            return this.games.get(identifier.toLowerCase());
        else
            return null;
    }

    public Map<String, AbstractGame> getGames()
    {
        return this.games;
    }
}
