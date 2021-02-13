package com.chill.dropit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.CanWriteFileFilter;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

@Data
@Slf4j
public class FileManager{

    private String path;


    private HashMultimap<String, String> getMultiMap(){
        HashMultimap<String, String> multiMap = HashMultimap.create();
        multiMap.putAll("图片", Sets.newHashSet("png", "jpg", "jpeg", "gif"));
        multiMap.putAll("压缩包", Sets.newHashSet("zip", "rar", "7z"));
        multiMap.putAll("文档资料", Sets.newHashSet("txt", "md", "doc", "docx", "xls", "xlsx", "csv", "ppt", "pptx"));
        multiMap.putAll("电子书", Sets.newHashSet("pdf", "mobi", "azw3"));
        multiMap.putAll("可执行文件", Sets.newHashSet("exe"));
        multiMap.putAll("安卓安装包", Sets.newHashSet("apk"));
        multiMap.putAll("快捷方式", Sets.newHashSet("lnk"));
        multiMap.putAll("音频", Sets.newHashSet("mp3", "wmv", "m4a", "flac"));
        multiMap.putAll("视频", Sets.newHashSet("mp4", "mkv", "avi"));
        multiMap.putAll("json文件", Sets.newHashSet("json"));
        multiMap.putAll("sql脚本", Sets.newHashSet("sql"));
        return multiMap;

    }

    public HashMultimap<String, File> getFiles(String path){
        HashMultimap<String, File> multiMap = HashMultimap.create();
        HashMultimap<String, String> formatMap = getMultiMap();
        Collection<File> files = FileUtils.listFilesAndDirs(new File(path),
                CanWriteFileFilter.CAN_WRITE, null);
        files.forEach(file -> formatMap.keySet().forEach(s -> {
            Set<String> strings = formatMap.get(s);
            if (strings.contains(file.getName().substring(file.getName().lastIndexOf(".") + 1))) {
                multiMap.put(s, file);
            }
        }));
        log.info("files = " + multiMap);
        return multiMap;
    }


    public void generateDirectoryAndMove(HashMultimap<String, File> multiMap, String path, JTextPane jTextPane){
        Stopwatch stopwatch = Stopwatch.createStarted();
        multiMap.keySet().forEach(key -> {
            Set<File> files = multiMap.get(key);
            files.forEach(file -> {
                try {
                    String message = copyFileToDirectory(path, key, file);
                    jTextPane.setText(appendString(jTextPane.getText(), message));
                    FileUtils.moveFileToDirectory(file, new File(path, "备份"), true);
                } catch (IOException e) {
                    log.error("有同名文件", e);
                }
            });
        });

        String text = jTextPane.getText();
        StringBuilder sb = new StringBuilder(text);
        sb.append("\r\n");
        sb.append("耗时").append(stopwatch.stop());
        jTextPane.setText(sb.toString());


    }

    private String appendString(String text, String appendStr){

        StringBuilder sb = new StringBuilder(text);
        sb.append(appendStr);
        sb.append("\r\n");
        return sb.toString();
    }

    private String copyFileToDirectory(String path, String key, File file) throws IOException{
        FileUtils.copyFileToDirectory(file, new File(path, key), true);
        String info = MessageFormat.format("移动文件:【{0}】到【{1}】", file.getName(), path + "/" + key);
        log.info(info);
        return info;
    }

    public void drop(String path, JTextPane jTextPane){
        HashMultimap<String, File> files = getFiles(path);
        generateDirectoryAndMove(files, path, jTextPane);

    }


}
