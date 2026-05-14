package com.example.mybirthdaytracker.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybirthdaytracker.R
import com.example.mybirthdaytracker.data.BirthdayEntity
import com.example.mybirthdaytracker.databinding.ItemRecentBinding
import com.example.mybirthdaytracker.utils.DateUtils

class RecentAdapter(private val context: Context) : RecyclerView.Adapter<RecentAdapter.RecentViewHolder>() {

    private var birthdays = emptyList<BirthdayEntity>()

    inner class RecentViewHolder(private val binding: ItemRecentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(birthday: BirthdayEntity) {
            binding.textNameRecent.text = birthday.name
            
            val daysAgo = DateUtils.getDaysAgo(birthday.dob)
            binding.textDaysAgo.text = "Missed $daysAgo days ago"

            // Handle image loading
            if (birthday.image.startsWith("content://")) {
                Glide.with(context).load(Uri.parse(birthday.image)).into(binding.profileImageRecent)
            } else {
                val imageName = birthday.image.substringBeforeLast(".")
                val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
                if (resId != 0) {
                    Glide.with(context).load(resId).into(binding.profileImageRecent)
                } else {
                    Glide.with(context).load(R.mipmap.ic_launcher).into(binding.profileImageRecent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = ItemRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount() = birthdays.size

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(birthdays[position])
    }

    fun submitList(newList: List<BirthdayEntity>) {
        birthdays = newList
        notifyDataSetChanged()
    }
}
