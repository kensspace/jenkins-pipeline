#!/usr/bin/groovy

package com.pipeline.qa.cd

import groovy.json.JsonSlurper
import hudson.FilePath
import groovy.xml.*;

//import hudson.model.*;
//import hudson.maven.*;
//import jenkins.model.*;
//import jenkins.maven.*
import jenkins.model.Jenkins;

import jenkins.*;
import jenkins.model.*;

import hudson.*;
import hudson.model.*;
import hudson.util.*;

import hudson.FilePath;

import javax.activation.*;

def createDockerfile(String content) {

//    def build = Thread.currentThread().executable

    if (manager.build.workspace.isRemote()) {
        channel = manager.build.workspace.channel;
        fp = new FilePath(channel, manager.build.workspace.toString() + "/Dockerfile")
    } else {
        fp = new FilePath(new File(manager.build.workspace.toString() + "/Dockerfile"))
    }

    if (fp != null) {
        fp.write("FROM 10.213.42.254:10500/base/tomcat_base_1.8:1.3", null); //writing to file
    }

}

def getProjectVersion(){
    def file = readFile('pom.xml')
    def project = new XmlSlurper().parseText(file)
    return project.version.text()
}

return this;
