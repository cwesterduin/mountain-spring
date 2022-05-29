package com.mountainspring.aws;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @GetMapping("")
    public List<Bucket> getAll() {
        return s3Service.listBuckets();
    }

    @GetMapping("/{bucketName}")
    public List<S3ObjectSummary> getObjects(@PathVariable String bucketName) {
        return s3Service.listObjects(bucketName);
    }

    @GetMapping("/{bucketName}/folders")
    public List<String> getFolderStruct(@PathVariable String bucketName) {
        return s3Service.listBucketFolders(bucketName);
    }

    @GetMapping("/{bucketName}/images")
    public List<S3Object> getFolderImages(@PathVariable String bucketName, @RequestParam String folderName) {
        return s3Service.listFolderImages(bucketName, folderName);
    }

    @PostMapping("/seed/{bucketName}/{region}")
    public void seedS3Data(@PathVariable String bucketName, @PathVariable String region) {
        s3Service.seedS3ObjectData(bucketName, region);
    }

    @PostMapping("/{bucketName}/upload")
    public void uploadS3File(@PathVariable String bucketName,
                             @RequestParam(value = "files", required = false) MultipartFile[] files,
                             @RequestParam(value = "folders", required = false) MultipartFile[] folders)
            throws JsonProcessingException {
        s3Service.uploadS3Image(bucketName, files, folders);
    }

    @DeleteMapping("/{bucketName}")
    public ResponseEntity<?> deleteS3File(
            @PathVariable String bucketName,
            @RequestBody List<String> pathList
    ) {
        return s3Service.deleteS3Object(bucketName, pathList);
    }

}
