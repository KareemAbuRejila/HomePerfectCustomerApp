package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.ItemProviderBinding
import com.codeshot.home_perfect.databinding.ItemWishListProviderBinding
import com.codeshot.home_perfect.models.Provider
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ProvidersAdapters(options:FirestoreRecyclerOptions<Provider>):
    FirestoreRecyclerAdapter<Provider, RecyclerView.ViewHolder>(options) {
    private var viewtype: Int = 0
    val PROVIDERS_TYPE = 0
    val WISHLIST_TYPE = 1

    private var providerList: List<Provider>? = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        return when (viewtype) {
            0 -> {
                val itemProviderBinding = ItemProviderBinding.inflate(inflater, parent, false)
                ProviderItem(itemProviderBinding)
            }
            else -> {
                val itemWishListBinding =
                    ItemWishListProviderBinding.inflate(inflater, parent, false)
                WishListItem(itemWishListBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Provider) {
        when (viewtype) {
            0 -> (holder as ProviderItem).bindItem(model)
            1 -> (holder as WishListItem).bindItem(model)
        }
    }

    fun setViewType(viewType: Int) {
        this.viewtype = viewType
        notifyDataSetChanged()
    }

    fun setList(providersList:List<Provider>){
        this.providerList=providersList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClicked(providerId:String)
    }

    fun setOnCLickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    inner class ProviderItem (private val itemProviderBinding: ItemProviderBinding) : RecyclerView.ViewHolder(itemProviderBinding.root) {

        fun bindItem(provider: Provider){
            itemProviderBinding.provider=provider
            itemProviderBinding.executePendingBindings()
            if(provider.online){
                itemProviderBinding.imgStatus.setImageResource(android.R.color.holo_green_light)
            }else{
                itemProviderBinding.imgStatus.setImageResource(android.R.color.holo_red_light)
            }

            itemProviderBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    val providerId=snapshots.getSnapshot(adapterPosition).id
                    onItemClickListener!!.onItemClicked(providerId = providerId)

                }
            }
        }
    }

    inner class WishListItem(private val itemWishListProviderBinding: ItemWishListProviderBinding) :
        RecyclerView.ViewHolder(itemWishListProviderBinding.root) {

        fun bindItem(provider: Provider) {
            itemWishListProviderBinding.provider = provider
            itemWishListProviderBinding.executePendingBindings()
            itemWishListProviderBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    val providerId = snapshots.getSnapshot(adapterPosition).id
                    onItemClickListener!!.onItemClicked(providerId = providerId)
                }
            }
            if (adapterPosition == providerList!!.size - 1) {
                itemWishListProviderBinding.view.visibility = View.INVISIBLE
            }
        }
    }


}