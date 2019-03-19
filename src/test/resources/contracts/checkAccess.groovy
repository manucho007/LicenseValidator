package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        url '/api/check-access/App1/Scripts/sc1'
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