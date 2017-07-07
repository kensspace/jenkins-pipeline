package com.pipeline.qa.cd.stage

class DeployStage extends BaseStage{

    DeployStage(context, config, util, params, stageName){
        super(context, config, util, params, stageName)
    }

    def stageJob() throws Exception{
        def result = util.sendHttpPostRequest(baseUrl+config.DEPLOY_URL, genDeployJson())
        context.echo("The response to call status interface of CI-server deploy :\n ${result}")
        def res_json = util.jsonParse(result)
        if (res_json.errCode == 0) {
            context.echo "Triggering CI deployment is successful."
        } else {
            context.echo "Triggering CI deployment is FAILURE."
        }

        context.timeout(time: 5, unit: 'MINUTES') {
            def userInput = context.input(
                id: 'inputid1', message: 'Do you want to continue this building?', submitter: 'jenkins'
            )
        }

        def log_result = util.sendHttpGetRequest(baseUrl+config.DEPLOY_LOG_URL + params.appDeliveryInfo.serviceId)
        def log_json = util.jsonParse(log_result)
        if(log_json.errCode != 0){
            context.echo "Get logs of deplyoment failed"
        }else{
            context.echo "The logs of deployment as follow :\n ${log_json.data.text}"
        }
    }

    def genDeployJson() {
        String json = """
            {
                "deliveryJobId": "${params.deliveryJobId}",
                "serviceId": "${params.appDeliveryInfo.serviceId}"
            }
            """
        return json
    }
}
