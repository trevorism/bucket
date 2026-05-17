package com.trevorism.service

import com.google.cloud.storage.*
import com.trevorism.bean.StorageProvider
import io.micronaut.http.multipart.CompletedFileUpload

@jakarta.inject.Singleton
class CloudStorageRepository implements DataRepository {

    private final StorageProvider storageProvider

    CloudStorageRepository(StorageProvider storageProvider) {
        this.storageProvider = storageProvider
    }

    @Override
    List<String> listAllFiles() {
        Bucket bucket = storageProvider.storage.get(storageProvider.bucketName)
        List<String> fileNames = []
        bucket.list().iterateAll().each { Blob blob ->
            if(!blob.getName().endsWith("/")) {
                fileNames.add(blob.getName())
            }
        }
        return fileNames
    }

    @Override
    String create(String path, CompletedFileUpload fileUpload) {
        BlobId blobId = BlobId.of(storageProvider.bucketName, path)
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build()

        Storage.BlobWriteOption precondition = createUploadingPrecondition(path)
        fileUpload.getInputStream().withStream { inputStream ->
            storageProvider.storage.createFrom(blobInfo, inputStream, precondition)
        }
        return path
    }

    private Storage.BlobWriteOption createUploadingPrecondition(String path) {
        if (storageProvider.storage.get(storageProvider.bucketName, path) == null) {
            return Storage.BlobWriteOption.doesNotExist()
        } else {
            return Storage.BlobWriteOption.generationMatch(storageProvider.storage.get(storageProvider.bucketName, path).getGeneration())
        }
    }

    @Override
    byte [] read(String path) {
        Bucket bucket = storageProvider.storage.get(storageProvider.bucketName)
        Blob blob = bucket.get(path)
        if (blob == null) {
            return null
        }
        return blob.getContent()
    }

    @Override
    String delete(String path) {
        Bucket bucket = storageProvider.storage.get(storageProvider.bucketName)
        Blob blob = bucket.get(path)
        if (blob == null) {
            return null
        }
        blob.delete()
        return path
    }
}
