package com.example.mybirthdaytracker.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybirthdaytracker.R
import com.example.mybirthdaytracker.data.BirthdayEntity
import com.example.mybirthdaytracker.databinding.ItemBirthdayBinding
import com.example.mybirthdaytracker.utils.DateUtils

class BirthdayAdapter(private val context: Context) : RecyclerView.Adapter<BirthdayAdapter.BirthdayViewHolder>() {

    private var birthdays = emptyList<BirthdayEntity>()

    inner class BirthdayViewHolder(private val binding: ItemBirthdayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(birthday: BirthdayEntity) {
            binding.textName.text = birthday.name
            
            val age = DateUtils.getAge(birthday.dob)
            binding.textAge.text = "Turns ${age + 1}"
            
            val date = DateUtils.getBirthDate(birthday.dob)
            val month = DateUtils.getBirthMonth(birthday.dob)
            val day = DateUtils.getDay(birthday.dob)
            binding.textDate.text = "$month $date, $day"
            
            val daysLeft = DateUtils.getDaysLeft(birthday.dob)
            binding.textDaysLeftCount.text = daysLeft.toString()

            // Handle image loading
            if (birthday.image.startsWith("content://")) {
                Glide.with(context).load(Uri.parse(birthday.image)).into(binding.profileImage)
            } else {
                // It's a local drawable
                val imageName = birthday.image.substringBeforeLast(".")
                val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
                if (resId != 0) {
                    Glide.with(context).load(resId).into(binding.profileImage)
                } else {
                    Glide.with(context).load(R.mipmap.ic_launcher).into(binding.profileImage)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        val binding = ItemBirthdayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BirthdayViewHolder(binding)
    }

    override fun getItemCount() = birthdays.size

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        holder.bind(birthdays[position])
    }

    fun submitList(newList: List<BirthdayEntity>) {
        birthdays = newList
        notifyDataSetChanged()
    }
}
