package com.pipeline.qa.cd.stage

class BuildStage extends BaseStage{

    BuildStage(context, config, util, params, stageName){
        super(context, config, util, params, stageName)
    }

    def stageJob() throws Exception{

        if("JAVA".equalsIgnoreCase(language)){
            def command = params.appDeliveryInfo.build.command
            def jdk = params.appDeliveryInfo.build.param.language
            context.withEnv(["JAVA_HOME=${context.tool jdk}", "PATH+MAVEN=${context.tool 'mvn3'}/bin:${context.env.JAVA_HOME}/bin"]) {
                if (command) {
                    def buildCommand = getBuildParam(command)
                    context.sh "${buildCommand}"
                } else {
                    context.sh 'mvn clean install -Dmaven.test.skip=true'
                }
            }
        }else{
            def env = params.environment.toLowerCase()
            context.sh "if [ -f ./sysconf/build.sh ]; then chmod +x ./sysconf/build.sh && ./sysconf/build.sh ${env}; fi"
        }
    }

    def getBuildParam(String params){
        if(params.indexOf("&") < 0){
            return params
        }else{
            return params.substring(0,params.indexOf("&&"))
        }
    }

}
