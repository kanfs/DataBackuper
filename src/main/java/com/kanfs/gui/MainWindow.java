package com.kanfs.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.kanfs.fileop.Basic.*;

public class MainWindow extends JFrame implements ActionListener{


    private JPanel sourcePanel, backupPanel, BorRPanel, confirmPanel, panel;
    private JTextField sourceTextField, backupTextField;
    private JRadioButton backupBtn, restoreBtn;

    public MainWindow() {
        super("DataBackuper");
        // 源文件选择面板
        sourcePanel = new JPanel();
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.X_AXIS));
        JLabel sourceLabel = new JLabel("  源文件路径:  ");
        sourceTextField = new JTextField(25);
        JButton sourceFileChooseBtn = new JButton("  选择源文件地址  ");
        sourceFileChooseBtn.setActionCommand("choose source file");
        sourceFileChooseBtn.addActionListener(this);
        sourcePanel.add(sourceLabel);
        sourcePanel.add(sourceTextField);
        sourcePanel.add(sourceFileChooseBtn);
        
        // 备份文件选择面板
        backupPanel = new JPanel();
        backupPanel.setLayout(new BoxLayout(backupPanel, BoxLayout.X_AXIS));
        JLabel backupLabel = new JLabel("备份文件路径:");
        backupTextField = new JTextField(25);
        JButton backupFileChooseBtn = new JButton("选择备件文件地址");
        backupFileChooseBtn.setActionCommand("choose backup file");
        backupFileChooseBtn.addActionListener(this);
        backupPanel.add(backupLabel);
        backupPanel.add(backupTextField);
        backupPanel.add(backupFileChooseBtn);

        // 备份或还原选择面板
        BorRPanel = new JPanel();
        BorRPanel.setLayout(new BoxLayout(BorRPanel, BoxLayout.X_AXIS));
        JLabel BorRLabel = new JLabel("操作:");
        backupBtn = new JRadioButton("备份");
        restoreBtn = new JRadioButton("还原");
        ButtonGroup _bg = new ButtonGroup();
        _bg.add(backupBtn);
        _bg.add(restoreBtn);
        BorRPanel.add(BorRLabel);
        BorRPanel.add(backupBtn);
        BorRPanel.add(restoreBtn);

        // 确认面板
        confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.X_AXIS));
        JButton confirmBtn = new JButton("确认");
        confirmBtn.setActionCommand("confirm");
        confirmBtn.addActionListener(this);
        confirmPanel.add(confirmBtn);

        // 总面板控制
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(sourcePanel);
        panel.add(backupPanel);
        panel.add(BorRPanel);
        panel.add(confirmPanel);
        this.add(panel);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // 点击了文件选择按钮
        if ( e.getActionCommand().equals("choose source file") || e.getActionCommand().equals("choose backup file"))
        {
            // 弹出文件选择窗口
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 文件或文件夹都可选
            jFileChooser.setDialogTitle("请选择文件");
            jFileChooser.showOpenDialog(null);
            // 将选择的文件路径填入 JTextField
            String filePath;
            if ( jFileChooser.getSelectedFile() != null )
            {
                filePath = jFileChooser.getSelectedFile().getAbsolutePath();
                if (e.getActionCommand().equals("choose source file")) sourceTextField.setText(filePath);
                else backupTextField.setText(filePath);
            }
        } else if ( e.getActionCommand().equals("confirm") ){
            // 点击了确认按钮后将信息进行备份或还原
            System.out.println("click confirm");
            //备份
            if ( backupBtn.isSelected() ) backup(sourceTextField.getText(), backupTextField.getText());
            //还原 将备份文件复制回源文件夹
            if ( restoreBtn.isSelected() ) restore(sourceTextField.getText(), backupTextField.getText());

            //dispose();
        }
    }
}
