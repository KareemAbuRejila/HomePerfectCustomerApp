package com.codeshot.home_perfect.models

class FCMResponse {
    var multicast_id: Long = 0
    var success = 0
    var failure = 0
    var canonical_ids = 0
    var results: List<Result>? = null

    constructor()
    constructor(
        multicast_id: Long,
        success: Int,
        failure: Int,
        canonical_ids: Int,
        results: List<Result>?
    ) {
        this.multicast_id = multicast_id
        this.success = success
        this.failure = failure
        this.canonical_ids = canonical_ids
        this.results = results
    }

}