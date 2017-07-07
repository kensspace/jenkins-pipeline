/**
 * Created by ken on 17/2/9.
 */

package com.pipeline.qa.cd

import groovy.json.JsonSlurper
import com.pipeline.qa.cd.Utils

def checkoutCode(Map params) {
    echo "Checkout Code"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.checkoutCode, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of CheckoutCode start :\n" + run_result)

    try {
        if (params.gitUrl.matches(".*git@.*")) {
            git url: "${params.gitUrl}", branch: "${params.gitBranch}"
        } else {
            git url: "ssh://jenkins@${params.gitUrl}", branch: "${params.gitBranch}"
        }
        // if (params.gitTag) {
        //     def grep_tag ="^"+params.gitTag+"\$"
        //     def tag = sh(script: "git tag -l | grep ${grep_tag} && echo \$? ").toString().trim()
        //     if (tag) {
        //         sh "git checkout ${params.gitTag}"
        //     }
        if (params.gitTag) {
            sh "git checkout ${params.gitTag}"
        } else {
            // if (params.gitCommit) {
            //     sh "git checkout ${params.gitCommit}"
            // } else {
            //     sh "git checkout ${params.gitBranch}"
            // }
            sh "git checkout ${params.gitBranch}"
        }
        // Create a Dockerfile
    } catch (err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.checkoutCode, params.stageStatus.failed, "checkout code failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of CheckoutCode failed :\n" + err_result)
        echo "Caught: checkout code failed. Please check the value of git tag/commit/master.\n ${err}"
        error "checkout code error, please check the value of git tag/commit/master."
    }

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.checkoutCode, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of CheckoutCode succeeded :\n" + end_result)
}

def cCode(Map params) {
    echo "Checkout Code"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.checkoutCode, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of CheckoutCode start :\n" + run_result)

    try {
        git url: "ssh://jenkins@${params.gitUrl}", branch: "${params.gitBranch}"
        sh "git checkout ${params.gitBranch}"
        // Create a Dockerfile
        if (!fileExists('Dockerfile')) {
            sh "touch Dockerfile"
            writeFile file: "Dockerfile", text: "${params.dockerfile}"
        }
    } catch (err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.checkoutCode, params.stageStatus.failed, "checkout code failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of CheckoutCode failed :\n" + err_result)
        echo "Caught: checkout code failed. ${err}"
        error "checkout code error"
    }

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.checkoutCode, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of CheckoutCode succeeded :\n" + end_result)
}

def codeScan(Map params) {
    echo "Code Scan"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.codeScan, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of CodeScan start :\n" + run_result)

    try {
        def cs_info = utils.genCodeScanJson(params.deliveryJobId, params.serviceId)
        def cs_result = utils.curl_post(params.codescan_url, cs_info)
        println("The result of CodeScan as follow :\n" + cs_result)
    } catch (err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.codeScan, params.stageStatus.failed, "codeScan failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of codeScan failed :\n" + err_result)
        echo "Caught: codeScan failed. ${err}"
    }

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.codeScan, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of CodeScan succeeded :\n" + end_result)
}

def unitTest(Map params) {
    echo "Unit Test"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.ut, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of UnitTest start :\n" + run_result)

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.ut, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of UnitTest succeeded :\n" + end_result)
}

def bbuild(Map params) {
    echo "Build"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.build, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of Build start :\n" + run_result)

    try {
        def compiler = params.language
        withEnv(["JAVA_HOME=${ tool compiler}", "PATH+MAVEN=${tool 'mvn3'}/bin:${env.JAVA_HOME}/bin"]) {
            sh 'mvn clean install -Dmaven.test.skip=true'
            step([$class: 'ArtifactArchiver', artifacts: '**/target/*.war', fingerprint: true])
        }
    } catch(err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.build, params.stageStatus.failed, "build failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of Build failed :\n" + err_result)
        echo "Caught: build failed. ${err}"
        error "build error"
    }

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.build, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of Build succeeded :\n" + end_result)
}

