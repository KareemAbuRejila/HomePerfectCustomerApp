package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.*
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Request

class ProvidersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var providersList: List<Provider>? = ArrayList()
    private var requests: List<Request>? = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var requestListener: ItemRequestListener? = null

    private var VIEW__TYPE: Int = 0
    val PROVIDER_TOP = 1
    val PROVIDER_ONLINE = 2
    val PROVIDER_SERVICE = 3
    val PROVIDER_WISHLIST = 4
    val REQUEST = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (VIEW__TYPE) {
            PROVIDER_SERVICE -> {
                val itemProviderBinding = ItemProviderBinding.inflate(inflater, parent, false)
                ProviderServiceItem(itemProviderBinding)
            }
            PROVIDER_ONLINE -> {
                val itemRecentProvidersBinding =
                    ItemRecentProvidersBinding.inflate(inflater, parent, false)
                OnlineProviderItem(itemRecentProvidersBinding)
            }
            PROVIDER_WISHLIST -> {
                val itemWishListProviderBinding =
                    ItemWishListProviderBinding.inflate(inflater, parent, false)
                WishListItem(itemWishListProviderBinding)
            }
            REQUEST -> {
                val itemBookBinding = ItemBookBinding.inflate(inflater, parent, false)
                ItemRequest(itemBookBinding)
            }
            else -> {
                val itemTopProviderBinding = ItemTopProviderBinding.inflate(inflater, parent, false)
                TopProviderItem(itemTopProviderBinding)
            }

        }
    }

    override fun getItemCount(): Int {
        return if (providersList!!.isNotEmpty())
            providersList!!.size
        else requests!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (VIEW__TYPE) {
            PROVIDER_TOP -> (holder as TopProviderItem).bindItem(providersList!![position])
            PROVIDER_ONLINE -> (holder as OnlineProviderItem).bindItem(providersList!![position])
            PROVIDER_SERVICE -> (holder as ProviderServiceItem).bindItem(providersList!![position])
            REQUEST -> (holder as ItemRequest).bindItem(requests!![position])
            PROVIDER_WISHLIST -> (holder as WishListItem).bindItem(providersList!![position])
        }
    }

    fun setViewType(viewType: Int) {
        this.VIEW__TYPE = viewType
        notifyDataSetChanged()
    }

    fun setList(providers: List<Provider>) {
        this.providersList = providers
        notifyDataSetChanged()
    }

    fun setRequestsList(requests: List<Request>) {
        this.requests = requests
        notifyDataSetChanged()
    }


    fun setOnCLickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItemRequestListener(requestListener: ItemRequestListener) {
        this.requestListener = requestListener
    }


    interface OnItemClickListener {
        fun onItemClicked(providerId: String)
    }

    interface ItemRequestListener {
        fun OnItemClicked(request: Request)
        fun OnImageClicked(providerId: String)
    }


    inner class TopProviderItem(private val itemTopProviderBinding: ItemTopProviderBinding) :
        RecyclerView.ViewHolder(itemTopProviderBinding.root) {

        fun bindItem(provider: Provider) {
            itemTopProviderBinding.provider = provider
            itemTopProviderBinding.executePendingBindings()
            itemTopProviderBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener!!.onItemClicked(providerId = provider.id!!)
                }

            }
        }
    }

    inner class OnlineProviderItem(private val itemRecentProvidersBinding: ItemRecentProvidersBinding) :
        RecyclerView.ViewHolder(itemRecentProvidersBinding.root) {

        fun bindItem(provider: Provider) {
            itemRecentProvidersBinding.provider = provider
            itemRecentProvidersBinding.executePendingBindings()

            itemRecentProvidersBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener!!.onItemClicked(providerId = provider.id!!)
                }

            }
        }
    }

    inner class ProviderServiceItem(private val itemProviderBinding: ItemProviderBinding) :
        RecyclerView.ViewHolder(itemProviderBinding.root) {

        fun bindItem(provider: Provider) {
            itemProviderBinding.provider = provider
            itemProviderBinding.executePendingBindings()
            if (provider.online) {
                itemProviderBinding.imgStatus.setImageResource(android.R.color.holo_green_light)
            } else {
                itemProviderBinding.imgStatus.setImageResource(android.R.color.holo_red_light)
            }
            itemProviderBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener!!.onItemClicked(providerId = provider.id!!)

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
                    onItemClickListener!!.onItemClicked(providerId = provider.id!!)
                }
            }
            if (adapterPosition == providersList!!.size - 1) {
                itemWishListProviderBinding.view.visibility = View.INVISIBLE
            }
        }
    }

    inner class ItemRequest(private val itemBookBinding: ItemBookBinding) :
        RecyclerView.ViewHolder(itemBookBinding.root) {
        fun bindItem(request: Request) {
            itemBookBinding.request = request
            itemBookBinding.executePendingBindings()
            itemBookBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && requestListener != null) {
                    requestListener!!.OnItemClicked(request = request)
                }
            }
            itemBookBinding.circleImageView2.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && requestListener != null) {
                    requestListener!!.OnImageClicked(request.to!!)
                }
            }
            if (adapterPosition == 0)
                itemBookBinding.lineUp.visibility = View.GONE

            if (adapterPosition == requests!!.size - 1)
                itemBookBinding.lineDown.visibility = View.GONE


        }

    }
}