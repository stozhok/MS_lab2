package org.midi.util;

public class BPMConverter {
    public static double convertBPMToMilliSec(double bpm) {
        return 60000 / bpm;
    }
    public static double convertBPMToMicroSec(double bpm) {
        return 60000000 / bpm;
    }
    public static long convertTickToMilliseconds(long timeInTicks, double bpm, int ppq) {
        long timeInMicroSec = (long) (timeInTicks * computeTickDurationMicroSec(bpm, ppq));
        return timeInMicroSec / 1000;
    }

    public static double computeTickDurationMicroSec(double bpm, int ppq) {
        return convertBPMToMicroSec(bpm) / ppq;
    }
    public static long convertTickToMilliseconds(long timeInTicks, double tickDurMicroSec) {
        long timeInMicroSec = (long) (timeInTicks * tickDurMicroSec);
        return timeInMicroSec / 1000;
    }
}