package com.kanfs.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.kanfs.fileop.Filter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class CipherDialog extends JDialog implements ActionListener {
    private JPanel cipherPanel, confirmPanel;
    private JPasswordField cipherPf;
    private JButton visibleBtn;
    private ImageIcon visibleIcon, unvisibleIcon;
    private String cipher;

    public CipherDialog(JFrame owner, String title, boolean modal){
        super(owner, title, modal);

        Font kaiti = new Font("楷体", Font.PLAIN, 18);
        ClassLoader classLoader = CipherDialog.class.getClassLoader();
        visibleIcon = new FlatSVGIcon("ImageIcon/eye-line.svg", 16, 16, classLoader);
        unvisibleIcon = new FlatSVGIcon("ImageIcon/eye-off-line.svg", 16, 16, classLoader);

        // 密钥面板
        cipherPanel = new JPanel();
        cipherPanel.setLayout(new BoxLayout(cipherPanel, BoxLayout.X_AXIS));
        JLabel cipherLabel = new JLabel("请输入密钥:");
        cipherLabel.setFont(kaiti);
        cipherPf = new JPasswordField();
        cipherPf.setEchoChar('*');
        visibleBtn = new JButton(unvisibleIcon);
        visibleBtn.setActionCommand("visible");
        visibleBtn.addActionListener(this);
        cipherPanel.add(cipherLabel);
        cipherPanel.add(cipherPf);
        cipherPanel.add(visibleBtn);

        // 确认面板
        confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.X_AXIS));
        JButton confirmBtn = new JButton("  确认  ");
        confirmBtn.setFont(kaiti);
        confirmBtn.setActionCommand("confirm");
        confirmBtn.addActionListener(this);
        confirmPanel.add(confirmBtn);

        // 总面板
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(cipherPanel);
        panel.add(confirmPanel);
        ImageIcon flatSVGIcon = new FlatSVGIcon("ImageIcon/key-2-line.svg", 16, 16, classLoader);
        this.setIconImage(flatSVGIcon.getImage());
        this.add(panel);
        this.setBounds(575,380,400,100);
        this.setVisible(true);
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( e.getActionCommand().equals("confirm") )
        {
            this.cipher = String.valueOf(cipherPf.getPassword());
            this.dispose();
        }else if ( e.getActionCommand().equals("visible")) {
            boolean flag = cipherPf.echoCharIsSet();
            cipherPf.setEchoChar(flag?'\0':'*');
            if(flag) visibleBtn.setIcon(visibleIcon);
            else visibleBtn.setIcon(unvisibleIcon);
        }
    }

    public String getCipher() {
        return cipher;
    }
}
