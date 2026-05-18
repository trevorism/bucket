package com.trevorism.bean

import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import io.micronaut.http.HttpRequest
import io.micronaut.runtime.http.scope.RequestAware
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.security.authentication.ServerAuthentication

@RequestScope
class StorageProvider implements RequestAware {

    private static final String GCP_DEFAULT_PROJECT = "trevorism-data"
    private static final String DEFAULT_BUCKET_NAME = "trevorism"

    private String tenant
    private Storage storage

    Storage getStorage() {
        if (!storage) {
            storage = StorageOptions.newBuilder().setProjectId(GCP_DEFAULT_PROJECT).build().getService()
        }
        return storage
    }

    String getBucketName() {
        if (tenant) {
            return "${DEFAULT_BUCKET_NAME}-${tenant}"
        }
        return DEFAULT_BUCKET_NAME
    }

    @Override
    void setRequest(HttpRequest<?> request) {
        Optional<ServerAuthentication> wrappedTenant = request.getAttribute("micronaut.AUTHENTICATION", ServerAuthentication)
        if(wrappedTenant.isPresent())
            tenant = wrappedTenant.get()?.attributes?.get("tenant")
    }
}
