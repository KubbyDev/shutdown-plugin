package org.gunivers.shutdownplugin;

import java.util.Timer;
import java.util.TimerTask;

class ShutdownTimer extends Timer {

    private final Runnable task;
    private TimerTask timerTask;
    private final long delay;

    public ShutdownTimer(Runnable runnable, long delay) {
        this.task = runnable;
        this.delay = delay;
    }

    public void restart() {
        if (timerTask != null)
            timerTask.cancel();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        };
        this.schedule(timerTask, delay);
    }

    public void stop() {
        if(timerTask != null)
            timerTask.cancel();
        timerTask = null;
    }
}