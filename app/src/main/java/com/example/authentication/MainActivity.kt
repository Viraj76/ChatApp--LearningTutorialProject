package com.example.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import  androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authentication.auth.SingInActivity

import com.example.authentication.databinding.ActivityMainBinding
import com.example.authentication.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var mainToolBar:Toolbar
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var userList : ArrayList<Users>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userList = ArrayList()
        firebaseAuth = FirebaseAuth.getInstance()
        settingUpToolBar()
        loggingOut()
        prepareRvForChatAdapter()
        showingAllUsers()


    }

    private fun showingAllUsers() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(users in snapshot.children){
                    val user = users.getValue(Users::class.java)
                    val currentUSerId = firebaseAuth.currentUser?.uid
                    if(currentUSerId != user?.userId)
                        userList.add(user!!)
                }
                chatAdapter.setUserList(userList)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun prepareRvForChatAdapter() {
        chatAdapter = ChatAdapter()
        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter = chatAdapter
        }
    }

    private fun loggingOut() {
        binding.ivLogout.setOnClickListener { val builder = AlertDialog.Builder(this)
            val alertDialog = builder.create()
            builder
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes"){dialogInterface,which->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this,SingInActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("No"){dialogInterface, which->
                    alertDialog.dismiss()
                }
                .show()
                .setCancelable(false) }

    }

    private fun settingUpToolBar() {
        mainToolBar = findViewById(R.id.tbCustom)
        mainToolBar.title = "ChitChat"
        mainToolBar.setTitleTextColor(resources.getColor(R.color.white))
        setSupportActionBar(mainToolBar)
    }
}