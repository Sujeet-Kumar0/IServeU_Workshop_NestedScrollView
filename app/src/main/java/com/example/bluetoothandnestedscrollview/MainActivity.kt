package com.example.bluetoothandnestedscrollview

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    //ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val serviceGenerator = ServiceGenerator.buildService(APIservice::class.java)
        val call = serviceGenerator.getPosts()
        //adapter
        //mBlueAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val mBlueAdapter: BluetoothAdapter? = bluetoothManager.adapter

        //check if bluetooth is available or not
        if (mBlueAdapter == null) {
            showToast("Bluetooth is not available")
        } else {
            showToast("Bluetooth is available")
        }

        //set image according to bluetooth status(on/off)
        if (mBlueAdapter != null) {
            if (mBlueAdapter.isEnabled) {
                bluetoothIv.setImageResource(R.drawable.ic_action_on)
            } else {
                bluetoothIv.setImageResource(R.drawable.ic_action_off)
            }
        }

        //on btn click
        onBtn.setOnClickListener(View.OnClickListener {
            if (mBlueAdapter != null) {
                if (!mBlueAdapter.isEnabled) {
                    showToast("Turning On Bluetooth...")
                    //intent to on bluetooth
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnClickListener
                    }
                    startActivityForResult(intent, REQUEST_ENABLE_BT)
                } else {
                    showToast("Bluetooth is already on")
                }
            }
        })
        //discover bluetooth btn click
        discoverableBtn.setOnClickListener(View.OnClickListener {
            if (mBlueAdapter != null) {
                if (!mBlueAdapter.isDiscovering) {
                    showToast("Making Your Device Discoverable")
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnClickListener
                    }
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                    startActivityForResult(intent, REQUEST_DISCOVER_BT)
                }
            }
        })
        //off btn click
        offBtn.setOnClickListener(View.OnClickListener {
            if (mBlueAdapter != null) {
                if (mBlueAdapter.isEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnClickListener
                    }
                    mBlueAdapter.disable()
                    showToast("Turning Bluetooth Off")
                    bluetoothIv.setImageResource(R.drawable.ic_action_off)
                } else {
                    showToast("Bluetooth is already off")
                }
            }
        })
        //get paired devices btn click
        pairedBtn.setOnClickListener(View.OnClickListener {
            if (mBlueAdapter != null) {
                if (mBlueAdapter.isEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnClickListener
                    }
                    pairedTv.text = "Paired Devices"
                    val devices = mBlueAdapter.bondedDevices
                    for (device in devices) {
                        pairedTv.append(
                            """
                                    
                                    Device: ${device.name}
                                    """.trimIndent()
                        )
                    }
                } else {
                    //bluetooth is off so can't get paired devices
                    showToast("Turn on bluetooth to get paired devices")
                }
            }
        })


        call.enqueue(object : Callback<MutableList<PostModel>> {
            override fun onResponse(
                call: Call<MutableList<PostModel>>,
                response: Response<MutableList<PostModel>>
            ) {
                RecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = ViewHolder(response.body()!!)

                }
            }

            override fun onFailure(call: Call<MutableList<PostModel>>, t: Throwable) {
                t.printStackTrace()
                Log.e("error", t.message.toString())
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> if (resultCode == RESULT_OK) {
                //bluetooth is on
                bluetoothIv.setImageResource(R.drawable.ic_action_on)
                showToast("Bluetooth is on")
            } else {
                //user denied to turn bluetooth on
                showToast("Couldn't turn on bluetooth")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //toast message function
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 0
        private const val REQUEST_DISCOVER_BT = 1
    }

}