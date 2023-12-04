package com.kanfs.fileop;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.*;

import static com.kanfs.fileop.AESCoder.decryptCompressFile;
import static com.kanfs.fileop.AESCoder.encryptCompressFile;

public class Packer {
    // 使用自己的打包压缩方法
    public static void packFile(File sourceFile,File backupFile, Parser parser) throws IOException, ParseException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        // 1. DFS递归获取所有文件及文件夹的信息 并获取一个存储有所有要压缩文件的byte列表 统计字符频率
        List<MyFile> fileInfoList = new ArrayList<MyFile>();
        List<Byte> data = new ArrayList<Byte>();
        Map<Byte, Integer> frequencyMap = new HashMap<Byte, Integer>();
        readFile(fileInfoList, data, frequencyMap, sourceFile, "", parser.filter);

        // 2. 构建哈夫曼树(使用优先级队列)
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<HuffmanNode>(Comparator.comparingInt(node -> node.frequency));
        for(Map.Entry<Byte, Integer> entry : frequencyMap.entrySet())
            priorityQueue.add(new HuffmanNode(entry.getKey(), entry.getValue(), true, null, null));
        while ( priorityQueue.size() > 1 )
        {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();
            HuffmanNode parent = new HuffmanNode((byte)0, left.frequency + right.frequency, false, left, right);
            priorityQueue.add(parent);
        }
        HuffmanNode huffmanTree = priorityQueue.poll();

        // 3. 生成哈夫曼编码表
        Map<Byte, String> huffmanCodes = new HashMap<>();
        generateHuffmanCodes(huffmanTree, "", huffmanCodes);

