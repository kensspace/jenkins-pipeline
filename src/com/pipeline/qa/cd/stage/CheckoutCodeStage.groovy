package com.pipeline.qa.cd.stage

class CheckoutCodeStage extends BaseStage{

    CheckoutCodeStage(context, config, util, params, stageName){
        super(context, config, util, params, stageName)
    }

    def stageJob() throws Exception{

        def gitUrl = params.appDeliveryInfo.git.url
        def gitBranch = params.appDeliveryInfo.git.branch
        def gitTag = params.appDeliveryInfo.git.tag

        if (gitUrl.matches(".*git@.*")) {
            context.git url: "${gitUrl}", branch: "${gitBranch}"
        } else {
            context.git url: "ssh://jenkins@${gitUrl}", branch: "${gitBranch}"
        }
        if (params.gitTag) {
            context.sh "git checkout ${gitTag}"
        } else {
            context.sh "git checkout ${gitBranch}"
        }
    }
}
