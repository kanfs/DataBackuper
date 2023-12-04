package com.kanfs.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.kanfs.fileop.Filter;
import com.kanfs.fileop.Parser;
import static com.kanfs.fileop.Basic.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

public class MainWindow extends JFrame implements ActionListener{


    private JPanel sourcePanel, backupPanel, newFileNamePanel, BorRPanel, attributesPanel, filterPanel, compressPanel, encryptionPanel, confirmPanel, panel;
    private JTextField sourceTextField, backupTextField, newFileNameTextField;
    private JRadioButton backupBtn, restoreBtn, compressBtn, decompressBtn, noCompressBtn, encryptBtn, decryptBtn, noEncryptionBtn;
    private JCheckBox owner, time, acl;
    private FilterDialog filterDialog;  // 文件筛选器弹窗
    private Filter filter = null; // 文件筛选器
    private String cipher; // 密钥

    public MainWindow() {
        super("DataBackuper");
        Font xingkai = new Font("华文行楷", Font.PLAIN, 18);
        Font kaiti = new Font("楷体", Font.PLAIN, 18);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        // 源文件选择面板
        sourcePanel = new JPanel();
        sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.X_AXIS));
        JLabel sourceLabel = new JLabel("    源文件路径:");
        sourceLabel.setFont(xingkai);
        sourceTextField = new JTextField(30);
        JButton sourceFileChooseBtn = new JButton("  选择源文件地址  ");
        sourceFileChooseBtn.setFont(xingkai);
        sourceFileChooseBtn.setActionCommand("choose source file");
        sourceFileChooseBtn.addActionListener(this);
        sourcePanel.add(sourceLabel);
        sourcePanel.add(sourceTextField);
        sourcePanel.add(sourceFileChooseBtn);
        
        // 备份文件选择面板
        backupPanel = new JPanel();
        backupPanel.setLayout(new BoxLayout(backupPanel, BoxLayout.X_AXIS));
        JLabel backupLabel = new JLabel("备份文件路径:");
        backupLabel.setFont(xingkai);
        backupTextField = new JTextField(30);
        JButton backupFileChooseBtn = new JButton("选择备件文件地址");
        backupFileChooseBtn.setFont(xingkai);
        backupFileChooseBtn.setActionCommand("choose backup file");
        backupFileChooseBtn.addActionListener(this);
        backupPanel.add(backupLabel);
        backupPanel.add(backupTextField);
        backupPanel.add(backupFileChooseBtn);

        // 新文件名字面板
        newFileNamePanel = new JPanel();
        newFileNamePanel.setLayout(new BoxLayout(newFileNamePanel, BoxLayout.X_AXIS));
        JLabel newFileNameLabel = new JLabel("        新文件名:");
        newFileNameLabel.setFont(xingkai);
        newFileNameTextField = new JTextField(30);
        //ClassLoader classLoader = MainWindow.class.getClassLoader();
        //ImageIcon flatSVGIcon = new FlatSVGIcon("ImageIcon/links-fill.svg", 24, 24, classLoader);
        JButton newFileNameBtn = new JButton("    使用旧文件名    ");
        newFileNameBtn.setFont(xingkai);
        newFileNameBtn.setActionCommand("use old file name");
        newFileNameBtn.addActionListener(this);
        newFileNamePanel.add(newFileNameLabel);
        newFileNamePanel.add(newFileNameTextField);
        newFileNamePanel.add(newFileNameBtn);

        // 备份或还原选择面板
        BorRPanel = new JPanel();
        BorRPanel.setLayout(new BoxLayout(BorRPanel, BoxLayout.X_AXIS));
        JLabel BorRLabel = new JLabel("操作:");
        BorRLabel.setFont(kaiti);
        backupBtn = new JRadioButton("备份", true);
        backupBtn.setFont(kaiti);
        backupBtn.setActionCommand("backup");
        backupBtn.addActionListener(this);
        restoreBtn = new JRadioButton("还原", false);
        restoreBtn.setFont(kaiti);
        restoreBtn.setActionCommand("restore");
        restoreBtn.addActionListener(this);
        ButtonGroup _bg = new ButtonGroup();
        _bg.add(backupBtn);
        _bg.add(restoreBtn);
        BorRPanel.add(BorRLabel);
        BorRPanel.add(backupBtn);
        BorRPanel.add(restoreBtn);

        // 元数据选择面板
        attributesPanel = new JPanel();
        attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.X_AXIS));
        JLabel attributesLabel = new JLabel("元数据拷贝选择:");
        attributesLabel.setFont(kaiti);
        owner = new JCheckBox("属主");
        time = new JCheckBox("时间");
        acl = new JCheckBox("权限"); // access control list 访问控制列表 => 权限
        owner.setFont(kaiti);
        time.setFont(kaiti);
        acl.setFont(kaiti);
        attributesPanel.add(attributesLabel);
        attributesPanel.add(owner);
        attributesPanel.add(time);
        attributesPanel.add(acl);

        // 筛选面板
        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
        JButton setFilterBtn = new JButton("设置筛选器");
        setFilterBtn.setFont(kaiti);
        setFilterBtn.setActionCommand("set filter");
        setFilterBtn.addActionListener(this);
        JButton clearFilterBtn = new JButton("重置筛选器");
        clearFilterBtn.setFont(kaiti);
        clearFilterBtn.setActionCommand("clear filter");
        clearFilterBtn.addActionListener(this);
        filterPanel.add(setFilterBtn);
        filterPanel.add(clearFilterBtn);

        // 压缩面板
        compressPanel = new JPanel();
        compressPanel.setLayout(new BoxLayout(compressPanel, BoxLayout.X_AXIS));
        JLabel compressLabel = new JLabel("压缩打包:");
        compressLabel.setFont(kaiti);
        compressBtn = new JRadioButton("压缩", false);
        compressBtn.setFont(kaiti);
        decompressBtn = new JRadioButton("解压", false);
        decompressBtn.setFont(kaiti);
        noCompressBtn = new JRadioButton("无", true);
        noCompressBtn.setFont(kaiti);
        ButtonGroup compressbg = new ButtonGroup();
        compressbg.add(compressBtn);
        compressbg.add(decompressBtn);
        compressbg.add(noCompressBtn);
        compressPanel.add(compressLabel);
        compressPanel.add(compressBtn);
        compressPanel.add(decompressBtn);
        compressPanel.add(noCompressBtn);

        // 加密面板
        encryptionPanel = new JPanel();
        encryptionPanel.setLayout(new BoxLayout(encryptionPanel, BoxLayout.X_AXIS));
        JLabel encryptionLabel = new JLabel("加密解密:");
        encryptionLabel.setFont(kaiti);
        encryptBtn = new JRadioButton("加密", false);
        encryptBtn.setActionCommand("encrypt");
        encryptBtn.addActionListener(this);
        encryptBtn.setFont(kaiti);
        decryptBtn = new JRadioButton("解密", false);
        decryptBtn.setActionCommand("decrypt");
        decryptBtn.addActionListener(this);
        decryptBtn.setFont(kaiti);
        noEncryptionBtn = new JRadioButton("无", true);
        noEncryptionBtn.setFont(kaiti);
        ButtonGroup encryptionbg = new ButtonGroup();
        encryptionbg.add(encryptBtn);
        encryptionbg.add(decryptBtn);
        encryptionbg.add(noEncryptionBtn);
        encryptionPanel.add(encryptionLabel);
        encryptionPanel.add(encryptBtn);
        encryptionPanel.add(decryptBtn);
        encryptionPanel.add(noEncryptionBtn);


        // 确认面板
        confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.X_AXIS));
        JButton confirmBtn = new JButton("  确认  ");
        confirmBtn.setFont(kaiti);
        confirmBtn.setActionCommand("confirm");
        confirmBtn.addActionListener(this);
        confirmPanel.add(confirmBtn);


        // 总面板控制
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(sourcePanel);
        panel.add(backupPanel);
        panel.add(newFileNamePanel);
        panel.add(BorRPanel);
        panel.add(attributesPanel);
        panel.add(filterPanel);
        panel.add(compressPanel);
        panel.add(encryptionPanel);
        panel.add(confirmPanel);
        ClassLoader classLoader = MainWindow.class.getClassLoader();
        ImageIcon flatSVGIcon = new FlatSVGIcon("ImageIcon/send-backward.svg", 12, 12, classLoader);
        this.setIconImage(flatSVGIcon.getImage());
        backupBtn.doClick();
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
            JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory(), FileSystemView.getFileSystemView());
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
        }else if ( e.getActionCommand().equals("use old file name") ) {
            // 点击使用旧文件名按钮
            String str = sourceTextField.getText();
            // 路径为空或文件不存在 则弹窗报错
            if ( str == null || str.equals("") )
            {
                ErrorDialog notNullDialog = new ErrorDialog(this, "error", true, 1);
                return ;
            }
            File tmpFile = new File(str);
            if (!tmpFile.exists())
            {
                ErrorDialog fileNotFoundDialog = new ErrorDialog(this, "error", true, 2);
                return ;
            }
            // 将文件名或目录名粘贴到textField中
            if (tmpFile.isFile()) str = tmpFile.getName();
            else str = tmpFile.getAbsolutePath().substring(tmpFile.getAbsolutePath().lastIndexOf('\\')+1);
            newFileNameTextField.setText(str);
        }else if ( e.getActionCommand().equals("backup") ) {
            // 点击备份 默认选择不压缩不加密 不允许解压、解密
            noCompressBtn.setSelected(true);
            noEncryptionBtn.setSelected(true);
            compressBtn.setEnabled(true);
            encryptBtn.setEnabled(true);
            decompressBtn.setEnabled(false);
            decryptBtn.setEnabled(false);
        }else if ( e.getActionCommand().equals("restore") ) {
            // 点击还原 默认选择不压缩不加密 不允许压缩、加密
            noCompressBtn.setSelected(true);
            noEncryptionBtn.setSelected(true);
            compressBtn.setEnabled(false);
            encryptBtn.setEnabled(false);
            decompressBtn.setEnabled(true);
            decryptBtn.setEnabled(true);
        }else if ( e.getActionCommand().equals("set filter") ) {
            // 点击设置过滤器
            filterDialog = new FilterDialog(this, "Filter", true);
            filter = filterDialog.getFilter();
        }else if ( e.getActionCommand().equals("clear filter")  && filterDialog != null) {
            // 点击重置过滤器
            filter = null;
        }else if ( e.getActionCommand().equals("encrypt")  || e.getActionCommand().equals("decrypt") ) {
            // 点击加密或者解密
            CipherDialog cipherDialog = new CipherDialog(this, "Cipher", true);
            cipher = cipherDialog.getCipher();
        }else if ( e.getActionCommand().equals("confirm") ){
            try {
                // 点击了确认按钮后将信息封装成Parser对象传给 备份或还原函数执行对应操作
                Parser parser = new Parser(sourceTextField.getText(), backupTextField.getText(), newFileNameTextField.getText(),
                        new boolean[]{owner.isSelected(), time.isSelected(), acl.isSelected()}, filter, compressBtn.isSelected(),
                        decompressBtn.isSelected(), cipher, encryptBtn.isSelected(), decryptBtn.isSelected());
                // 备份
                if ( backupBtn.isSelected() ) backup(parser);
                    // 还原 将备份文件复制回源文件夹
                else restore(parser);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchPaddingException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            } catch (InvalidKeyException ex) {
                throw new RuntimeException(ex);
            } catch (InvalidKeySpecException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalBlockSizeException ex) {
                throw new RuntimeException(ex);
            } catch (BadPaddingException ex) {
                throw new RuntimeException(ex);
            }


        }
    }
}