        // 4. 写入压缩文件
        writeCompressFile(backupFile, fileInfoList, huffmanCodes, data, parser.encrypt, parser.cipher);
    }

    public static void unpackFile(File sourceFile,File backupFile, Parser parser) throws IOException, ClassNotFoundException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        List<MyFile> fileInfoList = new ArrayList<>();
        Map<String, Byte> huffmanDecodes = new HashMap<>();

        // 1. ObjectInputStream读取文件中对象信息生成文件信息列表和哈夫曼编码表
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupFile));
        int byteCnt = readFileObjects(ois, fileInfoList, huffmanDecodes);
        ois.close();

        // 2. 文件重构
        restoreFileStructure(sourceFile, fileInfoList, parser.attributes);

        // 3. BufferedInputStream 读取压缩文件data部分
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(backupFile));
        bis.skip(byteCnt);
        byte[] data ;
        data = readByteArrayFromFile(bis);
        if(parser.decrypt) data = decryptCompressFile(data, parser.cipher); // 解密文件如果需要解密的话
        bis.close();

        // 4. 依次还原文件
        writeBackOriginalFile(fileInfoList, huffmanDecodes, data, sourceFile, parser.attributes);
    }

    private static boolean readFile(List<MyFile> fileInfoList, List<Byte> data, Map<Byte, Integer> frequencyMap,
                                    File sourceFile, String path, Filter filter) throws IOException, ParseException {
        boolean flag = false;
        // 创建自身MyFile对象 加入到列表中
        BasicFileAttributes sourceTimeAttributes = Files.readAttributes(sourceFile.toPath(), BasicFileAttributes.class);
        MyFile myFile = new MyFile(sourceFile.getName(), path, false, sourceFile.length(),
                sourceTimeAttributes.creationTime(),
                sourceTimeAttributes.lastModifiedTime(),
                sourceTimeAttributes.lastAccessTime());
        fileInfoList.add(myFile);
        // 目录则向下继续读取
        if (sourceFile.isDirectory())
        {
            // 修改为是目录
            fileInfoList.get(fileInfoList.size()-1).isDirectory = true;
            fileInfoList.get(fileInfoList.size()-1).size = 0;
            // 修改当前相对路径
            path += (File.separator + sourceFile.getName());
            // 递归遍历下层
            File[] files = sourceFile.listFiles();
            for(File file : files)
                flag |= readFile(fileInfoList, data, frequencyMap, file, path, filter);
            if ( !flag ) fileInfoList.remove(fileInfoList.size()-1); // 将目录项删除
            // 退出目录还原到上一级的相对路径
            path = path.substring(0, path.lastIndexOf(File.separator));
        }else{ // 文件则判断是否满足条件
            if ( filter != null && !filter.accept(sourceFile) ) // 不满足则删除
                {fileInfoList.remove(fileInfoList.size()-1);return false;}
            flag = true;
            // 读取文件信息
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = bis.read(buff)) != -1)
            {
                for (int i=0 ; i<len ; i++) // 加入待处理数据中并更新频率表
                {
                    data.add(buff[i]);
                    frequencyMap.put(buff[i], frequencyMap.getOrDefault(buff[i], 0)+1);
                }
            }
            bis.close();
        }
        return flag;
    }

    //  获取哈夫曼编码表
    private static void generateHuffmanCodes(HuffmanNode node, String code, Map<Byte, String> huffmanCodes) {
        if (node == null) return;
        if ( node.flag )     huffmanCodes.put(node.data, code);
        generateHuffmanCodes(node.left, code + "0", huffmanCodes);
        generateHuffmanCodes(node.right,code + "1", huffmanCodes);
    }

    // 构建压缩文件
    private static void writeCompressFile(File backupFile, List<MyFile> fileInfoList, Map<Byte, String> huffmanCodes,
                                          List<Byte> data, boolean encrypt, String password) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(backupFile));
        // 写入oos将要写入的字节数
        oos.writeObject("This software is jointly produced by kanfs and DongZheRuLian.");
        int bytesWritten = getBytesWritten(fileInfoList, huffmanCodes);
        oos.writeInt(bytesWritten);
        // 1. 写入文件信息列表
        oos.writeInt(fileInfoList.size()); // 写MyFile对象个数
        for( MyFile fileInfo : fileInfoList) oos.writeObject(fileInfo); // 将MyFile对象写入

        // 2. 写入哈夫曼编码表
        //oos.write(huffmanCodes.size()); // 写入哈夫曼表项数
        oos.writeObject(huffmanCodes);
        oos.close();

        // 3. 转换为哈夫曼编码
        StringBuilder compressData = new StringBuilder("");
        for( int i=0 ; i<data.size() ; i++ ) compressData.append(huffmanCodes.get(data.get(i)));

        // 4. 处理剩余的不足8位的二进制数据：如果剩下的二进制数据不足8位，可以在末尾添加0并写入文件。
        //  使用BufferedOutputStream写入文件的byte数据加快速度
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(backupFile, true));
        int cnt;
        for (cnt = compressData.length()%8; cnt%8 != 0; cnt++)
            compressData.append("0");

        // 5. 将二进制字符串写入文件：将生成的二进制字符串生成要写入文件的byte数组。
        String binaryData = compressData.toString();
        byte[] buffer = new byte[1024];
        int index = 0;
        while (index <= binaryData.length() - 8)
        {
            buffer[index>>3] = (byte)Integer.parseUnsignedInt(binaryData.substring(index, index + 8), 2);
            index += 8;
            if ((index>>3) >= buffer.length ) //需要扩长
            {
                byte[] newBuffer = new byte[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuffer, 0, (index>>3));
                buffer = newBuffer;
            }
        }
        // 截取实际读取的数据
        byte[] byteArray = new byte[(index>>3)];
        System.arraycopy(buffer, 0, byteArray, 0, (index>>3));

        // 6. 如果需要加密将byte数组加密 然后写入文件。
        if( encrypt ) byteArray = encryptCompressFile(byteArray, password);
        bos.write(byteArray);
        bos.close();
    }

    private static int getBytesWritten(List<MyFile> fileInfoList, Map<Byte, String> huffmanCodes) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(byteStream);

            // 在新的ObjectOutputStream中写入相同的数据
            oos.writeObject("This software is jointly produced by kanfs and DongZheRuLian.");
            // 1. 写入文件信息列表
            oos.writeInt(0); // 写BytesWritten
            oos.writeInt(fileInfoList.size()); // 写MyFile对象个数
            for( MyFile fileInfo : fileInfoList) oos.writeObject(fileInfo); // 将MyFile对象写入

            // 2. 写入哈夫曼编码表
            //oos.write(huffmanCodes.size()); // 写入哈夫曼表项数
            oos.writeObject(huffmanCodes);

            // 获取写入的字节数
            int bytesWritten = byteStream.size();

            oos.close();
            return bytesWritten;
        } catch (IOException e ) {
            e.printStackTrace();
            return -1; // 错误情况下返回-1
        }
    }

    // 读取文件中的对象信息 并返回对象部分字节数
    private static int readFileObjects(ObjectInputStream in, List<MyFile> fileInfoList, Map<String, Byte> huffmanDecodes) throws IOException, ClassNotFoundException {
        String word = (String) in.readObject();
        System.out.println(word);

        // 读取对象信息字节大小
        int byteCnt = in.readInt();

        // 读取MyFile文件对象
        int myFileCnt = in.readInt(); // MyFile对象个数
        for(int i=0 ; i<myFileCnt ; i++ )
            fileInfoList.add((MyFile) in.readObject());

        // 读取哈夫曼编码表
        Map<Byte, String> tmpHuffmanCodes = (Map<Byte, String>) in.readObject();
        for( Map.Entry<Byte, String> entry : tmpHuffmanCodes.entrySet() ) huffmanDecodes.put(entry.getValue(), entry.getKey());
        return byteCnt;
    }

    // 恢复文件结构
    private static void restoreFileStructure(File sourceFile, List<MyFile> fileInfoList, boolean[] attributes) throws IOException {
        if (!sourceFile.exists()) sourceFile.mkdir();
        String rootPath = sourceFile.getPath();
        for (int i = 0; i < fileInfoList.size(); i++)
        {
            File file = new File(rootPath+fileInfoList.get(i).path+File.separator+fileInfoList.get(i).name);
            if ( fileInfoList.get(i).isDirectory ) file.mkdir();
            else file.createNewFile();
            if ( attributes[1] )
            {
                FileTime creationTime = fileInfoList.get(i).creationTime;
                FileTime lastModifiedTime = fileInfoList.get(i).lastModifiedTime;
                FileTime lastAccessTime = fileInfoList.get(i).lastAccessTime;
                Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class).setTimes(lastModifiedTime, lastAccessTime, creationTime);
            }
        }
    }

    // 回复文件数据
    private static void writeBackOriginalFile(List<MyFile> fileInfoList, Map<String, Byte> huffmanDecodes, byte[] data,
                                              File sourceFile, boolean[] attributes) throws IOException {
        StringBuilder encoderBits = new StringBuilder();
        // 先转化回字符串
        for (int i = 0; i < data.length; i++)
        {
            String str = String.format("%8s", Integer.toBinaryString(data[i])).replace(" ", "0");
            encoderBits.append(str.length()==8?str:str.substring(24));
        }
        char[] encodedBits = encoderBits.toString().toCharArray();
        StringBuilder currentBits = new StringBuilder();
        for( int i=0, j=0 ; j<fileInfoList.size() ; j++)
        {
            if( fileInfoList.get(j).isDirectory ) continue;
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(sourceFile.getPath()+fileInfoList.get(j).path+File.separator+fileInfoList.get(j).name));
            List<Byte> decodedBytes = new LinkedList<>();
            int bytesWrite = 0;
            while ( bytesWrite < fileInfoList.get(j).size )
            {
                currentBits.append(encodedBits[i]);
                i += 1;
                if( huffmanDecodes.containsKey(currentBits.toString()) )
                {
                    decodedBytes.add(huffmanDecodes.get(currentBits.toString()));
                    currentBits = new StringBuilder();
                    bytesWrite += 1;
                }
            }
            byte[] result = new byte[bytesWrite];
            for(int k=0 ; k<bytesWrite ; k++ ) result[k] = decodedBytes.get(k);
            bos.write(result);
            bos.close();
        }
    }

    private static byte[] readByteArrayFromFile(InputStream fileInputStream) throws IOException {
        byte[] buffer = new byte[1024]; // 缓冲区
        int bytesRead;
        int totalBytes = 0;
        while ((bytesRead = fileInputStream.read(buffer, totalBytes, buffer.length - totalBytes)) != -1) {
            totalBytes += bytesRead;
            if (totalBytes == buffer.length) {
                // 扩展缓冲区大小
                byte[] newBuffer = new byte[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuffer, 0, totalBytes);
                buffer = newBuffer;
            }
        }
        fileInputStream.close();
        // 截取实际读取的数据
        byte[] byteArray = new byte[totalBytes];
        System.arraycopy(buffer, 0, byteArray, 0, totalBytes);
        return byteArray;
    }

    // 获取对象对应byte[]大小
    private static int getObjectSize(Object o) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream tempObjectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        tempObjectOutputStream.writeObject(o);
        tempObjectOutputStream.flush();
        tempObjectOutputStream.close();
        return byteArrayOutputStream.toByteArray().length;
    }

