package better.scoreboard.fabric.listener;

import better.scoreboard.core.displayuser.DisplayUserManager;
import com.github.retrooper.packetevents.PacketEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayerUpdateListener implements ServerEntityWorldChangeEvents.AfterPlayerChange {

    @Override
    public void afterChangeWorld(ServerPlayerEntity serverPlayerEntity, ServerWorld serverWorld, ServerWorld serverWorld1) {
        DisplayUserManager.getDisplayUser(PacketEvents.getAPI().getPlayerManager().getUser(serverPlayerEntity)).checkDisplays();
    }
}
