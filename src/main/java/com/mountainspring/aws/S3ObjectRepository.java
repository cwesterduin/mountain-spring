package com.mountainspring.aws;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface S3ObjectRepository extends JpaRepository<S3Object, Long> {

    void deleteAllByBucketName(String bucketName);

    List<S3Object> findAllByClassificationAndBucketName(String classification, String bucketName);

}
