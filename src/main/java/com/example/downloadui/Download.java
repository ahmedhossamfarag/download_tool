package com.example.downloadui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Iterator;

public class Download implements Runnable {
    String mainFolder, mainPath;
    LinkList list;
    boolean active;

    Thread thread;


    public Download(String realPath, String mainPath, LinkList list) {
        mainFolder = realPath;
        this.mainPath = mainPath;
        this.list = list;
    }


    void operate(){
        active = true;
        thread = new Thread(this);
        thread.start();

    }

    boolean downloadable(String path) {
        String s = path;
        if (path.contains("?")) {
            s = path.substring(0, path.indexOf(63));
        }

        return s.endsWith(".ico") || s.endsWith(".css") || s.endsWith(".js") || s.endsWith(".jpg") || s.endsWith(".gif") || s.endsWith(".png");
    }

    boolean readable(String path) {
        String s = path;
        if (path.contains("?")) {
            s = path.substring(0, path.indexOf(63));
        }

        return s.endsWith(".htm") || s.endsWith(".html") || s.endsWith(".asp") || s.endsWith(".com/") || s.endsWith(".php");
    }

    String getRealPath(String path){
        String realPath;
        if (path.startsWith("http://"))
            realPath = formatName(path.replace("http:/" , mainFolder));
        else
            realPath = formatName(path.replace("https:/" , mainFolder));
        if (realPath.contains("?")) {
            realPath = realPath.substring(0, realPath.indexOf(63));
        }
        return realPath;
    }

    void download(String path) throws IOException {
        String realPath = getRealPath(path);
        File file = new File(realPath);
        if (!file.exists()) {
            if (downloadable(path)) {
                byte[] b = (new URL(path)).openStream().readAllBytes();
                create(file);
                Files.write(file.toPath(), b, new OpenOption[0]);
            } else if (readable(path)) {
                ArrayList<String> relations = new ArrayList();
                String content = toString((new URL(path)).openStream().readAllBytes());
                create(file);
                StringBuilder adder = new StringBuilder();
                int i = 0;
                int j = find(content, i);

                while(true) {
                    String s;
                    while(true) {
                        if (j == -1) {
                            adder.append(content.substring(i));
                            Files.writeString(file.toPath(), adder.toString());
                            Iterator var9 = relations.iterator();
                            while(var9.hasNext()) {
                                s = (String)var9.next();
                                String newPath;
                                if (s.startsWith("/")) {
                                    newPath = mainPath + s;
                                } else{
                                    String temp = path.substring(0, path.lastIndexOf('/'));
                                    while (s.startsWith("../"))
                                    {
                                        temp = temp.substring(0, temp.lastIndexOf('/'));
                                        s = s.substring(3);
                                    }
                                    if(!temp.startsWith(mainPath))
                                        newPath = mainPath + '/' + s;
                                    else
                                        newPath = temp + '/' + s;
                                }

                                if (!new File(getRealPath(newPath)).exists())
                                    list.push(newPath);
                            }

                            return;
                        }

                        adder.append(content, i, j);
                        if (content.charAt(j - 1) == '"') {
                            i = content.indexOf(34, j);
                            break;
                        }

                        if (content.charAt(j - 1) == '\'') {
                            i = content.indexOf(39, j);
                            break;
                        }

                        ++j;
                    }

                    s = content.substring(j, i);
                    if (!s.contains("#") && !s.startsWith("http") && !s.startsWith("ftp:") && !s.startsWith("//")) {
                        adder.append(formatHref(s, path));
                        relations.add(s);
                    } else {
                        adder.append(s);
                    }

                    j = find(content, i);
                }
            }
        }
    }

    private String toString(byte[] readAllBytes) {
        StringBuilder s = new StringBuilder();
        byte[] var5 = readAllBytes;
        int var4 = readAllBytes.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            byte b = var5[var3];
            s.append((char)b);
        }

        return s.toString();
    }

    void create(File file) throws IOException {
        file.getParentFile().mkdirs();
        file.createNewFile();
    }

    int find(String content, int offset) {
        for(int i = offset; i < content.length(); ++i) {
            if (content.startsWith("href=", i)) {
                return i + 6;
            }

            if (content.startsWith("src=", i)) {
                return i + 5;
            }
        }

        return -1;
    }

    String formatName(String s) {
       if (!s.endsWith(".asp") && !s.endsWith(".php")) {
            s = s.replace(".asp?", ".html?");
            s = s.replace(".php?", ".html?");
            return s + (s.endsWith("/") ? "index.html" : "");
        } else {
            return s.substring(0, s.length() - 3) + "html";
        }
    }

    String formatHref(String s, String path){
        s = formatName(s);
        if(s.startsWith("/")){
            s = s.substring(1);
            path = path.substring(0, path.lastIndexOf('/'));
            while (path.length() > mainPath.length())
            {
                path = path.substring(0, path.lastIndexOf('/'));
                s = "../" + s;
            }
        }
        return s;
    }

    @Override
    public void run() {
        while(active && list.top() != null){
            try {
                download(list.top());
            }catch (IOException ex){
            }
            list.pop();
        }
    }
}

