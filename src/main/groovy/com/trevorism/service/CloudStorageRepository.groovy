package com.trevorism.service

import com.google.cloud.storage.*
import io.micronaut.http.multipart.CompletedFileUpload

@jakarta.inject.Singleton
class CloudStorageRepository implements DataRepository {

    private static final String GCP_DEFAULT_PROJECT = "trevorism-data"
    private static final String DEFAULT_BUCKET_NAME = "trevorism"

    private Storage storage = StorageOptions.newBuilder().setProjectId(GCP_DEFAULT_PROJECT).build().getService()

    @Override
    List<String> listAllFiles() {
        Bucket bucket = storage.get(DEFAULT_BUCKET_NAME)
        List<String> fileNames = []
        bucket.list().iterateAll().each { Blob blob ->
            fileNames.add(blob.getName())
        }
        return fileNames
    }

    @Override
    String create(String path, CompletedFileUpload fileUpload) {
        BlobId blobId = BlobId.of(DEFAULT_BUCKET_NAME, path)
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build()

        Storage.BlobWriteOption precondition = createUploadingPrecondition(path)
        fileUpload.getInputStream().withStream { inputStream ->
            storage.createFrom(blobInfo, inputStream, precondition)
        }
        return path
    }

    private Storage.BlobWriteOption createUploadingPrecondition(String path) {
        if (storage.get(DEFAULT_BUCKET_NAME, path) == null) {
            return Storage.BlobWriteOption.doesNotExist()
        } else {
            return Storage.BlobWriteOption.generationMatch(storage.get(DEFAULT_BUCKET_NAME, path).getGeneration())
        }
    }

    @Override
    byte [] read(String path) {
        Bucket bucket = storage.get(DEFAULT_BUCKET_NAME)
        Blob blob = bucket.get(path)
        if (blob == null) {
            return null
        }
        return blob.getContent()
    }

    @Override
    String delete(String path) {
        Bucket bucket = storage.get(DEFAULT_BUCKET_NAME)
        Blob blob = bucket.get(path)
        if (blob == null) {
            return null
        }
        blob.delete()
        return path
    }
}
