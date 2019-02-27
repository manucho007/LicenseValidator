package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url '/api/check-access'
        body("""{"data": "App1","children": [{"data": "Scripts","children": [{"data": "sc1"}]}]}""")
        headers {
            header('Content-Type', 'application/json')
        }
    }
    response {
        status 200
        body(
            response : value(producer(anyBoolean())),
            timestamp: value(producer(number())),
            hash: value(producer(regex('.*')))
        )
        headers {
            contentType(applicationJson())
        }
    }
}