//the project that don't need compile
def nobuild(Map params) {
    echo "Build"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.build, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of Build start :\n" + run_result)

    try {
        sh "if [ -f ./sysconf/build.sh ]; then chmod +x ./sysconf/build.sh && ./sysconf/build.sh ${params.env}; fi"
    } catch(err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.build, params.stageStatus.failed, "build failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of Build failed :\n" + err_result)
        echo "Caught: build failed. ${err}"
        error "build error"
    }

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.build, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of Build succeeded :\n" + end_result)
}

def build(Map params) {
    echo "Build"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.build, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of Build start :\n" + run_result)

    try {
        def compiler = params.language
        withEnv(["JAVA_HOME=${ tool compiler}", "PATH+MAVEN=${tool 'mvn3'}/bin:${env.JAVA_HOME}/bin"]) {
            if (params.command) {
                def buildCommand = utils.getBuildParam(params.command)
                sh "${buildCommand}"
            } else {
                sh 'mvn clean install -Dmaven.test.skip=true'
            }
        }
    } catch(err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.build, params.stageStatus.failed, "build failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of Build failed :\n" + err_result)
        echo "Caught: build failed. ${err}"
        error "build error"
    }

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.build, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of Build succeeded :\n" + end_result)
}

def docker(Map params) {
    echo "Docker Push"
    def utils = new Utils()

    def imageName = "${params.dockerRegistry}/${params.namespace}/${params.appName}:${params.version}"
    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.docker, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of Docker stage start :\n" + run_result)

    try {
        if (!fileExists('Dockerfile')) {
            sh "touch Dockerfile"
            writeFile file: "Dockerfile", text: "${params.dockerfile}"
        }
    } catch (err) {
        def dfile_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.docker, params.stageStatus.failed, "gen dockerfile failed")
        def dfile_result = utils.curl_post(params.status_url, dfile_info)
        println("The response to call status interface of Docker gen-dockerfile failed :\n" + dfile_result)
        echo "Caught: generate dockerfile failed.\n ${err}"
        error "generate dockerfile error."
    }

    try {
        if (params.lang.equalsIgnoreCase("JAVA")) {
            echo "${params.command}"
            def dockerCommand = utils.getDockerParam(params.command)
            def buildtype = utils.getJavabuildtype(params.dockerfile)
            if (buildtype.equalsIgnoreCase("war")) {
                sh "cp -rf `find . -name *.war` . && cp ./*.war project"
                step([$class: 'ArtifactArchiver', artifacts: '*.war', fingerprint: true])
            } else if (buildtype.equalsIgnoreCase("jar")) {
                if(dockerCommand){
                    echo "${dockerCommand}"
                    sh "mkdir -p ./project/lib/ && mkdir -p ./project/bin/"
                    sh "${dockerCommand}"
                    sh "cp ./project/lib/*.jar ."
                }else{
                    sh "mkdir -p ./project/lib/ && cp -rf `find . -name *.jar -ls | sort -nr -k 7 | head -1 | awk {'print \$11'}` . && cp ./*.jar project/lib/"
    //                sh 'mkdir -p ./project/lib/ && cp -rf `find . -name *.jar -exec stat -c "%s %n" {} \;| sort -nr | head -1| awk {'print $2'}` . && cp ./*.jar project/lib/'
    //                sh "mkdir -p ./project/lib/ && cp -rf `find . -name *.jar` . && cp ./*.jar project/lib/"
                    sh 'if [ -d bin ]; then cp -rf bin project/; else mkdir -p ./project/bin/ && cd project/bin/ && wget http://10.213.58.204/run_app.sh; chmod +x run_app.sh; fi'
                }
                step([$class: 'ArtifactArchiver', artifacts: '*.jar', fingerprint: true])


            }
        } else if (params.lang.equalsIgnoreCase("python")) {
            sh 'mkdir project && cp -rf `ls | grep -E -v "^project$"` project/'
        } else if (params.lang.equalsIgnoreCase("nodejs")) {
            sh 'mkdir project && cp -rf `ls | grep -E -v "^project$"` project/'
        } else if (params.lang.equalsIgnoreCase("php")) {
            sh 'mkdir project && cp -rf `ls | grep -E -v "^project$"` project/'
        } else {
            echo "The content of language field is wrong or null."
        }

    } catch (err) {
        def project_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.docker, params.stageStatus.failed, "make project failed")
        def project_result = utils.curl_post(params.status_url, project_info)
        println("The response to call status interface of Docker make-project failed :\n" + project_result)
        echo "Caught: make project failed. ${err}"
        error "make project error"
    }

    //docker build
    try {
        sh "sudo docker build -t ${imageName} ."
    } catch (err) {
        def build_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.docker, params.stageStatus.failed, "docker build failed")
        def build_result = utils.curl_post(params.status_url, build_info)
        println("The response to call status interface of Docker build failed :\n" + build_result)
        echo "Caught: docker build failed. ${err}"
        error "docker build error"
    }

    //docker push
    try {
        retry(5) {
            def dockerImageCommitID = sh([script: "sudo docker push ${imageName}", returnStdout: true]).toString().trim()
        }
    } catch (err) {
        def push_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.docker, params.stageStatus.failed, "docker push failed")
        def push_result = utils.curl_post(params.status_url, push_info)
        println("The response to call status interface of Docker push failed :\n" + push_result)
        echo "Caught: docker push failed. ${err}"
        erro "docker push error"
    }

    def docker_info = utils.genDockerJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, imageName, params.version, params.commit, params.buildNumber, "docker", params.stageStatus.succeeded)
    def docker_res = utils.curl_post(params.status_url, docker_info)
    println("The response to call status interface of Docker stage succeeded :\n" + docker_res)
}

