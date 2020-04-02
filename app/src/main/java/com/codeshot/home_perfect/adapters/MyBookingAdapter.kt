package com.codeshot.home_perfect.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.ItemBookBinding
import com.codeshot.home_perfect.models.Request

class MyBookingAdapter : RecyclerView.Adapter<MyBookingAdapter.ItemBook>() {
    private var requestsArray:List<Request>?=ArrayList<Request>()
    private lateinit var requestListener: ItemRequestListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBook {
        val inflater=LayoutInflater.from(parent.context)
        val itemBinding=ItemBookBinding.inflate(inflater,parent,false)
        return ItemBook(itemBookBinding = itemBinding)
    }

    override fun getItemCount(): Int {
        return requestsArray!!.size
    }

    override fun onBindViewHolder(holder: ItemBook, position: Int) {
        val request=requestsArray!![position]
        holder.bind(request)
    }
    fun setList(requestsArray:List<Request>){
        this.requestsArray=requestsArray
        notifyDataSetChanged()
    }
    fun setItemRequestListener(requestListener: ItemRequestListener){
        this.requestListener=requestListener
    }

    inner class ItemBook(val itemBookBinding: ItemBookBinding):
        RecyclerView.ViewHolder(itemBookBinding.root) {
        fun bind(request:Request){
            itemBookBinding.request=request
            itemBookBinding.executePendingBindings()
            itemBookBinding.root.setOnClickListener {
                if (adapterPosition!=RecyclerView.NO_POSITION && requestListener!=null){
                    requestListener.OnItemClicked(request = request)
                }
            }
            itemBookBinding.circleImageView2.setOnClickListener {
                if (adapterPosition!=RecyclerView.NO_POSITION && requestListener!=null){
                    requestListener.OnImageClicked(request.to!!)
                }
            }

        }

    }

    interface ItemRequestListener{
        fun OnItemClicked(request: Request)
        fun OnImageClicked(providerId:String)
    }
}