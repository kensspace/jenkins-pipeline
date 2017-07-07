package com.pipeline.qa.cd

class StageFactory implements Serializable{

    def context
    def config
    def util
    def params

    StageFactory(context, config, util, params){
        this.context = context
        this.config = config
        this.util = util
        this.params = params
    }

    def getStage(stageName){

        def stageClass = getStageClass(stageName)

        try{
            Class c = Class.forName("${config.STAGE_PATH}.${stageClass}", true, this.class.classLoader)
            return c.newInstance([context, config, util, params, stageName] as Object[])
        }catch(err){
            context.error err
            return null
        }

    }

    def getStageClass(stageName){
        for(item in config.STAGE_LIST){
            if(item.key == stageName){
                return item.value.stage
            }
        }
    }
}
