package com.pipeline.qa.cd.stage

class ScanCodeStage extends BaseStage{

    ScanCodeStage(context, config, util, params, stageName){
        super(context, config, util, params, stageName)
    }

    def stageJob() throws Exception{
        def result = util.sendHttpPostRequest(baseUrl+config.CODESCAN_URL, genCodeScanJson())
        context.echo("The result of CodeScan as follow :\n ${result}")
        def res_json = util.jsonParse(result)
        assert res_json instanceof Map
        if (res_json.errCode == 200) {
            context.echo "Triggering Sonar Scan successful"
        } else if(res_json.errCode == 0){
            context.echo "Get Sonar Result failed."
        }else{
            context.echo "Triggering Sonar Scan failed"
        }
    }

    def genCodeScanJson() {
        String json = """
            {
                "deliveryJobId": "${params.deliveryJobId}",
                "serviceId": "${params.appDeliveryInfo.serviceId}"
            }
            """
        return json
    }
}
