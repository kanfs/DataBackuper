package com.kanfs.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;

public class NotNullDialog extends JDialog {
    public NotNullDialog(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);
        ClassLoader classLoader = NotNullDialog.class.getClassLoader();
        ImageIcon flatSVGIcon = new FlatSVGIcon("ImageIcon/error-warning-line.svg", 24, 24, classLoader);
        JLabel notNullLabel = new JLabel("路径为空。", flatSVGIcon, SwingConstants.LEFT);
        this.setLocationRelativeTo(null);
        this.setBounds(700,400,200,80);
        this.add(notNullLabel);
        this.setVisible(true);
        this.pack();
    }
}