//    @Test
//    public void test() throws ParseException, IOException, ClassNotFoundException {
//        packFile(new File("C:\\Users\\kanfs\\Desktop\\SoftwareDevelopment\\modaoshi"), new File("C:\\Users\\kanfs\\Desktop\\SoftwareDevelopment\\modaoshi.kan"),
//                new boolean[]{false, false, false}, null);
//        unpackFile(new File("C:\\Users\\kanfs\\Desktop\\SoftwareDevelopment\\mofashi"), new File("C:\\Users\\kanfs\\Desktop\\SoftwareDevelopment\\modaoshi.kan"),
//                new boolean[]{false, false, false}, null);
//    }

}

class HuffmanNode{
    byte data;
    int frequency;
    boolean flag;
    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode(byte data, int frequency,boolean flag, HuffmanNode left, HuffmanNode right) {
        this.data = data;
        this.frequency = frequency;
        this.flag = flag;
        this.left = left;
        this.right = right;
    }
}

/*
    // zip打包复制文件(夹)
    public static void packageZip(File sourceFile,File backupFile, boolean[] attributes, Filter filter) throws IOException, ParseException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFile));
        if (sourceFile.isDirectory()) zipCopyDirectory(sourceFile, zos, "", attributes, filter, true);
        else zipCopyFile(sourceFile, zos, attributes, filter);
        zos.close();
    }

    // DFS递归实现zip打包
    private static boolean zipCopyDirectory(File sourceFile, ZipOutputStream zos, String filePath, boolean[] attributes,
                                            Filter filter, boolean flag) throws IOException, ParseException {
        boolean fileCopied = false;
        // 获取源文件夹下的所有子文件和子文件夹
        File[] files = sourceFile.listFiles();
        // 首次为选中的文件夹，即根目录，之后递归实现拼接目录
        filePath = flag ? sourceFile.getName() : filePath + File.separator + sourceFile.getName();
        for( File file : files ) // 进入递归,flag置false 即当前文件夹下仍包含文件夹
        {
            if ( file.isDirectory() )
                fileCopied |= zipCopyDirectory(file, zos, filePath, attributes, filter, false);
            else{
                // 查看是否通过筛选器
                if ( filter != null && !filter.accept(sourceFile) ) return false;
                fileCopied = true;

                // 文件复制
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                ZipEntry zipEntry = new ZipEntry(filePath + File.separator + file.getName());
                zos.putNextEntry(zipEntry);
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = bis.read(buff)) != -1)
                    zos.write(buff, 0, len);
                bis.close();
            }
        }
        return fileCopied;

    }

    // zip压缩单个文件
    private static void zipCopyFile(File sourceFile, ZipOutputStream zos, boolean[] attributes, Filter filter) throws IOException, ParseException {
        // 查看是否通过筛选器
        if ( filter != null && !filter.accept(sourceFile) ) return ;

        // 文件复制
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
        ZipEntry zipEntry = new ZipEntry(sourceFile.getPath());
        zos.putNextEntry(zipEntry);
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = bis.read(buff)) != -1)
            zos.write(buff, 0, len);
        bis.close();
    }
*/