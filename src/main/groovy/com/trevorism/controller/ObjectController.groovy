package com.trevorism.controller

import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import com.trevorism.service.DataRepository
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.http.multipart.CompletedFileUpload
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("/object")
class ObjectController {
    private static final Logger log = LoggerFactory.getLogger(ObjectController.class.name)

    @Inject
    DataRepository repository

    @Tag(name = "Object Operations")
    @Operation(summary = "Get all types")
    @Get(value = "/", produces = MediaType.APPLICATION_JSON)
    List<String> listTables() {
        return repository.listAllFiles()
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Get an object of type {kind} with id {id} **Secure")
    @Get(value = "{+restOfPath}", produces = MediaType.APPLICATION_OCTET_STREAM)
    @Secure(value = Roles.USER, allowInternal = true)
    byte [] read(String restOfPath) {
        def entity = repository.read(restOfPath)
        if (!entity)
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "${restOfPath} not found")

        return entity
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Upload data at {path} **Secure")
    @Status(HttpStatus.CREATED)
    @Post(value = "{+restOfPath}", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    @Secure(value = Roles.USER, allowInternal = true)
    String upload(String restOfPath, CompletedFileUpload file) {
        try {
            def entity = repository.create(restOfPath, file)
            return entity
        } catch (Exception e) {
            log.error("Unable to create ${restOfPath}", e)
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to create ${restOfPath}")
        }
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Delete data at {path} **Secure")
    @Delete(value = "{+restOfPath}", produces = MediaType.TEXT_PLAIN)
    @Secure(value = Roles.USER, allowInternal = true)
    String delete(String restOfPath) {
        def entity = repository.delete(restOfPath)
        if (!entity)
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "${restOfPath} not found")

        return entity
    }

}
