package com.snovelli.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Salvatore on 24/04/2016.
 */
//@RestController
public class FileUploadController {


    //@Value("${working.folder}")
    private String workingFolder;


    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {


        File outputFile = File.createTempFile("tmp",
                file.getOriginalFilename().replaceAll(" ", "_"),
                new File(workingFolder));

        if (!file.isEmpty()) {
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(outputFile));
            FileCopyUtils.copy(file.getInputStream(), stream);
            stream.close();

        }

        return outputFile.getName();

    }

}