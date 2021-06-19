package com.keuin.watchdog.monitor.plugin;

import java.util.function.Consumer;

public interface MonitorPlugin extends Consumer<Double> {
    /**
     * thread unsafe!
     * @param tps current measured tps
     */
    @Override
    void accept(Double tps);
}
