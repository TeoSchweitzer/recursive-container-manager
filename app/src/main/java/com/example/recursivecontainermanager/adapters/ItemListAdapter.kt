package com.example.recursivecontainermanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recursivecontainermanager.R
import com.example.recursivecontainermanager.data.entities.Item
import com.example.recursivecontainermanager.databinding.ItemInListViewBinding


class ItemListAdapter(private val itemList: List<Item>,
                      private val touchItem: (Item) -> Unit,
                      private val withPosition: Boolean):
    ListAdapter<Item, ItemListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            parent.context,
            ItemInListViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position], withPosition, position==itemList.lastIndex)
        holder.itemView.setOnClickListener {
            touchItem(itemList[position])
        }
    }

    class ItemViewHolder(var context: Context, var binding: ItemInListViewBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item, withPosition: Boolean, isLast: Boolean) {
            binding.itemNameText.text = item.name
            if (withPosition && !isLast) {
                binding.positionDescriptionText.visibility = View.VISIBLE
                if (item.position == "")
                     binding.positionDescriptionText.text = context.getString(R.string.default_position)
                else binding.positionDescriptionText.text = item.position
            }
            else binding.positionDescriptionText.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int { return itemList.size }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.location == newItem.location
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}