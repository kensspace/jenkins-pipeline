package com.pipeline.qa.cd.stage

class DockerPushStage extends BaseStage{

    def imageName
    def version
    def commit

    DockerPushStage(context, config, util, params, stageName){
        super(context, config, util, params, stageName)
    }

    def stageJob() throws Exception{

        this.commit = context.sh(script: 'git rev-parse --short HEAD', returnStdout: true).toString().trim()
        context.echo "git commitID: ${commit}"
        def now = new Date()
        def timestamp = now.time.toString().trim()[-1..6]
        this.version = commit + timestamp

        this.imageName = "${params.dockerRegistry}/${params.namespace}/${params.appDeliveryInfo.appName.toLowerCase()}:${version}"

        def workDir = params.appDeliveryInfo.build.param.workdir
        if (workDir) {
            context.dir(workDir) {
                doDocker()
            }
        }else{
            doDocker()
        }
    }

    //重写genEndJson
    def genEndJson(String ppStatus, String stageStatus){
        String json = """
            {
                "deliveryJobId": "${params.deliveryJobId}",
                "pipeline": {
                    "serviceId": "${params.appDeliveryInfo.serviceId}",
                    "status": "${ppStatus}",
                    "imageName": "${imageName}",
                    "imageTag": "${version}",
                    "commitId":"${commit}",
                    "buildNumber": "${context.BUILD_NUMBER}",
                    "stage": {
                        "name": "${shortStageName}", "status": "${stageStatus}", "failMsg": ""
                    }
                }
            }
            """
        return json
    }

    def doDocker(){
        this.prepareDocker()
        this.buildImage()
        this.pushImage()
    }

    def prepareDocker(){
        def dockerfile = params.appDeliveryInfo.dockerfile

        if (!context.fileExists('Dockerfile')) {
            context.sh "touch Dockerfile"
            context.writeFile file: "Dockerfile", text: "${dockerfile}"
        }

        def command = params.appDeliveryInfo.build.command
        if ("JAVA".equalsIgnoreCase(language)) {
            def dockerCommand = getDockerParam(command)
            def buildtype = getJavabuildtype(params.appDeliveryInfo.dockerfile)
            if (buildtype.equalsIgnoreCase("war")) {
                context.sh "cp -rf `find . -name *.war` . && cp ./*.war project"
                context.step([$class: 'ArtifactArchiver', artifacts: '*.war', fingerprint: true])
            } else if (buildtype.equalsIgnoreCase("jar")) {
                if(dockerCommand){
                    context.sh "mkdir -p ./project/lib/ && mkdir -p ./project/bin/"
                    context.sh "${dockerCommand}"
                    context.sh "cp ./project/lib/*.jar ."
                }else{
                    context.sh "mkdir -p ./project/lib/ && cp -rf `find . -name *.jar -ls | sort -nr -k 7 | head -1 | awk {'print \$11'}` . && cp ./*.jar project/lib/"
                    context.sh 'if [ -d bin ]; then cp -rf bin project/; else mkdir -p ./project/bin/ && cd project/bin/ && wget http://10.213.58.204/run_app.sh; chmod +x run_app.sh; fi'
                }
                context.step([$class: 'ArtifactArchiver', artifacts: '*.jar', fingerprint: true])


            }
        } else if ("python".equalsIgnoreCase(language)) {
            context.sh 'mkdir project && cp -rf `ls | grep -E -v "^project$"` project/'
        } else if ("nodejs".equalsIgnoreCase(language)) {
            context.sh 'mkdir project && cp -rf `ls | grep -E -v "^project$"` project/'
        } else if ("php".equalsIgnoreCase(language)) {
            context.sh 'mkdir project && cp -rf `ls | grep -E -v "^project$"` project/'
        } else {
            context.echo "The content of language field is wrong or null."
        }
    }

    def buildImage(){
        context.sh "sudo docker build -t ${imageName} ."
    }

    def pushImage(){
        context.retry(5) {
            def dockerImageCommitID = context.sh([script: "sudo docker push ${imageName}", returnStdout: true]).toString().trim()
        }
    }

    def getDockerParam(String params){
        if(params.indexOf("&&") < 0){
            return null
        }else{
            return params.substring(params.indexOf("&&")+2, params.length())
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
}
