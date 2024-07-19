package com.trevorism.service

import io.micronaut.http.multipart.CompletedFileUpload

interface DataRepository {

    List<String> listAllFiles()
    String create(String path, CompletedFileUpload fileUpload)
    byte [] read(String path)
    String delete(String path)
}