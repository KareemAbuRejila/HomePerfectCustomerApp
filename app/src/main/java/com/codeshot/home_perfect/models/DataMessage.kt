package com.codeshot.home_perfect.models

class DataMessage {
    var to: String? = null
    var data: Map<String, String>? = null

    constructor() {}
    constructor(to: String?, data: Map<String, String>?) {
        this.to = to
        this.data = data
    }

}