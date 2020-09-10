package com.steve;

import java.io.File;
import java.util.TimerTask;

public class PluginBuildWatcher extends TimerTask {
    // by Simofu, source: https://stackoverflow.com/a/54815431/13216113

    private long lastTimeStamp;
    private final File file;

    public PluginBuildWatcher() {
        this.file = new File(Util.PLUGINS_PATH);
        this.lastTimeStamp = file.lastModified();
    }

    public final void run() {
        long timeStamp = file.lastModified();
        if (timeStamp != this.lastTimeStamp) {
            this.lastTimeStamp = timeStamp;
            Util.pluginIsBuilt();
        }
    }
}