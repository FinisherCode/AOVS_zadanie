package code_generator.view;

import code_generator.controller.XMIParser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

public class Window extends JFrame{

    public Window(){
        this.setResizable(false);
        setSize(800, 600);
        this.setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JButton pickFileButton = new JButton("Choose file");
        pickFileButton.setBounds(5, 5, 100, 20 );
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
//                    System.out.println("You chose to open this file: " +
//                            chooser.getSelectedFile().getAbsolutePath());
                    try {
                        new XMIParser(chooser.getSelectedFile()).parseStuff();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        setLayout(null);
        add(pickFileButton);


    }


}
