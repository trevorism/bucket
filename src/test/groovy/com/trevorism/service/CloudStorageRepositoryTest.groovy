package com.trevorism.service

import com.trevorism.bean.StorageProvider
import org.junit.jupiter.api.Test

class CloudStorageRepositoryTest {

    @Test
    void testCloudStorageRepositoryCreation() {
        StorageProvider storageProvider = new StorageProvider()
        DataRepository cloudStorageRepository = new CloudStorageRepository(storageProvider)
        assert cloudStorageRepository
    }
}
