package org.midi.util;

import lombok.RequiredArgsConstructor;
import org.midi.GUI.MidiVisualizer;

import javax.sound.midi.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

@RequiredArgsConstructor
public class MidiReader {
    private final MidiVisualizer mv;

    public static FileFilter getMidiFileFilter() {
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".mid") | pathname.isDirectory();
            }

            @Override
            public String getDescription() {
                return "(*.mid) MIDI-файл";
            }
        };
    }

    public void start(String filepath, double bpm) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                read(filepath, bpm);
                return null;
            }
        };

        worker.execute();
    }

    private void read(String filepath, double bpm) {
        File midiFile = new File(filepath);

        if (!midiFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        try {
            Sequence sequence = MidiSystem.getSequence(midiFile);


            int ppq = sequence.getResolution();

            double tickDurMicroSec = BPMConverter.computeTickDurationMicroSec(bpm, ppq);

            Thread t = new Thread(() -> {
                try {
                    Synthesizer synthesizer = MidiSystem.getSynthesizer();
                    synthesizer.open();

                    Sequencer sequencer = MidiSystem.getSequencer();
                    sequencer.setSequence(sequence);
                    sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());

                    sequencer.open();
                    sequencer.start();
                    sequencer.setTempoInBPM((float) bpm);

                    while (sequencer.isRunning());
                    sequencer.close();
                    synthesizer.close();
                } catch (MidiUnavailableException | InvalidMidiDataException e) {
                    Thread.currentThread().interrupt();
                }
            }, "synthThread");

            t.start();
            long previousTick = 0;

            for (Track track : sequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    long tick = event.getTick();

                    long deltaTime = BPMConverter.convertTickToMilliseconds(tick - previousTick, tickDurMicroSec);

                    if(deltaTime > 0)
                        Thread.sleep(deltaTime);
                    previousTick = tick;

                    if (message instanceof ShortMessage sm) {
                        if (sm.getCommand() == ShortMessage.NOTE_ON) {
                            int note = sm.getData1();
                            int velocity = sm.getData2();

                            mv.onNoteRead(note, velocity);
                        }
                    }
                }
            }

            t.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}