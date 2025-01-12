package better.scoreboard.fabric.bridge;

import better.scoreboard.core.bridge.PluginLogger;
import org.slf4j.Logger;

public class FabricPluginLogger implements PluginLogger {

    private final Logger logger;

    public FabricPluginLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void logInfo(String s) {
        logger.info(s);
    }

    @Override
    public void logWarning(String s) {
        logger.warn(s);
    }
}
