package com.example.todonotesapp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.PeriodicSync
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.todonotesapp.NotesApp
import com.example.todonotesapp.utils.AppConstant
import com.example.todonotesapp.utils.PrefConstant
import com.example.todonotesapp.R
import com.example.todonotesapp.adapter.NotesAdapter
import com.example.todonotesapp.clickListeners.ItemClickListener
import com.example.todonotesapp.db.Notes
import com.example.todonotesapp.utils.StoreSession
import com.example.todonotesapp.workmanager.MyWorker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.TimeUnit

class MyNotesActivity : AppCompatActivity() {
    var fullName: String = ""
    lateinit var fabAddNotes: FloatingActionButton
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerView: RecyclerView
    var notesList = ArrayList<Notes>()
    val TAG = "MyNotesActivity"
    val ADD_NOTES_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_notes)
        setupSharedPreferences()
        bindViews()
        getIntentData()
        getDataFromDatabase()
        setupActionBar()
        clickListeners()
        setupRecyclerView()
        setupWorkManager()
    }


    private fun getIntentData() {
        val intent = intent
        if(intent.hasExtra(AppConstant.FULL_NAME)){
            fullName = intent.getStringExtra(AppConstant.FULL_NAME)
        }

        if(fullName.isNullOrEmpty()){
            fullName = StoreSession.readString(PrefConstant.FULL_NAME)!!
        }
    }

    private fun setupWorkManager() {
        val constraint = androidx.work.Constraints.Builder()
                .build()
        val request =PeriodicWorkRequest
                .Builder(MyWorker::class.java,1,TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build()
        WorkManager.getInstance().enqueue(request)
        // a -> b -> c
        //WorkManager.getInstance().beginWith(request).then().enqueue()
    }

    private fun getDataFromDatabase() {
        val notesApp = applicationContext as NotesApp
        val notesDao = notesApp.getNotesDb().notesDao()
        Log.d(TAG,notesDao.getAll().size.toString())
        notesList.addAll(notesDao.getAll())
    }

    private fun setupSharedPreferences() {
        //sharedPreferences = getSharedPreferences(PrefConstant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        StoreSession.init(this)
    }

    private fun bindViews() {
        fabAddNotes = findViewById(R.id.fabAddNotes)
        recyclerView = findViewById(R.id.recyclerViewNotes)
    }

    private fun clickListeners() {
        fabAddNotes.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //setupDialogBox()
                val intent = Intent(this@MyNotesActivity,AddNotesActivity::class.java)
                startActivityForResult(intent,ADD_NOTES_CODE)
            }

        })
    }

    private fun setupActionBar() {
        if(supportActionBar!=null) {
            supportActionBar?.title = "Howdy, "+fullName
        }
    }

    private fun setupRecyclerView() {
        val itemClickListener = object : ItemClickListener  {
            override fun onClick(notes: Notes) {
                val intent = Intent(this@MyNotesActivity, DetailActivity::class.java)
                intent.putExtra(AppConstant.TITLE,notes.title)
                intent.putExtra(AppConstant.DESCRIPTION,notes.description)
                startActivity(intent)
            }

            override fun onUpdate(notes: Notes) {
                Log.d(TAG,notes.isTaskCompleted.toString())
                val notesApp = applicationContext as NotesApp
                val notesDao = notesApp.getNotesDb().notesDao()
                notesDao.updateNotes(notes)
            }
        }
        val notesAdapter =NotesAdapter(notesList,itemClickListener)
        val linearLayoutManager = LinearLayoutManager(this@MyNotesActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = notesAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_NOTES_CODE && resultCode == Activity.RESULT_OK){
            val title = data?.getStringExtra(AppConstant.TITLE)
            val description = data?.getStringExtra(AppConstant.DESCRIPTION)
            val imagePath = data?.getStringExtra(AppConstant.IMAGE_PATH)

            val notes = Notes(title= title!!,description = description!!,imagePath = imagePath!!,isTaskCompleted = false)
            addNotesToDb(notes)
            notesList.add(notes)
            recyclerView.adapter?.notifyItemChanged(notesList.size-1)
        }
    }

    private fun addNotesToDb(notes: Notes) {
        //insert notes in Db
        val notesApp = applicationContext as NotesApp
        val notesDao = notesApp.getNotesDb().notesDao()
        notesDao.insert(notes)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
             R.id.Blog -> startActivity(Intent(this@MyNotesActivity,BlogActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }
}