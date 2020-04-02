package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.codeshot.home_perfect.databinding.ItemRecentProvidersBinding
import com.codeshot.home_perfect.models.Provider
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class RecentProvidersAdapters(options:FirestoreRecyclerOptions<Provider>):
    FirestoreRecyclerAdapter<Provider, RecentProviderItem>(options) {
    private lateinit var itemRecentProvidersBinding: ItemRecentProvidersBinding

    private var providerList:List<Provider>?=ArrayList<Provider>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentProviderItem {
        val inflater=LayoutInflater.from(parent.context)
        itemRecentProvidersBinding=ItemRecentProvidersBinding.inflate(inflater,parent,false)
        return RecentProviderItem(itemRecentProvidersBinding)
    }

    override fun onBindViewHolder(holder: RecentProviderItem, position: Int, model: Provider) {
        holder.bindItem(model)
    }

    fun setList(providersList:List<Provider>){
        this.providerList=providersList
        notifyDataSetChanged()
    }
}