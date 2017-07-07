package com.pipeline.qa.cd.stage

class PerTestStage extends BaseStage{

    PerTestStage(context, config, util, params, stageName){
        super(context, config, util, params, stageName)
    }

    def stageJob() throws Exception{
        context.echo "There is nothing to do in perfomance test yet"
        // do something to checkout code
    }
}
