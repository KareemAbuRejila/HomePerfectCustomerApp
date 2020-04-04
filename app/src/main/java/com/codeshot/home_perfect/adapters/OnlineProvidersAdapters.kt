package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.ItemRecentProvidersBinding
import com.codeshot.home_perfect.models.Provider
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class OnlineProvidersAdapters(options: FirestoreRecyclerOptions<Provider>) :
    FirestoreRecyclerAdapter<Provider, OnlineProvidersAdapters.RecentProviderItem>(options) {
    private lateinit var itemRecentProvidersBinding: ItemRecentProvidersBinding
    private var onOnlineProviderListener: OnOnlineProviderListener? = null


    private var providerList: List<Provider>? = ArrayList()

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

    interface OnOnlineProviderListener {
        fun onItemClicked(providerId: String)
    }

    fun setOnOnlineProviderCLickListener(onOnlineProviderListener: OnOnlineProviderListener) {
        this.onOnlineProviderListener = onOnlineProviderListener
    }

    inner class RecentProviderItem(private val itemRecentProvidersBinding: ItemRecentProvidersBinding) :
        RecyclerView.ViewHolder(itemRecentProvidersBinding.root) {

        fun bindItem(provider: Provider) {
            itemRecentProvidersBinding.provider = provider
            itemRecentProvidersBinding.executePendingBindings()

            itemRecentProvidersBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && onOnlineProviderListener != null) {
                    val providerId = snapshots.getSnapshot(adapterPosition).id
                    onOnlineProviderListener!!.onItemClicked(providerId = providerId)
                }

            }
        }
    }
}