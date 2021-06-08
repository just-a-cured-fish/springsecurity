package com.yxh.springsecurity.biz;

import com.yxh.springsecurity.auth.DemoAuthenticationFailureHandler;
import com.yxh.springsecurity.bean.Node;
import com.yxh.springsecurity.bean.txtcontent;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.io.IOUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component

public class FileBiz {

    Configuration configuration;
    FileSystem fileSystem;
    @Value("${hdfs.path}")
    private String HDFS_PATH;
    @Value("${hdfs.username}")
    private String username;
    private Logger log = LoggerFactory.getLogger(FileBiz.class);

    public void getFileSystem() throws Exception {
        configuration=new Configuration();
        fileSystem=FileSystem.get(new URI(HDFS_PATH),configuration,username);
    }
    public void closeSystem() throws Exception {
        fileSystem.close();
    }
    public  Node  mkdir(String path) throws  Exception{
        getFileSystem();
        Node node=new Node();
        boolean result=fileSystem.mkdirs((new Path(HDFS_PATH+"/"+path)));
        node.setName(path.substring(path.lastIndexOf("/")+1));
        node.setPath(path);
        closeSystem();
        return node;
    }

    public void write() throws Exception{
        FSDataOutputStream fsDataInputStream=fileSystem.create(new Path("/idea/hello.txt"));
        fsDataInputStream.writeUTF("hello IDEA!this is the first file in hadoop,你好世界");
        fsDataInputStream.close();
    }


