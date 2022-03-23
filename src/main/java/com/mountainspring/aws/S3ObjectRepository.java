package com.mountainspring.aws;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface S3ObjectRepository extends JpaRepository<S3Object, UUID> {

    void deleteAllByBucketName(String bucketName);

    S3Object findByPath(String path);

    List<S3Object> findAllByClassificationAndBucketName(String classification, String bucketName);

    @Query(
            value = "select * from s3object WHERE classification = 'file' " +
                    "AND bucket_name = ?1 " +
                    "AND SUBSTRING(path, 1,LOCATE(SUBSTRING_INDEX(path, '/', -1),path)-1) = ?2",
            nativeQuery = true)
    List<S3Object> findAllFolderImages(String bucketName, String folderName);



}
