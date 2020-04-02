package com.codeshot.home_perfect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codeshot.home_perfect.databinding.ItemAdditionBinding
import com.codeshot.home_perfect.models.Addition

class AdditionsAdapter
    : RecyclerView.Adapter<AdditionsAdapter.AdditionItem>() {

    private lateinit var itemAdditionBinding: ItemAdditionBinding
    private lateinit var onAdditionClicked: OnAdditionClicked

    private var additions=ArrayList<Addition>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdditionItem {
        val inflater=LayoutInflater.from(parent.context)
        itemAdditionBinding=ItemAdditionBinding.inflate(inflater,parent,false)
        return AdditionItem(itemAdditionBinding)
    }

    override fun getItemCount(): Int {
        return additions.size
    }
    fun addAddition(addition:Addition){
        additions.add(addition)
        notifyDataSetChanged()
    }
    fun setList(additions:ArrayList<Addition>){
        this.additions=additions
        notifyDataSetChanged()
    }
    fun setOnClickedListener(onAdditionClicked: OnAdditionClicked){
        this.onAdditionClicked=onAdditionClicked
    }

    override fun onBindViewHolder(holder: AdditionItem, position: Int) {
        holder.bindItem(addition = additions[position])
    }

    inner class AdditionItem(val itemAdditionBinding: ItemAdditionBinding):
        RecyclerView.ViewHolder(itemAdditionBinding.root) {

        fun bindItem(addition: Addition){
            itemAdditionBinding.addition=addition
            itemAdditionBinding.executePendingBindings()

            itemAdditionBinding.root.setOnClickListener {
                if (onAdditionClicked!=null&&adapterPosition!=RecyclerView.NO_POSITION){
                    if (!itemAdditionBinding.itemLayout.isSelected){
                        onAdditionClicked.onItemClicked(addition = additions[adapterPosition],position = adapterPosition,type = "add")
                        itemAdditionBinding.itemLayout.isSelected=true
                        itemAdditionBinding.icCheck.visibility= View.VISIBLE
                    }else{
                        onAdditionClicked.onItemClicked(addition = additions[adapterPosition],position = adapterPosition,type = "remove")
                        itemAdditionBinding.itemLayout.isSelected=false
                        itemAdditionBinding.icCheck.visibility= View.INVISIBLE

                    }

                }
            }


        }

    }
    interface OnAdditionClicked{
        fun onItemClicked(addition:Addition,position:Int,type:String)
    }
}