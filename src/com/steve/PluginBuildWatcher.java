package com.steve;

import java.io.File;
import java.sql.Timestamp;
import java.util.TimerTask;

public class PluginBuildWatcher extends TimerTask {
    // by Simofu, source: https://stackoverflow.com/a/54815431/13216113

    private long lastTimeStamp;
    private final File file;
    private static final String PLUGIN_PATH = "plugins/Steve.jar";

    public PluginBuildWatcher() {
        this.file = new File(PLUGIN_PATH);
        this.lastTimeStamp = file.lastModified();
    }

    public final void run() {
        long timeStamp = file.lastModified();
        if (timeStamp != this.lastTimeStamp) {
            this.lastTimeStamp = timeStamp;
            Util.pluginIsBuilt(new Timestamp(timeStamp));
        }
    }
}