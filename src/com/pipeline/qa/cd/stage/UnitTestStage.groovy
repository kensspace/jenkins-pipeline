package com.pipeline.qa.cd.stage

class UnitTestStage extends BaseStage{

    UnitTestStage(context, config, util, params, stageName){
        super(context, config, util, params, stageName)
    }

    def stageJob() throws Exception{
        context.echo "There is nothing to do in unit test yet"
        // do something to checkout code
    }
}
