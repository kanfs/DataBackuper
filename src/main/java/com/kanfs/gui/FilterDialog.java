package com.kanfs.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.kanfs.fileop.Filter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

public class FilterDialog extends JDialog implements ActionListener {
    private JPanel pathPanel, typePanel, namePanel, timePanel, sizePanel, confirmPanel;
    private JTextField pathTextField, typeTextField, nameTextField, timeStartTextField, timeEndTextField, minSizeTextField, maxSizeTextField;
    private JRadioButton pathBtn, nameBtn;
    private JComboBox<Object> minSizeUnit, maxSizeUnit;
    private Filter filter;

    public FilterDialog(JFrame owner, String title, boolean modal) {
        super(owner, title, modal);

        Font kaiti = new Font("楷体", Font.PLAIN, 18);

        // 路径面板
        pathPanel = new JPanel();
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));
        JLabel pathLabel = new JLabel("路径");
        pathLabel.setFont(kaiti);
        pathTextField = new JTextField(30);
        pathBtn = new JRadioButton("re");
        pathBtn.setFont(kaiti);
        pathPanel.add(pathLabel);
        pathPanel.add(pathTextField);
        pathPanel.add(pathBtn);

        // 类型面板
        typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
        JLabel typeLabel = new JLabel("类型");
        typeLabel.setFont(kaiti);
        typeTextField = new JTextField(30);
        JLabel typeRemindLabel = new JLabel("使用|分割文件类型");
        typeRemindLabel.setFont(kaiti);
        typePanel.add(typeLabel);
        typePanel.add(typeTextField);
        typePanel.add(typeRemindLabel);

        // 名字面板
        namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        JLabel nameLabel = new JLabel("名字");
        nameLabel.setFont(kaiti);
        nameTextField = new JTextField(30);
        nameBtn = new JRadioButton("re");
        nameBtn.setFont(kaiti);
        namePanel.add(nameLabel);
        namePanel.add(nameTextField);
        namePanel.add(nameBtn);

        // 时间面板 要求时间格式  HHHH-MM-DD HH:MM:SS
        timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
        JLabel timeLabel = new JLabel("时间");
        timeLabel.setFont(kaiti);
        timeStartTextField = new JTextField("yyyy-MM-dd HH:mm:ss", 30);
        // 添加焦点监听器
        timeStartTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // 当获得焦点时，清空文本框内容
                timeStartTextField.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                // 当失去焦点时，如果文本框为空，重新显示提示文本
                if (timeStartTextField.getText().isEmpty()) {
                    timeStartTextField.setText("yyyy-MM-dd HH:mm:ss");
                }
            }
        });
        timeEndTextField = new JTextField("yyyy-MM-dd HH:mm:ss", 30);
        // 添加焦点监听器
        timeEndTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // 当获得焦点时，清空文本框内容
                timeEndTextField.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                // 当失去焦点时，如果文本框为空，重新显示提示文本
                if (timeEndTextField.getText().isEmpty()) {
                    timeEndTextField.setText("yyyy-MM-dd HH:mm:ss");
                }
            }
        });
        JLabel timeRemindLabel = new JLabel("(留空视作无限早|晚)");
        timeRemindLabel.setFont(kaiti);
        timePanel.add(timeLabel);
        timePanel.add(timeStartTextField);
        timePanel.add(timeEndTextField);
        timePanel.add(timeRemindLabel);

        // 大小面板
        sizePanel = new JPanel();
        sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.X_AXIS));
        JLabel sizeLabel = new JLabel("大小");
        sizeLabel.setFont(kaiti);
        minSizeTextField = new JTextField(30);
        maxSizeTextField = new JTextField(30);
        minSizeUnit = new JComboBox<>(new String[]{"KB", "MB", "GB"});
        maxSizeUnit = new JComboBox<>(new String[]{"KB", "MB", "GB"});
        JLabel sizeRemindLabel = new JLabel("(留空视作0|∞)");
        sizeRemindLabel.setFont(kaiti);
        sizePanel.add(sizeLabel);
        sizePanel.add(minSizeTextField);
        sizePanel.add(minSizeUnit);
        sizePanel.add(maxSizeTextField);
        sizePanel.add(maxSizeUnit);
        sizePanel.add(sizeRemindLabel);

        // 确认面板
        confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.X_AXIS));
        JButton confirmBtn = new JButton("  确认  ");
        confirmBtn.setFont(kaiti);
        confirmBtn.setActionCommand("confirm");
        confirmBtn.addActionListener(this);
        confirmPanel.add(confirmBtn);

        // 总面板 自身
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(pathPanel);
        panel.add(typePanel);
        panel.add(namePanel);
        panel.add(timePanel);
        panel.add(sizePanel);
        panel.add(confirmPanel);
        ClassLoader classLoader = FilterDialog.class.getClassLoader();
        ImageIcon flatSVGIcon = new FlatSVGIcon("ImageIcon/filter-2-line.svg", 16, 16, classLoader);
        this.setIconImage(flatSVGIcon.getImage());
        this.add(panel);
        this.setBounds(475,300,600,240);
        this.setVisible(true);
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( e.getActionCommand().equals("confirm") )
        {
            try {
                filter = new Filter(pathTextField.getText(), typeTextField.getText(), nameTextField.getText(),
                        timeStartTextField.getText().equals("yyyy-MM-dd HH:mm:ss") ? "" :timeStartTextField.getText() ,
                        timeEndTextField.getText().equals("yyyy-MM-dd HH:mm:ss") ? "" :timeEndTextField.getText(),
                        minSizeTextField.getText(), maxSizeTextField.getText(), pathBtn.isSelected(), nameBtn.isSelected(),
                        minSizeUnit.getSelectedIndex(), maxSizeUnit.getSelectedIndex());
            }catch (ParseException exception){
                ErrorDialog wrongTimeFormatDialog = new ErrorDialog((JFrame) this.getOwner(), "wrong time format", false, 0);
            }
            this.dispose();
        }
    }

    public Filter getFilter(){
        return this.filter;
    }
}
