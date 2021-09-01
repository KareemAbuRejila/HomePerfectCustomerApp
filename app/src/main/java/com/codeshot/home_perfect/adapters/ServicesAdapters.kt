package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.ItemServiceBinding
import com.codeshot.home_perfect.interfaces.ItemServiceListener
import com.codeshot.home_perfect.models.Service
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ServicesAdapters(options:FirestoreRecyclerOptions<Service>):
    FirestoreRecyclerAdapter<Service, ServicesAdapters.ServiceItem>(options) {
    private lateinit var itemServiceBinding: ItemServiceBinding
    private var itemServiceListener: ItemServiceListener? = null

    private var serviceList:List<Service>?=ArrayList<Service>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceItem {
        val inflater=LayoutInflater.from(parent.context)
        itemServiceBinding=ItemServiceBinding.inflate(inflater,parent,false)
        return ServiceItem(itemServiceBinding)
    }

    override fun onBindViewHolder(holder: ServiceItem, position: Int, model: Service) {
        holder.bindItem(model)
    }

    fun setList(services:List<Service>){
        this.serviceList=services
        notifyDataSetChanged()
    }


    inner class ServiceItem (private val itemServiceBinding: ItemServiceBinding) : RecyclerView.ViewHolder(itemServiceBinding.root) {

        fun bindItem(service: Service){
            itemServiceBinding.service = service
            itemServiceBinding.executePendingBindings()

            itemServiceBinding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && itemServiceListener != null)
                    itemServiceListener!!.onItemClicked(
                        snapshots.getSnapshot(adapterPosition).toObject(Service::class.java)!!
                    )
            }
        }
    }

    fun setOnClickListener(itemServiceListener: ItemServiceListener) {
        this.itemServiceListener = itemServiceListener
    }
}