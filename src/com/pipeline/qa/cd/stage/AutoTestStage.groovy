package com.pipeline.qa.cd.stage

class AutoTestStage extends BaseStage{

    AutoTestStage(context, config, util, params, stageName){
        super(context, config, util, params, stageName)
    }

    def stageJob() throws Exception{
        def test_res = util.sendHttpPostRequest(baseUrl+config.TEST_URL, genTestJson())
        context.echo "The response to call status interface of CI-server autotest :\n ${test_res}"
        context.echo test_res
        def res_json = util.jsonParse(test_res)
        assert res_json instanceof Map
        if (res_json.errCode == 0) {
            context.echo "Triggering CI autotest is successful."
        } else {
            context.echo "Triggering CI autotest is FAILURE."
        }

        context.timeout(time: 5, unit: 'MINUTES') {
            def userInput = context.input(
                id: 'testautomation', message: 'Do you want to continue this building?', submitter: 'jenkins'
            )
        }
    }

    def genTestJson() {
        String json = """
            {
                "deliveryJobId":"${params.deliveryJobId}",
                "environment":"${params.environment}",
                "testconfig":[
                    {
                        "serviceId":"${params.appDeliveryInfo.serviceId}",
                        "serviceName":"${params.appDeliveryInfo.appName}",
                        "git":"${params.appDeliveryInfo.git.url}",
                        "language": "${params.appDeliveryInfo.build.param.language}"
                    }
                ]
            }
            """
        return json
    }
}
