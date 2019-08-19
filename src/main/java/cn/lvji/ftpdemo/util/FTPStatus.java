package cn.lvji.ftpdemo.util;

/**
 * @author linyunrui
 * @date 2019/8/13 10:54
 */
public enum FTPStatus {
    /**
     * 文件已存在
     */
    File_Exits(0,"file Exits"),
    /**
     * 创建文件夹成功
     */
    Directory_Successfully_Changed(1,"Directory successfully changed"),
    /**
     * 创建文件夹失败
     */
    Directory_faile_Changed(2,"Directory failed Changed"),
    /**
     * 上传断点成功
     */
    Upload_From_Break_Success(3,"Upload From Break Success"),
    /**
     * 上传断点失败
     */
    Upload_From_Break_Faild(4,"Upload From Break Faild"),
    /**
     * 下载断点成功
     */
    Download_From_Break_Success(5,"Download From Break Success"),
    /**
     * 下载断点失败
     */
    Download_From_Break_Faild(6,"Download From Break Faild"),
    /**
     * 上传文件成功
     */
    Upload_New_File_Success(7,"Upload New File Success"),
    /**
     * 上传文件失败
     */
    Upload_New_File_Failed(8,"Upload New File failed"),
    /**
     * 删除文件成功
     */
    Delete_Remote_Success(9,"Delete Rmoete Success"),
    /**
     * 删除文件失败
     */
    Delete_Remote_Faild(10,"Delete Remote Faild"),
    Remote_Bigger_Local(11,"Remote Bigger Local"),
    Remote_smaller_local(12,"Remote Smaller Local");

    /**
     * 上传状态
     */
    private int status;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    FTPStatus(int status,String msg){
        this.status = status;
        this.msg = msg;
    }
}