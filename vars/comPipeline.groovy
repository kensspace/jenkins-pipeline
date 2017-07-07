def call(Closure body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config

    body()
    params = config.configure

    node() {


        stage('代码检出') {
            if("${params.checkoutCode}" != "false"){
                checkout scm
            }else{
                echo "skip checkoutCode"
            }
        }

        stage('单元测试') {
            if("${params.unitTest}" == "true"){
                echo "starting unit test"
            }else{
                echo "skip unitTest"
            }
        }

        // stage('编译准备') {
        //     parallel (
        //         "代码扫描": {
        //             if("${params.codeScan}" == "true"){
        //                 echo "starting code scan"
        //             }else{
        //                 echo "skip codeScan"
        //             }
        //         },
        //         "单元测试": {
        //             if("${params.unitTest}" == "true"){
        //                 echo "starting unit test"
        //             }else{
        //                 echo "skip unitTest"
        //             }
        //         }
        //     )
        // }

        stage('编译打包') {
            if("${params.build.enable}" == "true"){
                sh "${params.build.script}"
            }else{
                echo "skip build"
            }
        }

        stage('应用容器化') {
            if("${params.docker}" == "true"){
                sh "sudo docker build -t 10.213.42.254:10500/spinnaker/spindemo:20170629 ."
            }else{
                echo "skip docker build"
            }
        }

        stage('容器推送') {
            if("${params.docker}" == "true"){
                sh "sudo docker push 10.213.42.254:10500/spinnaker/spindemo:20170629"
            }else{
                echo "skip docker build"
            }
        }
    }
}
