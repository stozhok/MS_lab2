package org.midi.GUI;

import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class MidiVisualizer {
    private final MainWindow mw;
    private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    private String noteAsString(int note) {
        String name = NOTE_NAMES[note % 12];
        int octave = (note / 12) - 1;
        return name + octave;
    }

    private int noteNumOnKeyboard(int note) {
        return note - 21;
    }

    public void onNoteRead(int note, int velocity) {
        SwingUtilities.invokeLater(() -> {
            JPanel drawPanel = mw.getDrawPanel();
            Graphics2D gr = (Graphics2D) drawPanel.getGraphics();
            gr.clearRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());

            int noteKey = noteNumOnKeyboard(note);
            gr.drawOval(150 - velocity / 2, 150 - velocity / 2, velocity, velocity);
            gr.setColor(Color.getHSBColor( (float) noteKey / 88, 1, 1));
            gr.drawRect(300, 100, 100, 100);
            gr.fillRect(300, 100, 100, 100);
            mw.getNotePropLabel().setText(String.format("<html>Нота: %s, <br> Швидкість: %d</html>", noteAsString(note), velocity));
            gr.setColor(Color.getHSBColor((float) noteKey / 88, (float) velocity / 127, (float) (noteKey * velocity) / 88 * 127));
            gr.fillOval(300 - noteKey, 300 - velocity / 2, noteKey * 2, velocity);
        });
    }
}