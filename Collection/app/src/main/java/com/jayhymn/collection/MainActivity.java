public void agregacara() {
        AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonRekognition rekognitionClient = new AmazonRekognitionClient(credentials);
        rekognitionClient.setRegion(Region.getRegion(Regions.US_EAST_1));

        Image image = new Image()
        .withS3Object(new S3Object()
        .withBucket(bucket)
        .withName(nombre));

        IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
        .withImage(image)
        .withCollectionId(coleccionid)
        .withExternalImageId(nombre)
        .withDetectionAttributes(“ALL”);

        IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);
        List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
        for (FaceRecord faceRecord : faceRecords) {
        }
        }