package com.pipeline.qa.cd


def getConfig(env) {
    def multiEnv = ["sit": "http://juno.intra.sit.com.com", "test": "http://juno.intra.test.com.com",
                    "uat": "http://juno.intra.uat.com.com", "prod": "http://juno.intra.com.com"]
    def url = multiEnv.get(env)
    String string = """
        {
            "ppStatus" : {"running":"running", "pending":"pending", "succeeded":"succeeded", "failed":"failed"},
            "stageStatus" : {"running":"running", "succeeded":"succeeded", "failed":"failed"},
            "stageName" : {"checkoutCode":"checkoutCode", "codeScan":"codeScan", "ut":"ut", "build":"build",
                        "docker":"docker", "deploy":"deploy", "at":"at", "pt":"pt"},
            "status_url" : "${url}/api/wfc2/v1/delivery_status",
            "codescan_url" : "${url}/api/wfc2/v1/sonar/scan",
            "deploy_url" : "${url}/api/wfc2/v1/deploy",
            "deploylog_url" : "${url}/api/wfc2/v1/deploy/log?serviceId=",
            "test_url" : "${url}/api/wfc2/v1/starttest"
        }
        """
    return string
}

return this