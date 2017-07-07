package com.pipeline.qa.cd

class RcConfig implements Serializable{

    def STAGE_PATH = 'com.pipeline.qa.cd.stage'  //stage类路径的前缀

    def STAGE_LIST = mapToList([
       'Checkout Code': [  //stage 名
           'shortName': 'checkoutCode',  //stage对应的简称，对应参数里面的stage
           'stage': 'CheckoutCodeStage'  //stage对应的类名
       ],
       'Code Scan': [
            'shortName': 'codeScan',
            'stage': 'ScanCodeStage'
       ],
       'Unit Test': [
           'shortName': 'ut',
           'stage': 'UnitTestStage'
       ],
       'Build': [
           'shortName': 'build',
           'stage': 'BuildStage'
       ],
       'Docker Push': [
           'shortName': 'docker',
           'stage': 'DockerPushStage'
       ],
       'Deployment': [
           'shortName': 'deploy',
           'stage': 'DeployStage'
       ],
       'Test Automation': [
           'shortName': 'at',
           'stage': 'AutoTestStage'
       ],
       'Performance Test': [
           'shortName': 'pt',
           'stage': 'PerTestStage'
       ]
   ])

    def MULTI_ENV = [
        "sit": "http://juno.intra.sit.com.com",
        "test": "http://juno.intra.test.com.com",
        "uat": "http://juno.intra.uat.com.com",
        "prod": "http://juno.intra.com.com"
    ]

    def STATUS_URL = '/api/wfc2/v1/delivery_status'
    def CODESCAN_URL = '/api/wfc2/v1/sonar/scan'
    def DEPLOY_URL = '/api/wfc2/v1/deploy'
    def DEPLOY_LOG_URL = '/api/wfc2/v1/deploy/log?serviceId='
    def TEST_URL = '/api/wfc2/v1/starttest'

    @NonCPS
    def mapToList(depmap) {
        def dlist = []
        for (def entry2 in depmap) {
            dlist.add(new java.util.AbstractMap.SimpleImmutableEntry(entry2.key, entry2.value))
        }
        return dlist
}

}
