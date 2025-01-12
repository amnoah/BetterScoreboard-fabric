package better.scoreboard.fabric;

import better.scoreboard.core.BetterScoreboard;
import better.scoreboard.core.displayuser.DisplayUserManager;
import better.scoreboard.core.placeholder.PlaceholderManager;
import better.scoreboard.fabric.bridge.FabricConfigSection;
import better.scoreboard.fabric.bridge.FabricPlaceholderProcessor;
import better.scoreboard.fabric.bridge.FabricPluginLogger;
import better.scoreboard.fabric.bridge.FabricUserData;
import better.scoreboard.fabric.listener.JoinListener;
import better.scoreboard.fabric.listener.PlayerUpdateListener;
import com.github.retrooper.packetevents.PacketEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class BetterScoreboardFabric implements ModInitializer {

    public static final String MOD_ID = "betterscoreboard";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Core objects.
    private BetterScoreboard core;

    private MinecraftServer server = null;

    private final Set<Object> tempPlayers = new HashSet<>();

    //private final Map<UUID, Object> tempPlayers = new HashMap<>(), activePlayers = new HashMap<>();


    @Override
    public void onInitialize() {
        core = new BetterScoreboard(
                new FabricPlaceholderProcessor(this),
                new FabricPluginLogger(LOGGER),
                new FabricUserData(this)
        );

        core.init();

        PlaceholderManager.registerPlaceholder("displayname", user -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(user.getUUID());
            if (player == null) return "";
            return player.getDisplayName().getString();
        });
        PlaceholderManager.registerPlaceholder("gamemode", user -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(user.getUUID());
            if (player == null) return "";
            return player.interactionManager.getGameMode().getName();
        });
        PlaceholderManager.registerPlaceholder("health", user -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(user.getUUID());
            if (player == null) return "";
            return String.valueOf(player.getHealth());
        });
        PlaceholderManager.registerPlaceholder("maxplayers", user -> String.valueOf(server.getMaxPlayerCount()));
        PlaceholderManager.registerPlaceholder("ping", user -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(user.getUUID());
            if (player == null) return "";
            return String.valueOf(player.networkHandler.getLatency());
        });
        PlaceholderManager.registerPlaceholder("players", user -> String.valueOf(server.getCurrentPlayerCount()));
        PlaceholderManager.registerPlaceholder("world", user -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(user.getUUID());
            if (player == null) return "";
            return player.getWorld().getRegistryKey().getValue().toShortTranslationKey();
        });
        PlaceholderManager.registerPlaceholder("worldplayers", user -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(user.getUUID());
            if (player == null) return "";
            return String.valueOf(player.getWorld().getPlayers().size());
        });

        core.enable();

        // Register all listeners.
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(new PlayerUpdateListener());
        PacketEvents.getAPI().getEventManager().registerListeners(new JoinListener(this));

        load();

        ServerTickEvents.END_SERVER_TICK.register((minecraftServer -> {
            if (server == null) server = minecraftServer;

            for (Object object : tempPlayers) DisplayUserManager.addDisplayUser(object);
            tempPlayers.clear();

            core.tick();
        }));
    }

    public void load() {
        try {
            Path configDirectory = FabricLoader.getInstance().getConfigDir();

            // This shouldn't be possible, but check anyway.
            if (!Files.exists(configDirectory)) Files.createDirectories(configDirectory);

            Path filePath = configDirectory.resolve("BetterScoreboardConfig.yml");
            File file = filePath.toFile();

            if (!file.exists()) {
                InputStream inputStream = BetterScoreboard.class.getResourceAsStream("/config.yml");
                assert (inputStream != null);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            YamlConfigurationLoader configLoader = YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).path(filePath).build();
            CommentedConfigurationNode node = configLoader.load();

            core.load(new FabricConfigSection(node));
        } catch (IOException e) {
            LOGGER.warn("Could not load BetterScoreboard's configuration.");
            LOGGER.warn("Please verify the legitimacy of your configuration file as the plugin may not work as intended.");
            e.printStackTrace();
        }
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void addNewPlayer(Object player) {
        tempPlayers.add(player);
    }

    public void removeNewPlayer(UUID uuid) {
        tempPlayers.remove(uuid);
    }
}
