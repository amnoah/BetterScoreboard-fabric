package better.scoreboard.fabric.bridge;

import better.scoreboard.core.bridge.UserData;
import better.scoreboard.fabric.BetterScoreboardFabric;
import com.github.retrooper.packetevents.protocol.player.User;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricUserData implements UserData {

    private final BetterScoreboardFabric plugin;

    public FabricUserData(BetterScoreboardFabric plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPermission(User user, String... strings) {
        ServerPlayerEntity player = plugin.getServer().getPlayerManager().getPlayer(user.getUUID());
        if (player == null) return false;
        if (player.getPermissionLevel() >= 3) return true;

        if (!FabricLoader.getInstance().isModLoaded("luckperms")) return false;
        for (String permission : strings) if (Permissions.check(player, permission)) return true;
        return false;
    }
}
