package cn.lvji.ftpdemo.controller;

import cn.lvji.ftpdemo.ftp.FTPStatus;
import cn.lvji.ftpdemo.ftp.FTPUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author linyunrui
 * @date 2019/8/15 15:53
 */
@RestController
@Scope(scopeName = "session")
public class FTPController {

    private FTPUtils ftpUtils = new FTPUtils();

    @Value("${ftp.host}")
    private String host;
    @Value("${ftp.port}")
    private String port;


    @ApiOperation(value = "ftp服务登陆",notes = "登陆成功返回true,否则返回false")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName",value = "账号"),
            @ApiImplicitParam(name = "passWord",value = "密码")
    })
    @GetMapping("/connect")
    public Boolean ftpConnect(String userName,String passWord) throws Exception {
       return ftpUtils.connect(host,Integer.valueOf(port),userName,passWord);
    }

    @ApiOperation(value = "获取指定路径下的所有文件",notes = "传参不需要\"\" , 跟目录下直接传入文件夹名称(注:不要带/) , 点入下级目录后把路径带上则再次进入下级目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filedir",value = "文件路径"),
    })
    @GetMapping("/getFilesList")
    public String[] getFilesList(String filedir) throws IOException {
        if (StringUtils.isEmpty(filedir)){
            filedir = "/";
        }
        filedir = new String(filedir.getBytes("UTF-8"),"iso-8859-1");
        return ftpUtils.getFileList(filedir);
    }

    @ApiOperation(value = "上传文件",notes = "先切换到指定上传的路径再上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "local",value = "文件路径名称"),
            @ApiImplicitParam(name = "fileName",value = "文件名称"),
    })
    @GetMapping("/upload")
    public FTPStatus uploadFile(String local, String fileName) throws Exception {
       return ftpUtils.upload(local, fileName);
    }

    @ApiOperation(value = "断开连接")
    @GetMapping("disconnect")
    public boolean disconnect(){
        try {
            ftpUtils.disconnect();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @GetMapping("changeWorking")
    @ApiOperation(value = "切换当前工作路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remote",value = "切换的路径"),
    })
    public FTPStatus changeWorking(String remote) throws IOException {
        return ftpUtils.createDirecrotyAndChangeWorking(remote);
    }

    @GetMapping("changeToParentDirectory")
    @ApiOperation(value = "切换到上级路径")
    public boolean changeToParentDirectory() throws IOException {
        return ftpUtils.changeToParentDirectory();
    }

}
