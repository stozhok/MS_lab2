package org.midi;

import org.midi.GUI.MainWindow;

import javax.swing.*;


public class App
{
    public static void main( String[] args )
    {
        JFrame frame = new JFrame("Анімація по MIDI");
        MainWindow mw = new MainWindow(frame);
        frame.setContentPane(mw.getMainPanel());
        frame.setMinimumSize(mw.mainWindowDims);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}