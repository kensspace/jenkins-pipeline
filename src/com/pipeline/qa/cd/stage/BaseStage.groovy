package com.pipeline.qa.cd.stage

class BaseStage implements Serializable{

    protected def context
    protected def config
    protected def util
    protected def params
    protected String stageName
    protected String baseUrl
    protected String shortStageName
    protected String language

    BaseStage(context, config, util, params, stageName){
        this.context = context
        this.config = config
        this.util = util
        this.params = params
        this.stageName = stageName
        this.baseUrl = config.MULTI_ENV.get(params.workEnv)

        def compiler = params.appDeliveryInfo.build.param.language.toLowerCase()
        if (compiler.matches(".*jdk.*")) {
            this.language = "java"
        } else if (compiler.matches(".*python.*")) {
            this.language = "python"
        } else if (compiler.matches(".*php.*")) {
            this.language = "php"
        } else if (compiler.matches(".*nodejs.*")) {
            this.language = "nodejs"
        } else {
            this.language = "other"
        }

        for(item in config.STAGE_LIST){
            if(item.key == stageName){
                this.shortStageName = item.value.shortName
            }
        }

    }

    //调用stage job之前调用的方法
    def beforeStageJob(){
        def before_info = util.sendHttpPostRequest(baseUrl+config.STATUS_URL, this.genStartJson("running", "running"))
        context.echo "The response to call status interface of ${stageName} start :\n ${before_info}"
    }

    //调用stage job之后调用的方法
    def afterStageJob(){
        def after_info = util.sendHttpPostRequest(baseUrl+config.STATUS_URL, this.genEndJson("running", "succeeded"))
        context.echo "The response to call status interface of ${stageName} end :\n ${after_info}"
    }

    //stage job执行出错时调用的方法
    def errorStageJob(err){
        def error_info = util.sendHttpPostRequest(baseUrl+config.STATUS_URL, this.genStatusJson("failed", "failed", "${stageName} failed"))
        context.echo "The response to call status interface of ${stageName} error :\n ${error_info}"
        context.echo "Caught: ${stageName} failed. Please check the value of parameters.\n ${err}"
        context.error "${stageName} error, please check the value of parameters."
    }

    //方法体由具体的子类去实现
    def stageJob() throws Exception{
    }

    //运行stage
    def run() throws Exception{
        context.stage(stageName){
            //判断是否执行对应的stage
            def isNeccesary = params.appDeliveryInfo.stage.get(shortStageName, true)

            if(!isNeccesary){
                context.echo "Skip the stage of ${stageName}"
            }else{
                context.echo this.stageName

                this.beforeStageJob() //开始前的动作
                try{
                    this.stageJob()
                }catch(err){
                    this.errorStageJob(err)
                    throw err
                }
                this.afterStageJob()
            }
        }
    }

    //生成stageJob开始之前调用CI status接口传输的数据。默认使用genStatusJson，如有需要重写此方法
    def genStartJson(String ppStatus, String stageStatus){
        return genStatusJson(ppStatus, stageStatus)
    }

    //生成stageJob结束调用CI status接口传输的数据。默认使用genStatusJson，如有需要重写此方法
    def genEndJson(String ppStatus, String stageStatus){
        return genStatusJson(ppStatus, stageStatus)
    }

    def genStatusJson(String ppStatus, String stageStatus, String failMsg="") {
        String json = """
            {
                "deliveryJobId": "${params.deliveryJobId}",
                "pipeline": {
                    "serviceId": "${params.appDeliveryInfo.serviceId}",
                    "status": "${ppStatus}",
                    "buildNumber": "${context.BUILD_NUMBER}",
                    "stage": {
                        "name": "${shortStageName}", "status": "${stageStatus}", "failMsg": "${failMsg}"
                    }
                }
            }
            """
        return json
    }

}
