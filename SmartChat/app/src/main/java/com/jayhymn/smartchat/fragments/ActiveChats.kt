package com.jayhymn.smartchat.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jayhymn.smartchat.ActiveContacts

import com.jayhymn.smartchat.R
import com.jayhymn.smartchat.appObjects.ContactChats
import com.jayhymn.smartchat.adapters.CAdapter

class ActiveChats : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var chats: ArrayList<ContactChats> = ArrayList()
    private lateinit var floatBar: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState !== null){
            onViewStateRestored(savedInstanceState)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        chats.add(ContactChats("", "Tunji", "How you dey"))
        chats.add(
            ContactChats("",
                "Matthew",
                "the last time I saw you was in picnic..."
            )
        )
        chats.add(
            ContactChats("",
                "Kunle Fashola",
                "our meeting is by 2pm today. Make sure you attend"
            )
        )

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_chats, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.activeChatList)

        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        adapter = CAdapter(chats, activity!!.applicationContext)
        recyclerView.adapter = adapter

        floatBar = view.findViewById(R.id.fab)
        floatBar.setOnClickListener{
            val intent = Intent(context, ActiveContacts::class.java)
            startActivity(intent)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("contactedUser", chats)
    }

}
