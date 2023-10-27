package com.kanfs.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;

public class WrongTimeFormatDialog extends JDialog {
    public WrongTimeFormatDialog ( JFrame owner, String title, boolean modal ){
        super(owner, title, modal);
        ClassLoader classLoader = WrongTimeFormatDialog.class.getClassLoader();
        ImageIcon flatSVGIcon1 = new FlatSVGIcon("ImageIcon/error-warning-line.svg", 16, 16, classLoader);
        ImageIcon flatSVGIcon2 = new FlatSVGIcon("ImageIcon/alarm-warning-line.svg", 36, 36, classLoader);
        JLabel wrongTimeFormatLabel = new JLabel("错误的时间格式。", flatSVGIcon2, SwingConstants.LEFT);
        this.setLocationRelativeTo(null);
        this.setIconImage(flatSVGIcon1.getImage());
        this.setBounds(700,400,200,80);
        this.add(wrongTimeFormatLabel);
        this.setVisible(true);
        this.pack();
    }
}
