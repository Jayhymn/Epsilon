package com.jayhymn.smartchat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jayhymn.smartchat.adapters.CAdapter
import com.jayhymn.smartchat.appObjects.ContactChats
import com.jayhymn.smartchat.utils.CountryToPhonePrefix
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.message_contacts.*

class ActiveContacts : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var layout: RecyclerView.LayoutManager
    private var mData: ArrayList<ContactChats> = ArrayList()
    private var data: ArrayList<ContactChats> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_contacts)
        getContactList()
        initialize()
    }

    private fun getContactList(){
        val isoPrefix = getPhoneISO()

        // get all contacts info from phone
        val mcursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,null,null,null)

        while (mcursor.moveToNext()) {
            val name = mcursor.getString(mcursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            var phone = mcursor.getString(mcursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            if (phone.toString() != null) {
                phone = phone.replace(Regex("[ ]|[-]|[(]|[)]"), "")

                if (phone[0].toString() != "+") {
                    phone = phone.replaceFirst("0".toRegex(), isoPrefix)
                }
                val obj = ContactChats(name, name, phone)
                mData.add(obj)
            checkContactList(obj)
            }
        }
        mcursor.close()
    }

    private fun checkContactList(mContact: ContactChats) {
        val mdatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("users")

        //find the contact on the database to see if he/she has once signed in to  WhatsChat
        val mQuery = mdatabase.orderByChild("phone").equalTo(mContact.phone)

        mQuery.addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        var name: String = ""; var key: String = ""
                        var phone = ""
                    for (mshot: DataSnapshot  in dataSnapshot.children){
                            if (mContact.name !== "") {
                                name = mContact.name
                            } else {
                                name = mshot.child("name").value.toString()
                            }
                        key = mshot.key!!
                        phone = mshot.child("phone").value.toString()
                        data.add(ContactChats(key, name, phone))
                        adapter.notifyDataSetChanged()

                        return
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
        )
    }

    private fun getPhoneISO(): String {
        var iso: String? = null
        val getISO = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (getISO.networkCountryIso != null){
            if (getISO.getNetworkCountryIso().toString() != ""){
                iso = getISO.getNetworkCountryIso().toString()
            }
        }
        return CountryToPhonePrefix.getPhone(iso!!)

    }
    private fun initialize(){
        recycler = findViewById(R.id.recycler)
        recycler.setNestedScrollingEnabled(false)
        recycler.setHasFixedSize(false)

        layout = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        adapter = CAdapter(data, applicationContext)

        recycler.layoutManager = layout
        recycler.adapter = adapter
        return
    }

}
