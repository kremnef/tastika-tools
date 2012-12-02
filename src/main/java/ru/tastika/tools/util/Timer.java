package ru.tastika.tools.util;


public class Timer {


    private long start_time;
    private long elapsed_time;
    private boolean stopped;


    public Timer() {
        this(true);
    }


    public Timer(boolean started) {
        stopped = true;
        if (started) {
            start();
        }
    }


    public void start() {
        if (stopped) {
            start_time = getCurrentSystemTime();
            stopped = false;
        }
    }


    public void stop() {
        if (!stopped) {
            elapsed_time += getCurrentSystemTime() - start_time;
            stopped = true;
        }
    }


    public void toggle() {
        if (stopped) {
            start();
        }
        else {
            stop();
        }
    }


    public long getElapsedTime() {
        if (stopped) {
            return elapsed_time;
        }
        else {
            return (elapsed_time + getCurrentSystemTime()) - start_time;
        }
    }


    public boolean isActive() {
        return !stopped;
    }


    public boolean isStopped() {
        return stopped;
    }


    public final void reset() {
        elapsed_time = 0L;
        start_time = getCurrentSystemTime();
    }


    public final String toString() {
        double time = ((double) getElapsedTime());
        long minutes = (long) (time / 60000L);
        time -= 60000L * minutes;
        long seconds = (long) (time / 1000L);
        time -= 1000L * seconds;
        long miliseconds = (long) time;
        String s = "";
        s = s + minutes + " min ";
        s = s + seconds + " sec ";
        s = s + miliseconds + " msec ";
        return s;
    }


    private static long getCurrentSystemTime() {
        return System.currentTimeMillis();
    }
}
