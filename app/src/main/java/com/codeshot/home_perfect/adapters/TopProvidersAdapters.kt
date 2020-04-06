package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.ItemTopProviderBinding
import com.codeshot.home_perfect.models.Provider
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class TopProvidersAdapters(val options: FirestoreRecyclerOptions<Provider>) :
    FirestoreRecyclerAdapter<Provider, TopProvidersAdapters.ProviderItem>(options) {
    private lateinit var itemTopProviderBinding: ItemTopProviderBinding

    private var providerList: List<Provider>? = ArrayList<Provider>()
    private var onItemClickListener: OnItemTopProviderListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderItem {
        val inflater = LayoutInflater.from(parent.context)
        itemTopProviderBinding = ItemTopProviderBinding.inflate(inflater, parent, false)
        return ProviderItem(itemTopProviderBinding)
    }

    override fun onBindViewHolder(holder: ProviderItem, position: Int, model: Provider) {
        model.online = false

        holder.bindItem(model)

    }

    fun setList(providersList: List<Provider>) {
        this.providerList = providersList
        notifyDataSetChanged()
    }

    interface OnItemTopProviderListener {
        fun onItemClicked(providerId:String)
    }

    fun setOnCLickListener(onItemClickListener: OnItemTopProviderListener) {
        this.onItemClickListener = onItemClickListener
    }


    inner class ProviderItem(private val itemTopProviderBinding: ItemTopProviderBinding) :
        RecyclerView.ViewHolder(itemTopProviderBinding.root) {

        fun bindItem(provider: Provider) {
            itemTopProviderBinding.provider = provider
            itemTopProviderBinding.executePendingBindings()
            itemTopProviderBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    val providerId=snapshots.getSnapshot(adapterPosition).id
                    onItemClickListener!!.onItemClicked(providerId = providerId)
                }

            }
        }
    }
}