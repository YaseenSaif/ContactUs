package com.codingacademy.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val NAME = "Yaseen Saif"
const val NUMBER = "770681883"

class MainActivity : AppCompatActivity() {
    private lateinit var sendEmailButton: Button
    private lateinit var openWebstieButton: Button
    private lateinit var locationButton: Button
    private lateinit var contactsButton: Button
    private val TAG = "PermissionDemo"
    private val READ_CONACTS_REQUEST_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendEmailButton = findViewById(R.id.send_email_button)
        sendEmailButton.setOnClickListener {
            sendEmailIntent()
        }
        openWebstieButton = findViewById(R.id.open_website_button)
        openWebstieButton.setOnClickListener {
            websiteIntent("https://github.com/")
        }
        locationButton = findViewById(R.id.location_button)
        locationButton.setOnClickListener {
            websiteIntent("geo:15.383153,44.157927")
        }
        contactsButton = findViewById(R.id.contacts_button)
        contactsButton.setOnClickListener {
            getContacts()
        }
    }

    fun getContacts() {

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }

        else {
            var nameList: MutableList<String> = arrayListOf()
            val contactUri = ContactsContract.Contacts.CONTENT_URI
           // Log.d("ddddd","$ContactsContract.CONTENT_FILTER_URI)
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = contentResolver.query(contactUri, queryFields, null, null, null)
            cursor?.use {
                if (it.count == 0)
                    return
                for (i in 0 until it.count) {
                    it.moveToPosition(i)
                    nameList.add(it.getString(0))
                    Log.d("$i", "${nameList[i]}")
                }
            }
            if (NAME in nameList) {
                var callIntent = Intent().apply {
                    action = Intent.ACTION_DIAL
                    data = Uri.parse("tel:$NUMBER")
                }
                startActivity(callIntent)
            } else {
                val intent = Intent().apply {
                    action = ContactsContract.Intents.Insert.ACTION
                    putExtra(ContactsContract.Intents.Insert.NAME, NAME)
                    putExtra(ContactsContract.Intents.Insert.PHONE, NUMBER)
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                }
                startActivity(intent)
            }

        }
    }

    private fun sendEmailIntent() {
        var sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_EMAIL, arrayOf("yasenshibane77@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "homework")
            putExtra(Intent.EXTRA_TEXT, "this is my homework")
            type = "text/plain"
        }
        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(sendIntent)
        }
    }


    private fun websiteIntent(UriString: String) {
        var webIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(UriString)
        }
        if (webIntent.resolveActivity(packageManager) != null) {
            startActivity(webIntent)
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONACTS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            READ_CONACTS_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                } else {
                    getContacts()
                }
            }
        }
    }


}
