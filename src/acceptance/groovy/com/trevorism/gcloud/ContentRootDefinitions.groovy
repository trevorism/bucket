package com.trevorism.gcloud

import io.cucumber.groovy.EN
import io.cucumber.groovy.Hooks

/**
 * @author tbrooks
 */

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def contextRootContent
def pingContent

Given(/the bucket application is alive/) { ->
    try{
        new URL("https://bucket.data.trevorism.com/ping").text
    }
    catch (Exception ignored){
        Thread.sleep(10000)
        new URL("https://bucket.data.trevorism.com/ping").text
    }
}

When(/I navigate to {string}/) { String string ->
    contextRootContent = new URL(string).text
}

Then(/then a link to the help page is displayed/) {  ->
    assert contextRootContent
    assert contextRootContent.contains("/help")
}

When(/I ping the application deployed to {string}/) { String string ->
    pingContent = new URL("${string}/ping").text
}

Then(/pong is returned, to indicate the service is alive/) {  ->
    assert pingContent == "pong"
}