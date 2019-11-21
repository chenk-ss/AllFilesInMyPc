package com.ck.files.Controller;

import com.ck.files.entity.PathWithCheck;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author chenk
 * @create 2019/3/25 13:49
 */
@Controller
public class FileController {


    @ResponseBody
    @RequestMapping(value = "/download")
    public ResponseEntity<byte[]> download(HttpServletRequest request,
                                           @RequestParam("filename") String filename,
                                           Model model) {
        System.out.println("Downloading......");
        //下载文件路径
        System.out.println("file:" + filename);
        try {
            File file = new File(filename);
            HttpHeaders headers = new HttpHeaders();
            //下载显示的文件名，解决中文名称乱码问题
            String downloadFileName = new String(file.getName().getBytes("UTF-8"), "iso-8859-1");
            //通知浏览器以attachment（下载方式）打开图片
            headers.setContentDispositionFormData("attachment", downloadFileName);
            //application/octet-stream ： 二进制流数据（最常见的文件下载）。
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                    headers, HttpStatus.CREATED);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/")
    public String queryAllDisks(Model m) {
        List<PathWithCheck> result = new ArrayList<PathWithCheck>();
        File[] roots = File.listRoots();// 获取磁盘分区列表
        PathWithCheck entity;
        for (File file : roots
        ) {
            entity = new PathWithCheck();
            entity.setPath(file.getPath());
            entity.setFile(false);
            result.add(entity);
        }
        m.addAttribute("path", null);
        m.addAttribute("resultList", result);
        return "Path";
    }

    @RequestMapping("/query/all")
    public String queryAll(Model m, @RequestParam("path") String path) {
        List<PathWithCheck> result = new ArrayList<PathWithCheck>();
        System.out.println("path:" + path);
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return "null";
        }
        List<PathWithCheck> fileNames = new ArrayList<PathWithCheck>();
        findFileList(dir, fileNames);
        path = path.replace('\\', '/');
        m.addAttribute("path", path + "/");
        m.addAttribute("resultList", fileNames);
        return "Path";
    }

    public void findFileList(File dir, List<PathWithCheck> fileNames) {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        PathWithCheck entity;
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < files.length; i++) {// 循环，添加文件名或回调自身
            File file = new File(dir, files[i]);
            entity = new PathWithCheck();
            entity.setFile(file.isFile());
//            entity.setPath(dir + "/" + file.getName());
            entity.setPath(file.getName());
            fileNames.add(entity);// 添加文件全路径名
        }
    }

    @RequestMapping("/goBack")
    public String goBack(Model m, @RequestParam("path") String path) {
        System.out.println("osName:" + System.getProperty("os.name"));
        if (System.getProperty("os.name").contains("Windows") && path.split("://").length == 1)
            return queryAllDisks(m);
        if (System.getProperty("os.name").contains("Linux") && ("".equals(path) || "//".equals(path)))
            return queryAllDisks(m);
        File file = new File(path);
        System.out.println("-----not root-----");
        return queryAll(m, file.getParentFile().getPath());
    }
}
