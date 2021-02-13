package com.chill.dropit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Slf4j
public class FileManager{

    private String path;


    private HashMultimap<String, String> getMultiMap(){
        HashMultimap<String, String> multiMap = HashMultimap.create();
        multiMap.putAll("图片", Stream.of("png", "jpg", "jpeg", "gif").collect(Collectors.toSet()));
        multiMap.putAll("压缩包", Stream.of("zip", "rar", "7z").collect(Collectors.toSet()));
        multiMap.putAll("文档资料", Stream.of("txt", "md", "doc", "docx", "xls", "xlsx", "csv", "ppt", "pptx").collect(Collectors.toSet()));
        multiMap.putAll("电子书", Stream.of("pdf", "mobi", "azw3").collect(Collectors.toSet()));
        multiMap.putAll("可执行文件", Stream.of("exe").collect(Collectors.toSet()));
        multiMap.putAll("安卓安装包", Stream.of("apk").collect(Collectors.toSet()));
        multiMap.putAll("快捷方式", Stream.of("lnk").collect(Collectors.toSet()));
        multiMap.putAll("音频", Stream.of("mp3", "wmv", "m4a", "flac").collect(Collectors.toSet()));
        multiMap.putAll("视频", Stream.of("mp4", "mkv", "avi").collect(Collectors.toSet()));
        multiMap.putAll("json文件", Stream.of("json").collect(Collectors.toSet()));
        multiMap.putAll("sql脚本", Stream.of("sql").collect(Collectors.toSet()));
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
