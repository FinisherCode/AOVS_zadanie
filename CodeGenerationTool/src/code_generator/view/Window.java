package code_generator.view;

import code_generator.controller.XMIParser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

public class Window extends JFrame{

    private File chodenFile = null;

    public Window(){
        this.setResizable(false);
        setSize(800, 190);
        this.setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton pickFileButton = new JButton("Choose file");
        pickFileButton.setBounds(5, 100, 100, 25 );
        final JLabel fileNameLabel = new JLabel();
        fileNameLabel.setBounds(10, 0, 800, 25);
        add(fileNameLabel);
        pickFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "XMI files", "xml");
                chooser.setFileFilter(filter);
                chooser.setCurrentDirectory(new File("./data"));
                int returnVal = chooser.showOpenDialog(Window.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                        chodenFile = chooser.getSelectedFile();
                        fileNameLabel.setText("Chosen file: " + chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        setLayout(null);
        add(pickFileButton);

        String[] petStrings = { "AODM" };
        JComboBox<String> modelList = new JComboBox<>(petStrings);
        modelList.setSelectedIndex(0);
        modelList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        modelList.setBounds(10, 50, 100, 25);
        add(modelList);

        String[] codeString = { "AspectJ" };
        JComboBox<String> codeList = new JComboBox<>(codeString);
        codeList.setSelectedIndex(0);
        codeList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        codeList.setBounds(120, 50, 100, 25);
        add(codeList);

        refresh();

        JButton generateButton = new JButton("Generate");
        generateButton.setBounds(120, 100, 100, 25);
        add(generateButton);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(chodenFile != null){
                    try {
                        new XMIParser(chodenFile).parseStuff();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(Window.this, "You need to choose file first");
                }
            }
        });
    }


    private void refresh(){
        invalidate();
        validate();
        repaint();
    }


}
