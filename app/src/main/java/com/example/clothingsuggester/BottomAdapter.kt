package com.example.clothingsuggester

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.clothingsuggester.databinding.ItemBottomBinding

class BottomAdapter : ListAdapter<Bottom, BottomAdapter.ViewHolder>(BottomDiffUtils()) {
    class ViewHolder(private val binding: ItemBottomBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(ItemBottomBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

        fun bind(bottom: Bottom) {
            binding.bottomImageView.setImageResource(bottom.bottomResId)
            if (bottom.isSelected) {
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


    class BottomDiffUtils : DiffUtil.ItemCallback<Bottom>() {
        override fun areItemsTheSame(oldItem: Bottom, newItem: Bottom): Boolean {
            return oldItem.bottomResId == newItem.bottomResId
        }

        override fun areContentsTheSame(oldItem: Bottom, newItem: Bottom): Boolean {
            return oldItem == newItem
        }
    }

}