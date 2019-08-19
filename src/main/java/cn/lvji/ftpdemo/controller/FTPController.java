package cn.lvji.ftpdemo.controller;

import cn.lvji.ftpdemo.util.FTPStatus;
import cn.lvji.ftpdemo.util.FTPUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

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
    @PostMapping("/upload")
    public FTPStatus uploadFile(HttpServletRequest request) throws Exception {
        if (request instanceof MultipartRequest) {
            MultipartFile file = getFileFormRequest(request);
            String fileName = file.getOriginalFilename();
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            File tempFile = File.createTempFile(String.valueOf(UUID.randomUUID()), prefix);
            file.transferTo(tempFile);
            FTPStatus upload = ftpUtils.upload(tempFile, fileName);
            deleteFile(tempFile);
            return upload;
        }
        throw new Exception("非文件上传请求");
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


    /**
     * 从请求（HttpServletRequest）中获取文件
     * <p>
     * 注意，只会获取请求中的第一个文件
     * </p>
     *
     * @param request 携带文件的请求
     * @return 文件（File)
     */
    private MultipartFile getFileFormRequest(HttpServletRequest request) {
        MultipartRequest multipartRequest = (MultipartRequest) request;
        MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();
        Iterator<String> fileNames = multipartRequest.getFileNames();
        MultipartFile file = null;
        if (fileNames.hasNext()) {
            file = multiFileMap.getFirst(fileNames.next());
        }
        return file;
    }

    /**
     * 删除文件
     * @param files
     */
    private void deleteFile(File... files){
        for (File file : files) {
            if (file.exists()){
                file.delete();
            }
        }
    }
}
