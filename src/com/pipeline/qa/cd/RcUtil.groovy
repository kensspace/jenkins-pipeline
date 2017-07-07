package com.pipeline.qa.cd

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.RequestEntity
import org.apache.commons.httpclient.methods.StringRequestEntity
import java.io.BufferedReader
import java.io.InputStreamReader

import groovy.json.JsonSlurperClassic

def sendHttpGetRequest(String requestUrl){
    GetMethod get = new GetMethod(requestUrl)
    new HttpClient().executeMethod(get)
    def body = get.getResponseBodyAsString()
    //def body = handleResponse(get.getResponseBodyAsStream())
    get.releaseConnection()
    return body
}

def sendHttpPostRequest(String requestUrl, String data){
    RequestEntity entity = new StringRequestEntity(data,"application/json","UTF-8");
    PostMethod post = new PostMethod(requestUrl);
    post.setRequestEntity(entity);
    post.setRequestHeader("Accept-Charset", "utf-8")
    post.setRequestHeader("Content-Type","application/json;charset=utf-8");
    new HttpClient().executeMethod(post);
    def body = post.getResponseBodyAsString()
    //def body = handleResponse(post.getResponseBodyAsStream())
    post.releaseConnection()
    return body
}

def handleResponse(inputStream){
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))
    def temp = ""
    def body = ""
    while((temp=reader.readLine())!=""){
        body += temp + "\r\n"
    }
    return body
}

@NonCPS
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}
