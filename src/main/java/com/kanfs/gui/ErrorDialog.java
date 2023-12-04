package com.kanfs.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;

public class ErrorDialog extends JDialog {
    public ErrorDialog ( JFrame owner, String title, boolean modal, int type){
        super(owner, title, modal);
        ClassLoader classLoader = ErrorDialog.class.getClassLoader();
        ImageIcon flatSVGIcon1 = new FlatSVGIcon("ImageIcon/error-warning-line.svg", 16, 16, classLoader);
        ImageIcon flatSVGIcon2 = new FlatSVGIcon("ImageIcon/alarm-warning-line.svg", 36, 36, classLoader);
        String str = "";
        switch (type)
        {
            case 0:
                str = "错误的时间格式。";
                break;
            case 1:
                str = "路径为空。";
                break;
            case 2:
                str = "未找到对应文件。";
                break;
            case 3:
                str = "错误的文件类型。";
                break;
            default:
                str = "未知错误";
        }
        JLabel wrongTimeFormatLabel = new JLabel(str, flatSVGIcon2, SwingConstants.LEFT);
        this.setLocationRelativeTo(null);
        this.setIconImage(flatSVGIcon1.getImage());
        this.setBounds(700,400,200,80);
        this.add(wrongTimeFormatLabel);
        this.setVisible(true);
        this.pack();
    }
}
