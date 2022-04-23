package com.mountainspring.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    @Autowired
    private S3ObjectRepository s3ObjectRepository;

    final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.EU_WEST_2)
            .build();

    public List<Bucket> listBuckets() {
        return s3.listBuckets();
    }

    public List<S3ObjectSummary> listObjects(String bucketName) {
        ListObjectsV2Result result = s3.listObjectsV2((new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withDelimiter("/")));
        List<S3ObjectSummary> returnObjects = new ArrayList<>();
        result.getObjectSummaries().forEach(r -> {
            if (r.getSize() == 0) {
                returnObjects.add(r);
            }
        });
        return returnObjects;
    }

//    public List<Folder> listBucketFolders(String bucketName) throws JsonProcessingException {
//
//        List<S3ObjectSummary> resultSummaries = getS3ObjectSummaries(bucketName);
//
//        List<Folder> resFolders = new ArrayList<>();
//
//        resultSummaries.forEach(r -> {
//            String[] splitStrings = r.getKey().split("(?<=/)");
//            StringBuilder folder = new StringBuilder();
//            for (String splitString : splitStrings) {
//                    folder.append(splitString);
//            }
//            List<String> folderStringList = List.of(String.valueOf(folder).split("/"));
//            Folder folderToAdd = new Folder(folderStringList.size(), folderStringList.get(folderStringList.size() - 1), folderStringList);
//            if (!resFolders.contains(folderToAdd)) {
//                resFolders.add(folderToAdd);
//            }
//
//        });
//
//        resFolders.sort(Comparator.comparing(Folder::getDepth));
//
//        return resFolders;
//
//    }


    public List<String> listBucketFolders(String bucketName){
        return s3ObjectRepository.findAllByClassificationAndBucketName("folder", bucketName)
                .stream().map(S3Object::getPath).collect(Collectors.toList());
    }


    public List<S3Object> listFolderImages(String bucketName, String folderName){
        return s3ObjectRepository.findAllFolderImages(bucketName, folderName);
    }


    public void seedS3ObjectData(String bucketName, String region) {
        List<S3ObjectSummary> resultSummaries = getS3ObjectSummaries(bucketName);
        List<S3Object> s3ObjectsToSave = new ArrayList<>();
        resultSummaries = resultSummaries.stream().filter(r -> r.getKey().startsWith("images")).collect(Collectors.toList());
        resultSummaries.forEach(r -> {
            String classification = "file";
            if (r.getSize() == 0) {
                classification = "folder";
            } else {
                List<String> pathList = Arrays.stream(r.getKey().split("(?<=/)")).collect(Collectors.toList());
                pathList.remove(pathList.size() - 1);
                while (pathList.size() > 0) {
                    String path = String.join("", pathList);
                    if (s3ObjectsToSave.stream().noneMatch(s3o -> s3o.getPath().equals(path))) {
                        S3Object s3ObjectToAdd = new S3Object();
                        s3ObjectToAdd.setBucketName(bucketName);
                        s3ObjectToAdd.setPath(path);
                        s3ObjectToAdd.setClassification("folder");
                        s3ObjectToAdd.setRegion(region);
                        s3ObjectsToSave.add(s3ObjectToAdd);
                        pathList.remove(pathList.size() - 1);
                    } else {
                        pathList.clear();
                    }
                }
            }
            S3Object s3ObjectToAdd = new S3Object();
            s3ObjectToAdd.setBucketName(bucketName);
            s3ObjectToAdd.setPath(r.getKey());
            s3ObjectToAdd.setClassification(classification);
            s3ObjectToAdd.setRegion(region);

            s3ObjectsToSave.add(s3ObjectToAdd);
        });

        s3ObjectRepository.saveAll(s3ObjectsToSave);

    }

    private List<S3ObjectSummary> getS3ObjectSummaries(String bucketName) {
        ObjectListing result = s3.listObjects(bucketName);
        List<S3ObjectSummary> objectSummaries = new ArrayList<>(result.getObjectSummaries());
        boolean cont = result.isTruncated();
        while (cont) {
            result = s3.listNextBatchOfObjects(result);
            objectSummaries.addAll(result.getObjectSummaries());
            cont = result.isTruncated();
        }
        return objectSummaries;
    }

    public void uploadS3Image(String bucketName, MultipartFile[] files, MultipartFile[] folders) {

        List<S3Object> s3ObjectsToSave = new ArrayList<>();

        try {
            if (files != null) {
                for (MultipartFile multipartFile : files) {
                    System.out.println(multipartFile.getContentType());

                    BufferedImage image = ImageIO.read(multipartFile.getInputStream());


                    // scale image to target width keep original ration
                    int targetHeight;
                    int targetWidth = 800;
                    double originalHeight = image.getHeight();
                    double originalWidth = image.getWidth();
                    double targetRatio = originalHeight / originalWidth;
                    targetHeight = (int) (targetRatio * targetWidth);
                    Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);

                    //create a new image of size defined above
                    BufferedImage buffered = new BufferedImage(targetWidth, targetHeight, image.getType());
                    buffered.createGraphics().drawImage(scaledImage, 0, 0, null);

                    //convert image data to s3 compatible format
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(buffered, "jpg", outputStream);
                    InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                    //upload the resized image to s3
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentType("jpg");
                    PutObjectResult s3Upload = s3.putObject(bucketName, multipartFile.getOriginalFilename(), inputStream, metadata);

                    //check if ref exists in db
                    if (s3ObjectRepository.existsS3ObjectByPathAndBucketName(multipartFile.getOriginalFilename(), bucketName)) {
                        break;
                    }

                    //add to list of s3 database entities
                    S3Object s3ObjectToAdd = new S3Object();
                    s3ObjectToAdd.setBucketName(bucketName);
                    s3ObjectToAdd.setPath(multipartFile.getOriginalFilename());
                    s3ObjectToAdd.setClassification("file");
                    s3ObjectToAdd.setRegion(s3.getRegionName());
                    s3ObjectsToSave.add(s3ObjectToAdd);
                }
            }

            if (folders != null) {
                InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
                for (MultipartFile multipartFile : folders) {
                    //upload folders to s3
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(0);
                    s3.putObject(bucketName, multipartFile.getOriginalFilename(), emptyContent, metadata);

                    //add to list of s3 database entities
                    S3Object s3ObjectToAdd = new S3Object();
                    s3ObjectToAdd.setBucketName(bucketName);
                    s3ObjectToAdd.setPath(multipartFile.getOriginalFilename());
                    s3ObjectToAdd.setClassification("folder");
                    s3ObjectToAdd.setRegion(s3.getRegionName());
                    s3ObjectsToSave.add(s3ObjectToAdd);
                }
            }

            //establish and save reference for any new folders
            List<S3Object> s3FoldersToSave = new ArrayList<>();
            for (S3Object s3Object : s3ObjectsToSave) {
                List<String> pathList = Arrays.stream(s3Object.getPath().split("(?<=/)")).collect(Collectors.toList());
                pathList.remove(pathList.size() - 1);
                while (pathList.size() > 0) {
                    String path = String.join("", pathList);
                    if (
                                    s3ObjectsToSave.stream().noneMatch(s3o -> s3o.getPath().equals(path))
                                    && s3FoldersToSave.stream().noneMatch(s3o -> s3o.getPath().equals(path))
                                    && s3ObjectRepository.findByPath(path) == null
                    ) {
                        S3Object s3ObjectToAdd = new S3Object();
                        s3ObjectToAdd.setBucketName(bucketName);
                        s3ObjectToAdd.setPath(path);
                        s3ObjectToAdd.setClassification("folder");
                        s3ObjectToAdd.setRegion(s3.getRegionName());
                        s3FoldersToSave.add(s3ObjectToAdd);
                        pathList.remove(pathList.size() - 1);
                    } else {
                        pathList.clear();
                    }
                }
            }
            //combine folders and files to save all
            s3ObjectsToSave.addAll(s3FoldersToSave);
            s3ObjectRepository.saveAll(s3ObjectsToSave);

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
