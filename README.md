File Archive Service
====================
This service exposes a REST endpoint that allows users to upload multiple files, archives the uploaded files and returns
the archive file.

The POST request expects following parameters:

- files: A list of files to be archived sent to server as Multipart form data.
- format: The format of the archive file. At present, only zip format is supported.Possibility of supporting other
  formats in future.

The service returns the archive file as a ByteArrayOutputStream.
The archiving is done using the java.util.zip package.

The service also records the upload statistics in a database. The statistics include:

- Number of files uploaded
- IP address of the client
- Date of the upload

There are certain validations that are done on the input:

- The files parameter should not be empty.
- The files parameter should not exceed fair usage limit as configured in application.properties.
- None of the files should exceed the maximum file size as configured in application.properties.
- None of the files should be empty.
- The file extension should not be one of the restricted file extensions as configured in FileExtension enum.
- The format parameter should have a valid value as configured in ArchiveType enum.

The service also checks if the client is attempting to upload files more than the configured limit in a day.

The service also has a rate limiter that limits the number of requests that can be made in a time period.
The rate limit is configurable in application.properties.

Recommendations for future enhancements:
----------------------------------------

For the requirement to configure any other archive format, the following changes can be made:

- Add a new enum value in ArchiveType enum.
- Add a new implementation of ArchivalStrategy to implement the new method of archival.
- Use library such as Apache Commons Compress to implement the new method of archival.

Above follows the Strategy pattern.

For the requirement to handle file sizes upto 1GB, the following changes can be made:

- Avoid use of multipart form data and use a different method to upload files. For eg use standard HttpServletRequest
  object.
- Use of libraries like Apache Commons FileUpload can be made.
- With commons file upload, use Streaming API to stream the file and then archive it. Avoiding the need to store the
  file in temporary location.
  Here is a sample code snippet to use Streaming API:

```
JakartaServletFileUpload upload = new JakartaServletFileUpload();
FileItemInputIterator iterStream = upload.getItemIterator(request);
while (iterStream.hasNext()) {
    FileItemInput item = iterStream.next();
    String name = item.getFieldName();
    InputStream stream = item.getInputStream();
    if (!item.isFormField()) {
        // Perform validations and archive the file
    } else {
        // Get value of form fields such as format
        String formFieldValue = IOUtils.toString(stream, StandardCharsets.UTF_8);
    }
}
```

- For this method we need to set the property spring.servlet.multipart.enabled=false in application.properties.
- To further improve the response for large files, we can return ResponseEntity<StreamingResponseBody> instead of
  ResponseEntity<ByteArrayOutputStream> and stream the response.

For the requirement to handle high number of requests, the following approaches can be considered:

- Use of Asynchronous processing. The service can be made asynchronous by using @Async annotation on the service method.
- Can configure a ThreadPoolTaskExecutor to handle the asynchronous processing.See file AsyncConfig.java.
- Make use of CompletableFuture to handle the asynchronous processing. Use methods like
- CompletableFuture.supplyAsync() or CompletableFuture.runAsync() to run the task asynchronously passing in the executor
  configured earlier.
- Care must be taken to handle exceptions in the asynchronous processing. If doing multiple operations such as file
  upload, archiving and database insert in the asynchronous processing, handle exceptions at each step.
- Use transaction management to ensure that the operations are atomic. Use transactionTemplate in the service method to
  ensure that the operations are atomic and ensure data consistency.

Apart from the above, the service can be made more scalable by decoupling the file upload and archival process. The file
upload can be done in a separate service and the archival can be done in a separate service. The two services can
communicate using a message broker like RabbitMQ or Kafka. This will ensure that the services are loosely coupled and
can be scaled independently.

The files uploaded can be transferred to a cloud storage like AWS S3 or Google Cloud Storage. The files can be uploaded
as InputStream to the cloud storage or dedicated storage server and the URI of the bucket/directory can be returned to
the client. The client can then publish this URI as a RabbitMQ message.

The archival service nodes can then consume this message and archive the files retrieved from the cloud storage.

Upload the archived files zip(or any other format) back to the cloud storage and return the download link of the
archived file to the client.

The archival service will record the upload statistics in a database.

After successful archival and record upload statistics, the files can be deleted from the cloud storage to save space.

The service can be deployed on a cloud platform like AWS, Google Cloud or Azure. The service can be deployed as a
containerized application using Docker and Kubernetes. The service can be scaled horizontally by deploying multiple
instances of the service behind a load balancer For eg. using Auto scaling and Elastic Load Balancer in AWS.


