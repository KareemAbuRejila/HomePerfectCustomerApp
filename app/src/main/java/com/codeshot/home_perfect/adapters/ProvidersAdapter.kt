package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.*
import com.codeshot.home_perfect.interfaces.ItemProviderListener
import com.codeshot.home_perfect.interfaces.ItemRequestListener
import com.codeshot.home_perfect.models.Notification
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Request

class ProvidersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var providersList: List<Provider>? = ArrayList()
    private var requests: List<Request>? = ArrayList()
    private var notifications: List<Notification>? = ArrayList()
    private var itemProviderListener: ItemProviderListener? = null
    private var requestListener: ItemRequestListener? = null

    private var VIEW_TYPE: Int = 0
    val PROVIDER_TOP = 1
    val PROVIDER_ONLINE = 2
    val PROVIDER_SERVICE = 3
    val PROVIDER_WISHLIST = 4
    val REQUEST = 5
    val NOTIFICATION = 6

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (VIEW_TYPE) {
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
            NOTIFICATION -> {
                val itemNotificationBinding =
                    ItemNotificationBinding.inflate(inflater, parent, false)
                ItemNotification(itemNotificationBinding)
            }
            else -> {
                val itemTopProviderBinding = ItemTopProviderBinding.inflate(inflater, parent, false)
                TopProviderItem(itemTopProviderBinding)
            }

        }
    }

    override fun getItemCount(): Int {
        return when {
            providersList!!.isNotEmpty() -> providersList!!.size
            notifications!!.isNotEmpty() -> notifications!!.size
            else -> requests!!.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (VIEW_TYPE) {
            PROVIDER_TOP -> (holder as TopProviderItem).bindItem(providersList!![position])
            PROVIDER_ONLINE -> (holder as OnlineProviderItem).bindItem(providersList!![position])
            PROVIDER_SERVICE -> (holder as ProviderServiceItem).bindItem(providersList!![position])
            REQUEST -> (holder as ItemRequest).bindItem(requests!![position])
            NOTIFICATION -> (holder as ItemNotification).bindItem(notifications!![position])
            PROVIDER_WISHLIST -> (holder as WishListItem).bindItem(providersList!![position])
        }
    }

    fun setViewType(viewType: Int) {
        this.VIEW_TYPE = viewType
        notifyDataSetChanged()
    }

    fun setProviders(providers: List<Provider>) {
        this.providersList = providers
        notifyDataSetChanged()
    }

    fun setRequestsList(requests: List<Request>) {
        this.requests = requests
        notifyDataSetChanged()
    }

    fun setNotifications(notifications: List<Notification>) {
        this.notifications = notifications
        notifyDataSetChanged()
    }


    fun setOnCLickListener(itemProviderListener: ItemProviderListener) {
        this.itemProviderListener = itemProviderListener
    }

    fun setItemRequestListener(requestListener: ItemRequestListener) {
        this.requestListener = requestListener
    }


    inner class TopProviderItem(private val itemTopProviderBinding: ItemTopProviderBinding) :
        RecyclerView.ViewHolder(itemTopProviderBinding.root) {

        fun bindItem(provider: Provider) {
            itemTopProviderBinding.provider = provider
            itemTopProviderBinding.executePendingBindings()
            itemTopProviderBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && itemProviderListener != null) {
                    itemProviderListener!!.onProviderClicked(providerId = provider.id!!)
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
                if (adapterPosition != RecyclerView.NO_POSITION && itemProviderListener != null) {
                    itemProviderListener!!.onProviderClicked(providerId = provider.id!!)
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
                if (adapterPosition != RecyclerView.NO_POSITION && itemProviderListener != null) {
                    itemProviderListener!!.onProviderClicked(providerId = provider.id!!)

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
                if (adapterPosition != RecyclerView.NO_POSITION && itemProviderListener != null) {
                    itemProviderListener!!.onProviderClicked(providerId = provider.id!!)
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
                    requestListener!!.onRequestClicked(request = request)
                }
            }
            itemBookBinding.circleImageView2.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && requestListener != null) {
                    requestListener!!.onImageRequestClicked(request.to!!)
                }
            }
            if (adapterPosition == 0)
                itemBookBinding.lineUp.visibility = View.GONE

            if (adapterPosition == requests!!.size - 1)
                itemBookBinding.lineDown.visibility = View.GONE


        }

    }

    inner class ItemNotification(private val itemNotificationBinding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(itemNotificationBinding.root) {

        fun bindItem(notification: Notification) {
            itemNotificationBinding.noti = notification
            itemNotificationBinding.executePendingBindings()
        }

    }
}