    public InputStream down1(String cloudPath) throws IOException, InterruptedException, URISyntaxException {

// 1获取对象

        Configuration conf = new Configuration();

        FileSystem fs = FileSystem.get(new URI(HDFS_PATH), conf, "root");
        System.out.println(cloudPath);
        FSDataInputStream in = fs.open(new Path(cloudPath));

        return in;

    }
    public  ResponseEntity<InputStreamResource> preFile( String path) throws Exception {
        configuration=new Configuration();
        fileSystem=FileSystem.get(new URI(HDFS_PATH),configuration,username);
        FSDataInputStream in = fileSystem.open(new Path(path));
        String fileName=path.substring(path.lastIndexOf("/")+1);
        try {
            byte[] testBytes = new byte[in.available()];
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("inline; filename=\"%s\"", fileName));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Language", "UTF-8");
            //最终这句，让文件内容以流的形式输出
            return ResponseEntity.ok().headers(headers).contentLength(testBytes.length)
                    .contentType(MediaType.parseMediaType(getContentType(path))).body(new InputStreamResource(in));
        } catch (IOException e) {
            log.info("downfile is error" + e.getMessage());
        }finally {

        }
        log.info("file is null" + fileName);
        return null;
    }
    public static  String getContentType(String fileName){
        //文件名后缀
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if(".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if(".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if(".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)  || ".png".equalsIgnoreCase(fileExtension) ){
            return "image/jpeg";
        }
        if(".html".equalsIgnoreCase(fileExtension)){
            return "text/html";
        }
        if(".txt".equalsIgnoreCase(fileExtension)){
            return "text/plain";
        }
        if(".vsd".equalsIgnoreCase(fileExtension)){
            return "application/vnd.visio";
        }
        if(".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if(".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if(".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        if(".doc".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if(".docx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        if(".pdf".equalsIgnoreCase(fileExtension)) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }

    public  ResponseEntity<InputStreamResource> downloadFile( String path) throws Exception {
        configuration=new Configuration();
        fileSystem=FileSystem.get(new URI(HDFS_PATH),configuration,username);
        FSDataInputStream in = fileSystem.open(new Path(path));
        String fileName=path.substring(path.lastIndexOf("/")+1);
        try {
            byte[] testBytes = new byte[in.available()];
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Language", "UTF-8");
            //最终这句，让文件内容以流的形式输出
            return ResponseEntity.ok().headers(headers).contentLength(testBytes.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(in));
        } catch (IOException e) {
            log.info("downfile is error" + e.getMessage());
        }finally {

        }
        log.info("file is null" + fileName);
        return null;
    }

    public OutputStream down2(String cloudPath) throws Exception {
        getFileSystem();
        // 1获取对象
        ByteArrayOutputStream out = null;
//        cloudPath=HDFS_PATH+"/"+cloudPath;
        System.out.println(cloudPath);
        try {

            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(new URI(HDFS_PATH), conf);
            out = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(out);
            compress(cloudPath, zos, fs);
            zos.close();
        } catch (IOException e) {
            log.info("----error:{}----" + e.getMessage());
        } catch (URISyntaxException e) {
            log.info("----error:{}----" + e.getMessage());
        }finally {
            closeSystem();
        }
        return out;
    }
    public void compress(String baseDir, ZipOutputStream zipOutputStream, FileSystem fs) throws IOException {

        try {
            FileStatus[] fileStatulist = fs.listStatus(new Path(baseDir));
            log.info("basedir = " + baseDir);
            String[] strs = baseDir.split("/");
            //lastName代表路径最后的单词
            String lastName = strs[strs.length - 1];

            for (int i = 0; i < fileStatulist.length; i++) {

                String name = fileStatulist[i].getPath().toString();
                name = name.substring(name.indexOf("/" + lastName));

                if (fileStatulist[i].isFile()) {
                    Path path = fileStatulist[i].getPath();
                    FSDataInputStream inputStream = fs.open(path);
                    zipOutputStream.putNextEntry(new ZipEntry(name.substring(1)));
                    IOUtils.copyBytes(inputStream, zipOutputStream, Integer.parseInt("1024"));
                    inputStream.close();
                } else {
                    zipOutputStream.putNextEntry(new ZipEntry(fileStatulist[i].getPath().getName() + "/"));
                    log.info("fileStatulist[i].getPath().toString() = " + fileStatulist[i].getPath().toString());
                    compress(fileStatulist[i].getPath().toString(), zipOutputStream, fs);
                }
            }
        } catch (IOException e) {
            log.info("----error:{}----" + e.getMessage());
        }
    }

    public txtcontent read(String path, int start, int end) throws Exception{
        getFileSystem();
        FSDataInputStream fsDataInputStream=fileSystem.open(new Path(path));
        txtcontent txt=new txtcontent();
        InputStreamReader isr=new InputStreamReader(fsDataInputStream);
        BufferedReader br=new BufferedReader(isr);
        StringBuilder str=new StringBuilder();
        str.append(br.readLine());
        while(br.read()!=-1&&start<end){
            str.append("<br/>");
            System.out.print(str);
            str.append(br.readLine());
            start++;


        }
        txt.setCount(start);
        txt.setContent(str.toString());
        br.close();
        isr.close();
        fsDataInputStream.close();
        return txt;
    }

    public void upload(String path,String path2) throws Exception{
        getFileSystem();
        fileSystem.copyFromLocalFile(new Path(path),new Path(path2));
        closeSystem();
    }

    public boolean  rename(String path,String name) throws Exception{
        getFileSystem();
       boolean result=fileSystem.rename(new Path(path),new Path(path.substring(0,path.lastIndexOf("/"))+"/"+name));
        closeSystem();
        return result;
    }
    public boolean  move(String path,String path2) throws Exception{
        getFileSystem();
        boolean result=fileSystem.rename(new Path(path),new Path(path2));
        closeSystem();
        return result;
    }
    public void download(String path,String path2) throws Exception{
            getFileSystem();
            fileSystem.copyToLocalFile(new Path(path),new Path(path2));
            closeSystem();
    }

    public  List<Node> query(String path) throws Exception {
        List<Node> result=new ArrayList<>();
        try {
            getFileSystem();
            FileStatus[] fileStatuses=fileSystem.listStatus(new Path(path));

            for(int i=0;i<fileStatuses.length;i++){
                Node node=new Node();
                node.setName(fileStatuses[i].getPath().toString().substring(
                        fileStatuses[i].getPath().toString().lastIndexOf("/")+1,fileStatuses[i].getPath().toString().length()
                ));
                node.setLen(fileStatuses[i].getLen());
                node.setOwner(fileStatuses[i].getOwner());
                node.setPath(fileStatuses[i].getPath().toString());
                Date date = new Date(fileStatuses[i].getModificationTime());
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                node.setModifytime(sd.format(date));
                node.setType(getType(fileStatuses[i]));

                result.add(node);
               // System.out.println(fileStatuses[i].getPath().toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closeSystem();
        }
        return result;
    }
    public void map(){

    }
    public String getType(FileStatus fileStatus){
        if(fileStatus.isDirectory()){return "Dir";}
        String path=fileStatus.getPath().toString();


        return path.substring(path.lastIndexOf("."));
//        }
//
//        Map<String,Pattern> map=new HashMap<>();
//        map.put("pic", Pattern.compile(".*(.jpeg|.jpg|.png|.bmp|.gif)$"));
//        map.put("text",Pattern.compile(".*(.txt|.rtf|.doc|.docx|.xls|.xlsx|.html|.htm|.xml)$"));
//        map.put("video",Pattern.compile(".*(.mp4|.avi|.wmv)$"));
//        map.put("music",Pattern.compile(".*(.mp3|.wav)$"));
//        map.put("other",Pattern.compile("^\\S+\\.*$"));
//        for (Map.Entry<String, Pattern> entry : map.entrySet()) {
//            if(  entry.getValue().matcher(path).matches()){
//                return entry.getKey();
//            }
//
//        }




    }
    public boolean delete(String path) throws Exception{
        getFileSystem();
        boolean re= fileSystem.delete(new Path(path),true);
        closeSystem();
        return re;
    }

    public  void testCheckFilelocation() throws IOException {
        FileStatus status=fileSystem.getFileStatus(new Path("/idea/1.txt"));
        BlockLocation[] locations=fileSystem.getFileBlockLocations(status,0,status.getLen());
        for(BlockLocation bl:locations){
            System.out.println(Arrays.toString(bl.getHosts())+"\t"+Arrays.toString(bl.getNames()));

        }
        fileSystem.close();
    }

    public void testcheckAllDataNodes() throws IOException{
        DistributedFileSystem fs=(DistributedFileSystem) fileSystem;
        DatanodeInfo[] dataNodes=fs.getDataNodeStats();
        for(DatanodeInfo datanode:dataNodes){
            System.out.println(datanode.getHostName()+"\t"+datanode.getName());
        }
        fileSystem.close();
    }

    public void destroy(){

    }

}
