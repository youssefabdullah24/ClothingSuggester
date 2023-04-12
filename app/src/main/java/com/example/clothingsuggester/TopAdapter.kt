package com.example.clothingsuggester

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingsuggester.databinding.ItemTopBinding

class TopAdapter : ListAdapter<Top, TopAdapter.ViewHolder>(TopDiffUtils()) {
    class ViewHolder(private val binding: ItemTopBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(ItemTopBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

        fun bind(top: Top) {
            binding.topImageView.setImageResource(top.topResId)
            if (top.isSelected) {
                binding.cardView.strokeWidth = 8
                binding.cardView.strokeColor = Color.BLACK
            } else {
                binding.cardView.strokeWidth = 0
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class TopDiffUtils : DiffUtil.ItemCallback<Top>() {
        override fun areItemsTheSame(oldItem: Top, newItem: Top): Boolean {
            return oldItem.topResId == newItem.topResId
        }

        override fun areContentsTheSame(oldItem: Top, newItem: Top): Boolean {
            return oldItem == newItem
        }
    }

}