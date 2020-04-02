package com.codeshot.home_perfect.adapters

import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.ItemProviderBinding
import com.codeshot.home_perfect.databinding.ItemRecentProvidersBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.User

class RecentProviderItem (private val itemRecentProvidersBinding: ItemRecentProvidersBinding) : RecyclerView.ViewHolder(itemRecentProvidersBinding.root) {

    fun bindItem(provider: Provider){
        itemRecentProvidersBinding.provider=provider
        itemRecentProvidersBinding.executePendingBindings()
    }
}