def ddocker(Map params) {
    echo "Docker Push"
    def utils = new Utils()

    def imageName = "${params.dockerRegistry}/${params.namespace}/${params.appName}:${params.version}"
    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.docker, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of Docker stage start :\n" + run_result)

    try {
        if (params.lang.equalsIgnoreCase("JAVA")) {
            sh "cp -f ./*/target/*.war . && mv ./*.war project"
        } else if (params.lang.equalsIgnoreCase("python")) {
            sh 'mkdir project && cp -rf `ls | grep -E -v "^project$"` project/'
        } else if (params.lang.equalsIgnoreCase("nodejs")) {
            sh 'mkdir project && cp -rf `ls | grep -E -v "^project$"` project/'
        } else {
            echo "The content of language field is wrong or null."
        }

    } catch (err) {
        def project_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.docker, params.stageStatus.failed, "make project failed")
        def project_result = utils.curl_post(params.status_url, project_info)
        println("The response to call status interface of Docker make-project failed :\n" + project_result)
        echo "Caught: make project failed. ${err}"
        error "make project error"
    }

    //docker build
    try {
        sh "sudo docker build -t ${imageName} ."
    } catch (err) {
        def build_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.docker, params.stageStatus.failed, "docker build failed")
        def build_result = utils.curl_post(params.status_url, build_info)
        println("The response to call status interface of Docker build failed :\n" + build_result)
        echo "Caught: docker build failed. ${err}"
        error "docker build error"
    }

    //docker push
    try {
        retry(5) {
            def dockerImageCommitID = sh([script: "sudo docker push ${imageName}", returnStdout: true]).toString().trim()
        }
    } catch (err) {
        def push_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.docker, params.stageStatus.failed, "docker push failed")
        def push_result = utils.curl_post(params.status_url, push_info)
        println("The response to call status interface of Docker push failed :\n" + push_result)
        echo "Caught: docker push failed. ${err}"
        erro "docker push error"
    }

    def docker_info = utils.genDockerJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, imageName, params.version, params.commit, params.buildNumber, "docker", params.stageStatus.succeeded)
    def docker_res = utils.curl_post(params.status_url, docker_info)
    println("The response to call status interface of Docker stage succeeded :\n" + docker_res)
}

