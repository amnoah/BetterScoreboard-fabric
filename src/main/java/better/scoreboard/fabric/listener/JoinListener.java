package better.scoreboard.fabric.listener;

import better.scoreboard.fabric.BetterScoreboardFabric;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.simple.PacketLoginSendEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

public class JoinListener extends SimplePacketListenerAbstract {

    private final BetterScoreboardFabric plugin;

    public JoinListener(BetterScoreboardFabric plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.JOIN_GAME) return;
        plugin.addNewPlayer(event.getPlayer());

    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        plugin.removeNewPlayer(event.getUser().getUUID());
    }
}
