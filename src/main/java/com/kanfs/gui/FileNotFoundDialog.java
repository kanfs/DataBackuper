package com.kanfs.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;

public class FileNotFoundDialog extends JDialog{
    public FileNotFoundDialog(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);
        ClassLoader classLoader = FileNotFoundDialog.class.getClassLoader();
        ImageIcon flatSVGIcon = new FlatSVGIcon("ImageIcon/error-warning-line.svg", 24, 24, classLoader);
        JLabel notFoundLabel = new JLabel("未找到对应文件。", flatSVGIcon, SwingConstants.LEFT);
        this.setLocationRelativeTo(null);
        this.setBounds(700,400,200,80);
        this.add(notFoundLabel);
        this.setVisible(true);
        this.pack();
    }
}
