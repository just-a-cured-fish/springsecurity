package com.yxh.springsecurity.Controller;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.yxh.springsecurity.bean.Node;
import com.yxh.springsecurity.bean.Result;
import com.yxh.springsecurity.bean.txtcontent;
import com.yxh.springsecurity.biz.FileBiz;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/file")
public class Filecontroller {
    @Autowired
    private FileBiz filebiz;

    @GetMapping("/getuser")
    public String getuser(HttpServletRequest request){
        return (String)request.getSession().getAttribute("username");
    }
    @GetMapping("/getroot")
    public List<Node> getfile() throws Exception {

        return filebiz.query("/");
    }
    @RequestMapping(value = "/precede", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> Test02(String path) throws Exception {
        //下面两行，初始化hdfs配置连接
        ResponseEntity<InputStreamResource> result = filebiz.preFile(path);
        return result;
    }
    @GetMapping(value = "/gettxt")
    public txtcontent gettxt(String path,int count) throws Exception {
        txtcontent txt=new txtcontent();
        txt=filebiz.read(path,count,count+10);



        return txt;
    }
    @GetMapping(value = "/getpic")
    @ResponseBody
    public String createFolw(HttpServletRequest request,
                             HttpServletResponse response,String path) throws InterruptedException, IOException, URISyntaxException {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("contentType", "text/html; charset=utf-8");
        InputStream a=filebiz.down1(path);
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((count = a.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            a.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }
    @RequestMapping(value = "/getFileStream", method = RequestMethod.GET)
    public void pdfStreamHandler(HttpServletRequest request, HttpServletResponse response) {
        String fileSrc="C:\\Users\\DELL\\Desktop\\2.png";
        File file = new File("C:\\Users\\DELL\\Desktop\\2.png");
        try {
            byte[] data = null;
            try {
                String strUrl = fileSrc.trim();
                URL url=new URL(strUrl);
                //打开请求连接
                URLConnection connection = url.openConnection();
                HttpURLConnection httpURLConnection=(HttpURLConnection) connection;
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                // 取得输入流，并使用Reader读取
                int responseCode=((HttpURLConnection) connection).getResponseCode();
                BufferedInputStream bis=null;
                bis = new BufferedInputStream(httpURLConnection.getInputStream());
                response.setCharacterEncoding("UTF-8");
                response.setHeader("contentType", "text/html; charset=utf-8");
                ServletOutputStream sos = response.getOutputStream();
                //BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
                int b;
                while((b = bis.read())!=-1) {
                    sos.write(b);
                }
                sos.close();
                bis.close();
            } catch (Exception e) {
            }
        }
        catch (Exception e) {
            return;
        }
    }
    @GetMapping("/getPath")
    public List<Node> getfile(String path) throws Exception {
        return filebiz.query(path);
    }
    @GetMapping("/download")
    public void download( String path,String localpath) throws Exception {
        filebiz.download(path,localpath);

    }
    @RequestMapping(value = "/down", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> Test01(String path) throws Exception {
        //下面两行，初始化hdfs配置连接
        ResponseEntity<InputStreamResource> result = filebiz.downloadFile(path);
        return result;
    }

    @RequestMapping(value = "/downloadFolder", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadFolder(String path) throws Exception {
        ResponseEntity<byte[]> response = null;
        String filename=path.substring(path.lastIndexOf("/")+1);
        System.out.println(filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition","attachment;filename*=utf-8'zh_cn'"+ URLEncoder.encode(filename,"utf-8"));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Language", "UTF-8");
        headers.add("Content-type","application/octet-stream;charset=UTF-8");
        ByteArrayOutputStream zos =
                (ByteArrayOutputStream) filebiz.down2(path);
        byte[] out = zos.toByteArray();
        zos.close();
        response = new ResponseEntity<>(out, headers, HttpStatus.OK);

        return response;
    }

    @PostMapping("/upload")
    @ResponseBody
    public Result handleFileUpload(@RequestParam("file") MultipartFile file, String path) {
        Node node=new Node();
        Result result=new Result();
        if (!file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(
                                new File(originalFilename)
                        )
                );
                out.write(file.getBytes());
                out.flush();
                out.close();
                filebiz.upload(originalFilename, path);
                String filename=originalFilename.substring(originalFilename.lastIndexOf("/")+1);
               // System.out.println(file.getName());
               // System.out.println(originalFilename.substring(originalFilename.lastIndexOf("/")+1));
                node.setPath(path+"/"+filename);
                node.setName(filename);
                node.setType("file");
                result.setData(node);
                result.setCode(1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;

        } else {
            result.setCode(0);
            return result;
        }

    }

    @GetMapping("/delete")
    public boolean delete( String path) throws Exception {
        return filebiz.delete(path);
    }
    @GetMapping("/mkdir")
    public Node mkdir( String path){
        try {
            return filebiz.mkdir(path);

        }catch (Exception e){

        }
        return null;
    }
    @GetMapping("/rename")
    public boolean getfile(String path,String name) throws Exception {
        System.out.println(path);
        System.out.println(name);
        try {
            return filebiz.rename(path, name);
        }catch (Exception e){
        }
        return false;
    }
    @GetMapping("/move")
    public boolean move(String path,String path2) throws Exception {
        System.out.println(path);
        System.out.println(path2);
        try {
            return filebiz.move(path, path2);
        }catch (Exception e){

        }
        return false;
    }
}
