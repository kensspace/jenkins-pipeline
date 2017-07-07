package com.pipeline.qa.cd

import groovy.json.JsonSlurperClassic

import hudson.model.*
import hudson.model.Slave
import hudson.model.Hudson
import jenkins.model.Jenkins


@NonCPS
def jsonParse(def json) {
    new groovy.json.JsonSlurperClassic().parseText(json)
}

def curl_post(url, data, cookies="") {
    try {

        def method = "POST"
        def header = "Content-Type: application/json"
        //def post_content=data
        def res = sh([script: "curl -X ${method} -H '${header}' -d '${data}' ${cookies} ${url}", returnStdout: true]).toString().trim()
        return res

    } catch (Exception ex) {
        println("In curl_post, there is exception happened")
        ex.printStackTrace()
        println(ex.toString())
    }
}

def curl_get(url, sid, cookies="") {
    try {
        def log_url = url + sid
        def method = "GET"
        def header1 = "Accept: application/json"
        def header2 = "Content-Type: application/json"
        def res = sh([script: "curl -X ${method} -H '${header1}' -H '${header2}' ${cookies} ${log_url}", returnStdout: true]).toString().trim()
        return res

    } catch (Exception ex) {
        println("In curl_post, there is exception happened")
        ex.printStackTrace()
        println(ex.toString())
    }
}

def getLanguage(String compl) {
    def compiler = compl.toLowerCase()
    if (compiler.matches(".*jdk.*")) {
        return "java"
    } else if (compiler.matches(".*python.*")) {
        return "python"
    } else if (compiler.matches(".*php.*")) {
        return "php"
    } else if (compiler.matches(".*nodejs.*")) {
        return "nodejs"
    } else {
        return "other"
    }
}

def getJavabuildtype(String dockerfile) {
    if (dockerfile.matches(".*tomcat.*")) {
        return "war"
    } else if (dockerfile.matches(".*java.*")) {
        return "jar"
    } else {
        return "other"
    }
}

def node_optimization() {
    Jenkins jenkins = Jenkins.instance
    def jenkinsSlaves = jenkins.slaves

    def map = [:]
    for (aSlave in jenkinsSlaves)
    {
        if (!aSlave.getComputer().isOffline())
        {
            def name = aSlave.name
            def idleExe = aSlave.getComputer().countIdle()
            map.put(name, idleExe)
        }
        else
        {
            println(aSlave.name + "is offline!!!")
        }

    }

    def sm = sortbyvalueMap(map)
    def kset = sm.keySet()
    def maxkey = kset.getAt(0)
    def maxvalue = sm.get(maxkey)

    def selected = selectsameMap(map, maxvalue)
    def slist = selected.keySet()
    def size = selected.size()
    println("the selected is : " + selected)

    def random = new Random()
    def num = random.nextInt(size)
    println("the random num is : " + num)
    def nodename = slist.getAt(num)
    println("The idle node is : " + nodename)

    return nodename
}

@NonCPS
def sortbyvalueMap(def map) {
    def result = map.sort(){ a, b ->
            (int)b.value - (int)a.value
    }
}

@NonCPS
def selectsameMap(def map, def maxvalue) {
    def select = map.findAll{ node, exe ->
        exe == maxvalue
    }
}

def callStatusApi() {

}

def callTestApi() {

}

def genStatusJson(String deliveryJobId, String serviceId, String ppStatus, String buildNumber, String stageName, String stageStatus, String failMsg="") {
    String json = """
        {
            "deliveryJobId": "${deliveryJobId}",
            "pipeline": {
                "serviceId": "${serviceId}",
                "status": "${ppStatus}",
                "buildNumber": "${buildNumber}",
                "stage": {
                    "name": "${stageName}", "status": "${stageStatus}", "failMsg": "${failMsg}"
                }
            }
        }
        """
    return json
}

def genCodeScanJson(String deliveryJobId, String serviceId) {
    String json = """
        {
            "deliveryJobId": "${deliveryJobId}",
            "serviceId": "${serviceId}"
        }
        """
    return json
}

def genDockerJson(String deliveryJobId, String serviceId, String ppStatus, String imageName, String version, String commit, String buildNumber, String stageName, String stageStatus, String failMsg="") {
    String json = """
        {
            "deliveryJobId": "${deliveryJobId}",
            "pipeline": {
                "serviceId": "${serviceId}",
                "status": "${ppStatus}",
                "imageName": "${imageName}",
                "imageTag": "${version}",
                "commitId":"${commit}",
                "buildNumber": "${buildNumber}",
                "stage": {
                    "name": "${stageName}", "status": "${stageStatus}", "failMsg": "${failMsg}"
                }
            }
        }
        """
    return json
}

def genDeployJson(String deliveryJobId, String serviceId) {
    String json = """
        {
            "deliveryJobId": "${deliveryJobId}",
            "serviceId": "${serviceId}"
        }
        """
    return json
}

def genTestJson(String deliveryJobId, String serviceId, String environment, String serviceName, String gitUrl, String language) {
    String json = """
        {
            "deliveryJobId":"${deliveryJobId}",
            "environment":"${environment}",
            "testconfig":[
                {
                    "serviceId":"${serviceId}",
                    "serviceName":"${serviceName}",
                    "git":"${gitUrl}",
                    "language": "${language}"
                }
            ]
        }
        """
    return json
}

def getBuildParam(String params){
    if(params.indexOf("&") < 0){
        return params
    }else{
        return params.substring(0,params.indexOf("&&"))
    }
}

def getDockerParam(String params){
    if(params.indexOf("&&") < 0){
        return null
    }else{
        return params.substring(params.indexOf("&&")+2, params.length())
    }
}

return this
