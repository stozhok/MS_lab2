package org.midi.GUI;

import lombok.Getter;
import org.midi.util.MidiReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainWindow {
    public final Dimension mainWindowDims = new Dimension(600, 500);

    private JButton launchButton;
    @Getter
    private JPanel mainPanel;
    private JLabel bpmLabel;
    private JTextField bpmTextField;
    private JLabel pathLabel;
    private JTextField pathTextField;
    private JButton chooseFileButton;
    @Getter
    private JPanel drawPanel;
    @Getter
    private JLabel notePropLabel;
    private final JFrame frame;
    private String midiFileName;

    public MainWindow(JFrame frame) {
        this.frame = frame;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10)); // Табличне розташування: 3 рядки, 2 стовпці, відступи 10px

        pathLabel = new JLabel("Шлях до MIDI-файлу:");
        pathTextField = new JTextField(20);
        chooseFileButton = new JButton("Вибрати файл");

        bpmLabel = new JLabel("BPM:");
        bpmTextField = new JTextField("120", 5);
        launchButton = new JButton("Запуск");

        inputPanel.add(pathLabel);
        inputPanel.add(pathTextField);
        inputPanel.add(chooseFileButton);
        inputPanel.add(bpmLabel);
        inputPanel.add(bpmTextField);
        inputPanel.add(launchButton);

        drawPanel = new JPanel();
        drawPanel.setBackground(Color.WHITE);
        drawPanel.setPreferredSize(new Dimension(600, 300)); // Задання розміру

        notePropLabel = new JLabel("Інформація про ноту відображатиметься тут");
        notePropLabel.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(drawPanel, BorderLayout.CENTER);
        mainPanel.add(notePropLabel, BorderLayout.SOUTH);

        chooseFileButton.addActionListener(e -> {
            if (openFile()) {
                pathTextField.setText(midiFileName);
            }
        });

        launchButton.addActionListener(e -> {
            handleLaunch();
        });
    }

    private boolean openFile() {
        JFileChooser openFileDialog = new JFileChooser();
        openFileDialog.setDialogTitle("Відкрити файл...");
        openFileDialog.setCurrentDirectory(new File(System.getProperty("user.dir")));
        openFileDialog.setFileFilter(MidiReader.getMidiFileFilter());
        int result = openFileDialog.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            midiFileName = openFileDialog.getSelectedFile().getAbsolutePath();
            return true;
        }
        return false;
    }

    private void handleLaunch() {
        if (pathTextField.getText().isBlank()) {
            JOptionPane.showMessageDialog(frame, "Введіть назву файлу", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        midiFileName = pathTextField.getText();
        if (!midiFileName.endsWith(".mid")) {
            JOptionPane.showMessageDialog(frame, "Файл має містити розширення .mid!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String bpmText = bpmTextField.getText();
        if (bpmText.isBlank()) {
            JOptionPane.showMessageDialog(frame, "Не введено BPM. Використовується BPM за замовчуванням (120)", "Не введено BPM", JOptionPane.INFORMATION_MESSAGE);
            bpmTextField.setText("120");
            bpmText = "120";
        }

        try {
            double bpm = Double.parseDouble(bpmText);
            if (bpm <= 0) throw new NumberFormatException();

            MidiVisualizer mv = new MidiVisualizer(this);
            MidiReader reader = new MidiReader(mv);
            reader.start(midiFileName, bpm);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "BPM має бути додатним дійсним числом", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
