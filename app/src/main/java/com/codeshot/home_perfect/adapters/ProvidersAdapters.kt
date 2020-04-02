package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.ItemProviderBinding
import com.codeshot.home_perfect.models.Provider
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot

class ProvidersAdapters(options:FirestoreRecyclerOptions<Provider>):
    FirestoreRecyclerAdapter<Provider, ProvidersAdapters.ProviderItem>(options) {
    private lateinit var itemProviderBinding: ItemProviderBinding

    private var providerList:List<Provider>?=ArrayList<Provider>()
    private var onItemClickLinstener: OnItemClickLinstener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderItem {
        val inflater=LayoutInflater.from(parent.context)
        itemProviderBinding=ItemProviderBinding.inflate(inflater,parent,false)
        return ProviderItem(itemProviderBinding)
    }

    override fun onBindViewHolder(holder: ProviderItem, position: Int, model: Provider) {
        holder.bindItem(model)
    }
    fun setList(providersList:List<Provider>){
        this.providerList=providersList
        notifyDataSetChanged()
    }

    interface OnItemClickLinstener{
        fun onItemClicked(providerId:String)
    }
    fun setOnCLickLinstener(onItemClickLinstener: OnItemClickLinstener){
        this.onItemClickLinstener=onItemClickLinstener
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
                if (adapterPosition!=RecyclerView.NO_POSITION && onItemClickLinstener!=null){
                    val providerId=snapshots.getSnapshot(adapterPosition).id
                    onItemClickLinstener!!.onItemClicked(providerId=providerId)

                }
            }
        }
    }
}