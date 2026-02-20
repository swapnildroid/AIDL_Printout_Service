package com.example.clientapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aidlprintoutservice.IMyAidlInterface
import com.example.aidlprintoutservice.IMyCallback
import com.example.aidlprintoutservice.MyData
import com.example.clientapp.ui.theme.AIDLPrintoutServiceTheme

class MainActivity : ComponentActivity() {

    private var myAidlInterface: IMyAidlInterface? = null
    private var isBound = false

    private var connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            Log.d("TAGGED", "onServiceConnected() called with: p0 = $p0, p1 = $p1")
            isBound = true
            myAidlInterface = IMyAidlInterface.Stub.asInterface(p1)
            Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()
            myAidlInterface?.basicTypes(
                /* anInt = */ 1,
                /* aLong = */ 2L,
                /* aBoolean = */ true,
                /* aFloat = */ 3.0f,
                /* aDouble = */ 4.0,
                /* aString = */ "Hello, world!"
            )
            myAidlInterface?.sendData(MyData("Test", 1))
            myAidlInterface?.sendDataWithCallback(MyData("Test2", 1),
                object : IMyCallback {
                    override fun onResult(result: String?) {
                        Log.d("TAGGED", "onResult() called with: result = $result")
                    }

                    override fun asBinder(): IBinder? {
                        Log.i("TAGGED", "MainActivity::asBinder: ")
                        return null
                    }

                })
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
            Log.d("TAGGED", "onBindingDied() called with: name = $name")
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
            Log.d("TAGGED", "onNullBinding() called with: name = $name")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d("TAGGED", "onServiceDisconnected() called with: p0 = $p0")
            isBound = false
            myAidlInterface = null
            Toast.makeText(this@MainActivity, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("TAGGED", "MainActivity::onStart: ")
        val intent = Intent()
        intent.component = ComponentName(
            "com.example.aidlprintoutservice",
            "com.example.aidlprintoutservice.MyAidlService"
        )
        intent.action = "com.example.aidlprintoutservice.IMyAidlInterface"
        val resolveService = packageManager.resolveService(intent, 0)
        Log.i("TAGGED", "MainActivity::onStart: resolveService=$resolveService")
        val bindService = bindService(intent, connection, BIND_AUTO_CREATE)
        Log.i("TAGGED", "MainActivity::onStart: bindService=$bindService")
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIDLPrintoutServiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android Client",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello Client $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AIDLPrintoutServiceTheme {
        Greeting("Android")
    }
}