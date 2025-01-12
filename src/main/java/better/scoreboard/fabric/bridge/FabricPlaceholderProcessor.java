package better.scoreboard.fabric.bridge;

import better.scoreboard.core.bridge.PlaceholderProcessor;
import better.scoreboard.fabric.BetterScoreboardFabric;
import com.github.retrooper.packetevents.protocol.player.User;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FabricPlaceholderProcessor implements PlaceholderProcessor {

    private final BetterScoreboardFabric plugin;

    public FabricPlaceholderProcessor(BetterScoreboardFabric plugin) {
        this.plugin = plugin;
    }

    @Override
    public String setPlaceholders(User user, String s) {
        if (!FabricLoader.getInstance().isModLoaded("placeholder-api")) return s;

        ServerPlayerEntity player = plugin.getServer().getPlayerManager().getPlayer(user.getUUID());
        if (player == null) return s;

        return Placeholders.parseText(
                Text.of(s),
                PlaceholderContext.of(player)
        ).getString();
    }
}