def deployment(Map params) {
    echo "Deployment"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.deploy, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of Deployment start :\n" + run_result)

    try {
//        def deploy_url = "http://juno.intra.sit.com.com/api/wf/v1/deploy"
        def deploy_info = utils.genDeployJson(params.deliveryJobId, params.serviceId)
        def deploy_result = utils.curl_post(params.deploy_url, deploy_info)
        println("The response to call status interface of CI-server deploy :\n" + deploy_result)
        def res_json = utils.jsonParse(deploy_result)
        println res_json
        if (res_json.errCode == 0) {
            echo "Triggering CI deployment is successful."
        } else {
            echo "Triggering CI deployment is FAILURE."
        }
    } catch (err) {
        def trg_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.deploy, params.stageStatus.failed, "trigger ci deploy failed")
        def trg_result = utils.curl_post(params.status_url, trg_info)
        println("The response to call status interface of trigger ci deploy failed :\n" + trg_result)
        echo "Caught: Trigger ci deploy failed. ${err}"
        error "trigger ci deploy error"
    }

    def pending_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.pending, params.buildNumber, params.stageName.deploy, params.stageStatus.running)
    def pending_result = utils.curl_post(params.status_url, pending_info)
    println("The response to call status interface of Deployment pending :\n" + pending_result)

    try {
        // pipeline pausable
        timeout(time: 5, unit: 'MINUTES') {
            def userInput = input(
                id: 'inputid1', message: 'Do you want to continue this building?', submitter: 'jenkins'
            )
        }
    } catch (err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.deploy, params.stageStatus.failed, "CD deploy failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of CD deploy failed :\n" + err_result)
        echo "Caught: Deployment was abort. ${err}"
        error "CD deploy error"
    } finally {
    //    def deploylog_url = "http://10.213.42.149:10080/api/wfc2/v1/deploy/log?serviceId="
        def log_result = utils.curl_get(params.deploylog_url, params.serviceId)
        println("The logs of deployment as follow :\n" + log_result)
    }

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.deploy, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of Deployment succeeded :\n" + end_result)
}

def autoTest(Map params) {
    echo "Testing Automation"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.at, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of AutoTest start :\n" + run_result)

    def test_info = utils.genTestJson(params.deliveryJobId, params.serviceId, params.environment, params.appName, params.gitUrl, params.language)
//    def test_url = "http://juno.intra.sit.com.com/api/wfc2/v1/starttest"
    try {
        def test_res = utils.curl_post(params.test_url, test_info)
        println("The response to call status interface of CI-server autotest :\n" + test_res)
        def res_json = utils.jsonParse(test_res)
        println res_json
        assert res_json instanceof Map
        //println "------JsonOutput------"
        //println JsonOutput.prettyPrint(res_json.toString())
        if (res_json.errCode == 0) {
            echo "Triggering CI autotest is successful."
        } else {
            echo "Triggering CI autotest is FAILURE."
        }
    } catch (err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.at, params.stageStatus.failed, failMsg= "trigger ci autotest failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of trigger ci autotest failed :\n" + err_result)
        echo "Caught: Trigger ci autotest failed. ${err}"
        error "trigger ci autotest error"
    }

    def pending_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.pending, params.buildNumber, params.stageName.at, params.stageStatus.running)
    def pending_result = utils.curl_post(params.status_url, pending_info)
    println("The response to call status interface of AutoTest pending :\n" + pending_result)

    try {
        // pipeline pausable
        timeout(time: 5, unit: 'MINUTES') {
            def userInput = input(
                id: 'testautomation', message: 'Do you want to continue this building?', submitter: 'jenkins'
            )
        }
    } catch (err) {
        def err_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.failed, params.buildNumber, params.stageName.at, params.stageStatus.failed, failMsg= "autotest failed")
        def err_result = utils.curl_post(params.status_url, err_info)
        println("The response to call status interface of AutoTest failed :\n" + err_result)
        echo "Caught: Autotest was abort. ${err}"
        error "autotest error"
    }

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.at, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of AutoTest succeeded :\n" + end_result)
}

def performanceTest(Map params) {
    echo "Performace Test"
    def utils = new Utils()

    def run_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.pt, params.stageStatus.running)
    def run_result = utils.curl_post(params.status_url, run_info)
    println("The response to call status interface of PerformanceTest start :\n" + run_result)

    def end_info = utils.genStatusJson(params.deliveryJobId, params.serviceId, params.ppStatus.running, params.buildNumber, params.stageName.pt, params.stageStatus.succeeded)
    def end_result = utils.curl_post(params.status_url, end_info)
    println("The response to call status interface of PerformanceTest succeeded :\n" + end_result)
}

return this
