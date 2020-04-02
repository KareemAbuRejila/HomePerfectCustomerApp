package com.codeshot.home_perfect.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.Common.Common
import com.codeshot.home_perfect.databinding.ItemTopProviderBinding
import com.codeshot.home_perfect.models.Provider
import com.firebase.client.Firebase
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class TopProvidersAdapters(options: FirestoreRecyclerOptions<Provider>) :
    FirestoreRecyclerAdapter<Provider, TopProvidersAdapters.ProviderItem>(options) {
    private lateinit var itemTopProviderBinding: ItemTopProviderBinding

    private var providerList: List<Provider>? = ArrayList<Provider>()
    private var onItemClickLinstener: OnItemTopProviderLinstener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderItem {
        val inflater = LayoutInflater.from(parent.context)
        itemTopProviderBinding = ItemTopProviderBinding.inflate(inflater, parent, false)
        return ProviderItem(itemTopProviderBinding)
    }

    override fun onBindViewHolder(holder: ProviderItem, position: Int, model: Provider) {

            holder.bindItem(model)
    }

    fun setList(providersList: List<Provider>) {
        this.providerList = providersList
        notifyDataSetChanged()
    }

    interface OnItemTopProviderLinstener {
        fun onItemClicked(providerId:String)
    }

    fun setOnCLickLinstener(onItemClickLinstener: OnItemTopProviderLinstener) {
        this.onItemClickLinstener = onItemClickLinstener
    }


    inner class ProviderItem(private val itemTopProviderBinding: ItemTopProviderBinding) :
        RecyclerView.ViewHolder(itemTopProviderBinding.root) {

        fun bindItem(provider: Provider) {
            itemTopProviderBinding.provider = provider
            itemTopProviderBinding.executePendingBindings()
            if (provider.online) {
                itemTopProviderBinding.imgStatus.setImageResource(android.R.color.holo_green_light)
            } else {
                itemTopProviderBinding.imgStatus.setImageResource(android.R.color.holo_red_light)
            }

            itemTopProviderBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && onItemClickLinstener != null){
                    val providerId=snapshots.getSnapshot(adapterPosition).id
                    onItemClickLinstener!!.onItemClicked(providerId = providerId)
                }

            }
        }
    }
}