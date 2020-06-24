package com.jayhymn.smartchat.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jayhymn.smartchat.R
import com.jayhymn.smartchat.appObjects.ContactChats
import com.jayhymn.smartchat.PrivateChat
import kotlin.collections.ArrayList

class CAdapter(private val myDataset: ArrayList<ContactChats>, context: Context): RecyclerView.Adapter<CAdapter.CViewHolder>() {
    private val context: Context = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_contacts, parent, false) as View
        return CViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(holder: CViewHolder, position: Int) {
        val user: ContactChats = myDataset[position]
        holder.contactName.text = user.name
        holder.lastMsg.text = user.phone

        holder.contactName.setOnClickListener{
                val intent = Intent(context, PrivateChat::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
    }

    class CViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById<TextView>(R.id.name)!!
        val lastMsg: TextView  = itemView.findViewById<TextView>(R.id.last_msg) as TextView
        val profilePic = itemView.findViewById(R.id.profile_pic) as ImageView
    }
}

