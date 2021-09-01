package com.codeshot.home_perfect.interfaces

import com.codeshot.home_perfect.models.Service

interface ItemServiceListener {
    fun onItemClicked(service: Service)

}