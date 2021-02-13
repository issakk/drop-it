package com.chill.dropit;

import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@Data
public class FrameManager{


    private String selectedDir;

    public void init(){
        {
            FileManager fileManager = new FileManager();
            JFrame frame = new JFrame();
            frame.setTitle("Drop It");
            // int t = new Scanner(System.in).nextInt();
            frame.setBounds(500, 100, 700, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //JSplitPane jSplitPane = new JSplitPane();
            // jSplitPane.setSize(700, 800);


            JPanel jPanel = new JPanel();
            JTextField jTextField = new JTextField(30);

            JTextPane jTextPane = new JTextPane();
            jTextPane.setEditable(false);
            jTextPane.setText("操作记录:                         \r\n");

            jTextPane.setSize(700, 300);
            jTextPane.setFont(Font.getFont("OPPOSans R"));
            JScrollPane jScrollPane = new JScrollPane(jTextPane);
            jScrollPane.setPreferredSize(new Dimension(jTextPane.getWidth()-100, jTextPane.getHeight()));

            jScrollPane.add(jTextField);

            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setCurrentDirectory(new File("G:\\test-for-drop-it"));
            jFileChooser.setFont(Font.getFont("OPPOSans R"));
            jFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jFileChooser.addActionListener(e -> {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    System.out.println("jFileChooser = " + jFileChooser.getSelectedFile());
                    setSelectedDir(jFileChooser.getSelectedFile().getAbsolutePath());
                    jTextField.setText(getSelectedDir());
                    fileManager.drop(getSelectedDir(), jTextPane);
                }
                if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                    System.exit(0);
                }

            });
            FlowLayout flow = new FlowLayout();
            jPanel.setLayout(flow);
            jPanel.setSize(350, 800);
            jPanel.add(jFileChooser);
            jPanel.add(jScrollPane);
            frame.add(jPanel);
            frame.setVisible(true);
        }
    }
}
