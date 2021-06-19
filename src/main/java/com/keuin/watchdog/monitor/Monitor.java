package com.keuin.watchdog.monitor;

import com.keuin.watchdog.monitor.plugin.MonitorPlugin;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Monitor {

    private static Monitor instance = null;
    private static final List<MonitorPlugin> plugins = new ArrayList<>();
    public static Monitor getInstance() {
        return instance;
    }
    public synchronized static void initialize(Collection<MonitorPlugin> plugins) {
        Monitor.plugins.addAll(plugins);
        if (instance == null) {
            instance = new Monitor();
        }
    }

    private final Logger logger = Logger.getLogger(Monitor.class.getName());
    private final BlockingQueue<Double> queue = new LinkedBlockingQueue<>();

    public Monitor() {
        new Thread(this::queueConsumer).start();
    }

    public void accept(double tps) {
        queue.add(tps);
    }

    private void queueConsumer() {
        try {
            while (true) {
                double tps = queue.take();
                plugins.forEach(plugin -> plugin.accept(tps));
//                System.out.println("TPS: " + tps);
            }
        } catch (InterruptedException ignored) {
            logger.info("Monitor is quitting...");
        }
    }
}
