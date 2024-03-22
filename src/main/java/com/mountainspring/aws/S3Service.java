package com.mountainspring.aws;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.AWSCodePipelineClientBuilder;
import com.amazonaws.services.codepipeline.model.StartPipelineExecutionRequest;
import com.amazonaws.services.s3.model.*;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.mountainspring.eventMedia.EventMedia;
import com.mountainspring.eventMedia.EventMediaRepository;
import com.mountainspring.mapFeature.MapFeature;
import com.mountainspring.mapFeature.MapFeatureRepository;
import com.mountainspring.trip.Trip;
import com.mountainspring.trip.TripRepository;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    Logger logger = LoggerFactory.getLogger(S3Service.class);

    final static Set<String> IMAGE_CONTENT_TYPES = new HashSet<>();
    final static Set<String> VIDEO_CONTENT_TYPES = new HashSet<>();

    static {
        // Add common image MIME types
        IMAGE_CONTENT_TYPES.add("image/jpeg");
        IMAGE_CONTENT_TYPES.add("image/png");
        IMAGE_CONTENT_TYPES.add("image/gif");
        IMAGE_CONTENT_TYPES.add("image/bmp");
        IMAGE_CONTENT_TYPES.add("image/webp");

        VIDEO_CONTENT_TYPES.add("video/mp4");
        VIDEO_CONTENT_TYPES.add("video/mpeg");
        VIDEO_CONTENT_TYPES.add("video/quicktime");
        VIDEO_CONTENT_TYPES.add("video/x-msvideo");
        VIDEO_CONTENT_TYPES.add("video/x-flv");
    }


    @Autowired
    private S3ObjectRepository s3ObjectRepository;

    @Autowired
    private EventMediaRepository eventMediaRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private MapFeatureRepository mapFeatureRepository;

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

    public List<String> listBucketFolders(String bucketName) {
        return s3ObjectRepository.findAllByClassificationAndBucketName("folder", bucketName)
                .stream().map(S3Object::getPath).collect(Collectors.toList());
    }


    public List<S3Object> listFolderImages(String bucketName, String folderName) {
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

    public void uploadS3Image(String bucketName, MultipartFile[] files, MultipartFile[] folders) throws ImageProcessingException, MetadataException {

        List<S3Object> s3ObjectsToSave = new ArrayList<>();

        try {
            if (files != null) {
                for (MultipartFile multipartFile : files) {

                    System.out.println(multipartFile.getContentType());


                    if (IMAGE_CONTENT_TYPES.contains(multipartFile.getContentType())) {


                        BufferedImage image = ImageIO.read(multipartFile.getInputStream());


                        // Read metadata from the image file
                        Metadata originalMetadata = ImageMetadataReader.readMetadata(multipartFile.getInputStream());

                        // Extract the orientation information
                        int orientation = getOrientation(originalMetadata);
                        image = rotateImage(image, orientation);

                        int original_width = image.getWidth();
                        int original_height = image.getHeight();
                        int bound_width = 1650;
                        int bound_width_thumb = 150;
                        int new_width = original_width;
                        int new_height = original_height;
                        int new_width_thumb = original_width;
                        int new_height_thumb = original_height;

                        // first check if we need to scale width
                        if (original_width > bound_width) {
                            //scale width to fit
                            new_width = bound_width;
                            //scale height to maintain aspect ratio
                            new_height = (new_width * original_height) / original_width;
                        }

                        // first check if we need to scale width
                        if (original_width > bound_width_thumb) {
                            //scale width to fit
                            new_width_thumb = bound_width_thumb;
                            //scale height to maintain aspect ratio
                            new_height_thumb = (new_width_thumb * original_height) / original_width;
                        }

                        BufferedImage scaledImage = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = scaledImage.createGraphics();
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g.drawImage(image, 0, 0, new_width, new_height, null);
                        g.dispose();

                        BufferedImage scaledThumbImage = new BufferedImage(new_width_thumb, new_height_thumb, BufferedImage.TYPE_INT_RGB);
                        Graphics2D gThumb = scaledThumbImage.createGraphics();
                        gThumb.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        gThumb.drawImage(image, 0, 0, new_width_thumb, new_height_thumb, null);
                        gThumb.dispose();

                        //convert image data to s3 compatible format
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        ImageIO.write(scaledImage, FilenameUtils.getExtension(multipartFile.getOriginalFilename()), outputStream);
                        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                        ImageIO.write(scaledThumbImage, FilenameUtils.getExtension(multipartFile.getOriginalFilename()), outputStream2);
                        InputStream inputStream2 = new ByteArrayInputStream(outputStream2.toByteArray());

                        String newFileName = FilenameUtils.getFullPath(multipartFile.getOriginalFilename()) + FilenameUtils.getBaseName(multipartFile.getOriginalFilename()) + ".jpg";
                        String newThumbName = FilenameUtils.getFullPath(multipartFile.getOriginalFilename()).replaceFirst("images", "thumbnails") + FilenameUtils.getBaseName(multipartFile.getOriginalFilename()) + ".jpg";


                        //upload the resized image to s3
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentType("image/jpeg");
                        PutObjectResult s3Upload = s3.putObject(bucketName, newFileName, inputStream, metadata);

                        //upload the thumbnail image to s3
                        ObjectMetadata metadataThumb = new ObjectMetadata();
                        metadataThumb.setContentType("image/jpeg");
                        PutObjectResult s3UploadThumb = s3.putObject(bucketName, newThumbName, inputStream2, metadataThumb);

                        //check if ref exists in db
                        if (s3ObjectRepository.existsS3ObjectByPathAndBucketName(newFileName, bucketName)) {
                            break;
                        }

                        //add to list of s3 database entities
                        S3Object s3ObjectToAdd = new S3Object();
                        s3ObjectToAdd.setBucketName(bucketName);
                        s3ObjectToAdd.setPath(newFileName);
                        s3ObjectToAdd.setClassification("file");
                        s3ObjectToAdd.setFileType("image");
                        s3ObjectToAdd.setRegion(s3.getRegionName());
                        s3ObjectsToSave.add(s3ObjectToAdd);

                    } else if (VIDEO_CONTENT_TYPES.contains(multipartFile.getContentType())) {

                        InputStream videoInputStream = multipartFile.getInputStream();
                        String newFileName = FilenameUtils.getFullPath(multipartFile.getOriginalFilename()) + FilenameUtils.getBaseName(multipartFile.getOriginalFilename()) + ".mp4";

                        //upload the video to s3
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentType("video/mp4");
                        PutObjectResult s3Upload = s3.putObject(bucketName, newFileName, videoInputStream, metadata);

                        //check if ref exists in db
                        if (s3ObjectRepository.existsS3ObjectByPathAndBucketName(newFileName, bucketName)) {
                            break;
                        }

                        //add to list of s3 database entities
                        S3Object s3ObjectToAdd = new S3Object();
                        s3ObjectToAdd.setBucketName(bucketName);
                        s3ObjectToAdd.setPath(newFileName);
                        s3ObjectToAdd.setClassification("file");
                        s3ObjectToAdd.setFileType("video");
                        s3ObjectToAdd.setRegion(s3.getRegionName());
                        s3ObjectsToSave.add(s3ObjectToAdd);


                    }
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

        } catch (SdkClientException | IOException e) {
            e.printStackTrace();
        }
        if (files != null) {
            logger.info("uploaded: " + Arrays.stream(files).count());
        }
        if (folders != null) {
            logger.info("uploaded: " + Arrays.stream(folders).count());
        }
    }

    public ResponseEntity<?> deleteS3Object(String bucket, List<String> path) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket);

        List<DeleteObjectsRequest.KeyVersion> keyList = new ArrayList<>();
        List<S3Object> s3ObjectsToDelete = new ArrayList<>();
        List<List<String>> pathListList = new ArrayList<>();


        for (String s : path) {
            List<String> pathList = Arrays.stream(s.split("(?<=/)")).collect(Collectors.toList());
            pathListList.add(pathList);
            keyList.add(new DeleteObjectsRequest.KeyVersion(s));
            keyList.add(new DeleteObjectsRequest.KeyVersion(s.replaceFirst("images", "thumbnails")));
            s3ObjectsToDelete.add(s3ObjectRepository.findByPath(s));
        }
        deleteObjectsRequest.setKeys(keyList);

        try {
            s3ObjectRepository.deleteAll(s3ObjectsToDelete);
            logger.info("deleted db references");
        } catch (Exception e) {
            logger.error("failed to delete db references");
            HashMap<String, HashMap<String, List<String>>> errorMap = new HashMap<>();
            s3ObjectsToDelete.forEach(s3Object -> {
                errorMap.put(s3Object.getPath(), new HashMap<>());
                errorMap.get(s3Object.getPath()).put("events", new ArrayList<>());
                errorMap.get(s3Object.getPath()).put("trips", new ArrayList<>());
                errorMap.get(s3Object.getPath()).put("map-features", new ArrayList<>());

                List<EventMedia> eventMediaList = eventMediaRepository.findByMedia(s3Object);
                for (EventMedia eventMedia : eventMediaList) {
                    errorMap.get(s3Object.getPath()).get("events").add(eventMedia.getEvent().getName());
                }

                List<Trip> tripMediaList = tripRepository.findByPrimaryImage(s3Object);
                for (Trip trip : tripMediaList) {
                    errorMap.get(s3Object.getPath()).get("trips").add(trip.getName());
                }

                List<MapFeature> mapFeatureList = mapFeatureRepository.findByPrimaryImage(s3Object);
                for (MapFeature mapFeature : mapFeatureList) {
                    errorMap.get(s3Object.getPath()).get("map-features").add(mapFeature.getName());
                }

            });


            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorMap);
        }

        try {
            s3.deleteObjects(deleteObjectsRequest);
            logger.info("deleted s3 resources");
        } catch (Exception e) {
            logger.error("failed to delete db references");
            s3ObjectRepository.saveAll(s3ObjectsToDelete);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("failed to delete aws s3 resource");
        }

        //delete folders
        List<S3Object> s3ObjectsFoldersToDelete = new ArrayList<>();
        for (List<String> strings : pathListList) {
            for (int i = 0; i < strings.size(); i++) {
                String path2 = String.join("", strings.subList(0, i));
                S3Object s3o = s3ObjectRepository.findFirstByPathStartsWithAndClassification(path2, "file");
                if (s3o == null) {
                    s3o = s3ObjectRepository.findByPath(
                            String.join("", strings.subList(0, i))
                    );
                    s3ObjectsFoldersToDelete.add(s3o);
                };
            }
        }
        if (!s3ObjectsFoldersToDelete.isEmpty()) {
            logger.info("deleting folders");
            s3ObjectRepository.deleteAll(s3ObjectsFoldersToDelete);
        }
        HashMap<String, String> successMap = new HashMap<>();
        successMap.put("message", "deleted s3Object in database and s3");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(successMap);


    }


    public void updateDescription(UUID id, String description) {
        Optional<S3Object> s3ObjectToUpdate = s3ObjectRepository.findById(id);
        s3ObjectToUpdate.ifPresent(s3Object -> {
            s3Object.setDescription(description);
            s3ObjectRepository.save(s3Object);
        });
    }

    private static int getOrientation(Metadata metadata) throws MetadataException {
        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (exifIFD0Directory != null && exifIFD0Directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
            return exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        }
        return 1;
    }

    public static BufferedImage rotateImage(BufferedImage originalImage, int orientation) {
        switch (orientation) {
            case 6:
                return rotateClockwise(originalImage, 90);
            case 3:
                return rotateClockwise(originalImage, 180);
            case 8:
                return rotateClockwise(originalImage, 270);
            default:
                return originalImage;
        }
    }

    private static BufferedImage rotateClockwise(BufferedImage originalImage, int degrees) {
        double radians = Math.toRadians(degrees);
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int newWidth = width;
        int newHeight = height;

        if (degrees == 90 || degrees == 270) {
            newWidth = height;
            newHeight = width;
        }

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = rotatedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        AffineTransform at = new AffineTransform();
        at.translate(newWidth / 2, newHeight / 2);
        at.rotate(radians);
        at.translate(-width / 2, -height / 2);
        g.drawImage(originalImage, at, null);
        g.dispose();

        return rotatedImage;
    }

    public void triggerBuild(String projectName){
        AWSCodePipeline awsCodePipelineClient = AWSCodePipelineClientBuilder.standard().build();

        StartPipelineExecutionRequest request = new StartPipelineExecutionRequest()
                .withName(projectName);

        // Trigger pipeline execution
        awsCodePipelineClient.startPipelineExecution(request);
    }


}
