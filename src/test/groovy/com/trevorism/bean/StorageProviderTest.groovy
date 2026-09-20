package com.trevorism.bean

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.ServerAuthentication
import org.junit.jupiter.api.Test

class StorageProviderTest {

    @Test
    void testGetBucketName() {
        StorageProvider storageProvider = new StorageProvider()
        assert storageProvider.getBucketName() == StorageProvider.DEFAULT_BUCKET_NAME
    }

    @Test
    void testGetBucketNameWithTenant() {
        StorageProvider storageProvider = new StorageProvider()
        storageProvider.tenant = "1234"
        assert storageProvider.getBucketName() == "${StorageProvider.DEFAULT_BUCKET_NAME}-1234"
    }

    @Test
    void testSetRequest() {
        StorageProvider storageProvider = new StorageProvider()
        storageProvider.setRequest([getAttribute: { String name, Class<ServerAuthentication> clazz ->
            Optional.of(new ServerAuthentication("user", [], [tenant: "1234"]))
        }] as HttpRequest)
        assert storageProvider.tenant == "1234"
    }
}
