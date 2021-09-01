package com.codeshot.home_perfect.interfaces

import com.codeshot.home_perfect.models.Request

interface ItemRequestListener {
    fun onRequestClicked(request: Request)
    fun onImageRequestClicked(providerId: String)
}