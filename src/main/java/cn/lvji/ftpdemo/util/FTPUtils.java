package cn.lvji.ftpdemo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.regex.Pattern;

/**
 * @author linyunrui
 * @date 2019/8/13 11:00
 */
@Slf4j
public class FTPUtils {

    private FTPClient ftpClient;

    private String charsetName = "GBK";

    private final Pattern p = Pattern.compile("\"(.*?)\"");
    /**
     * 对象构造 设置将过程中使用到的命令输出到控制台
     */

    /**
     *
     * java编程中用于连接到FTP服务器
     *
     * @param hostname
     *      主机名
     *
     * @param port
     *      端口
     *
     * @param username
     *      用户名
     *
     * @param password
     *      密码
     *
     * @return 是否连接成功
     *
     * @throws IOException
     */

    public boolean connect(String hostname, int port, String username, String password) throws Exception {
        ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        try {
            ftpClient.connect(hostname, port);
        }catch (Exception e){
            throw new Exception("连接异常,请检查主机端口");
        }

        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            //设置被动模式
            ftpClient.enterLocalPassiveMode();
            //设置终端和服务器建立连接这个过程的超时时间。
            ftpClient.setConnectTimeout(3000 * 10);
            //设置传输命令的socket建立连接超时时间
            ftpClient.setSoTimeout(3000 * 10);

            if (ftpClient.login(username, password)){

                return true;
            }
            throw new Exception("登陆失败,请检查账号密码");
        }
        disconnect();
        return false;

    }

    /**
     *
     * 断开与远程服务器的连接
     *
     * @throws IOException
     */

    public void disconnect() throws IOException {
        if (ftpClient != null && ftpClient.isConnected()){
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    /**
     * 查看文件列表
     * @param filedir ftp服务文件路径
     * @return 所有文件列表
     * @throws IOException
     */
    public String[] getFileList(String filedir) throws IOException {
        String[] files = ftpClient.listNames(filedir);
        for (int i = 0; i < files.length; i++) {
            byte[] bytes = files[i].getBytes("iso-8859-1");
            files[i] = new String(bytes,charsetName);
        }
        return files;
    }


    /**
     *
     * 上传文件到FTP服务器，支持断点续传
     *
     * @param f
     *      临时文件
     *
     * @param remoteFileName 文件名称
     *
     * @return 上传结果
     *
     * @throws IOException
     */

    @SuppressWarnings("resource")
    public FTPStatus upload(File f, String remoteFileName) throws Exception {

        // 设置PassiveMode传输
        ftpClient.enterLocalPassiveMode();
        // 设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        //定义枚举类
        FTPStatus result;


        // 检查远程是否存在文件
        FTPFile[] files = ftpClient.listFiles(remoteFileName);


        if (files.length == 1) {

            long remoteSize = files[0].getSize();
            long localSize = f.length();
            if (remoteSize == localSize) {
                return FTPStatus.File_Exits;

            } else if (remoteSize > localSize) {

                //远程文件大于本地文件
                return FTPStatus.Remote_Bigger_Local;

            }

            // 尝试移动文件内读取指针,实现断点续传
            InputStream is = new FileInputStream(f);

            if (is.skip(remoteSize) == remoteSize){

                ftpClient.setRestartOffset(remoteSize);

                if (ftpClient.storeFile(remoteFileName, is)){

                    //断点续传成功
                    return FTPStatus.Upload_From_Break_Success;

                }

            }

            // 如果断点续传没有成功，则删除服务器上文件，重新上传

            if (!ftpClient.deleteFile(remoteFileName)) {

                //删除文件失败
                return FTPStatus.Delete_Remote_Faild;

            }

            is = new FileInputStream(f);
            if (ftpClient.storeFile(remoteFileName, is)){

                result = FTPStatus.Upload_New_File_Success;

            } else {

                result = FTPStatus.Upload_New_File_Failed;

            }

            is.close();

        } else {

            InputStream is = new FileInputStream(f);

            if (ftpClient.storeFile(remoteFileName, is)) {

                result = FTPStatus.Upload_New_File_Success;

            } else {

                result = FTPStatus.Upload_New_File_Failed;
            }
            is.close();
        }
        return result;
    }

    /**
     * 创建文件目录
     * @param remote 切换路径
     * @return
     * @throws IOException
     */
    public FTPStatus createDirecrotyAndChangeWorking(String remote) throws IOException {
        if (!ftpClient.changeWorkingDirectory(new String(remote.getBytes(charsetName),"iso-8859-1"))) {
            return FTPStatus.Directory_faile_Changed;
        }
        return FTPStatus.Directory_Successfully_Changed;
    }


    public boolean changeToParentDirectory() throws IOException {
        return ftpClient.changeToParentDirectory();
    }
}
