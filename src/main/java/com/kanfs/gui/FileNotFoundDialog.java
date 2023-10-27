package com.kanfs.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;

public class FileNotFoundDialog extends JDialog{
    public FileNotFoundDialog(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);
        ClassLoader classLoader = FileNotFoundDialog.class.getClassLoader();
        ImageIcon flatSVGIcon1 = new FlatSVGIcon("ImageIcon/error-warning-line.svg", 16, 16, classLoader);
        ImageIcon flatSVGIcon2 = new FlatSVGIcon("ImageIcon/folder-warning-fill.svg", 36, 36, classLoader);
        JLabel notFoundLabel = new JLabel("未找到对应文件。", flatSVGIcon2, SwingConstants.LEFT);
        this.setLocationRelativeTo(null);
        this.setIconImage(flatSVGIcon1.getImage());
        this.setBounds(700,400,200,80);
        this.add(notFoundLabel);
        this.setVisible(true);
        this.pack();
    }
}
