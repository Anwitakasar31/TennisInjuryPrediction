/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.Message
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetail
import org.tensorflow.lite.examples.TennisInjuryPredictor.Database.TennisServeDetailDBHelper
import org.tensorflow.lite.examples.poseestimation.ProjectConstants.TAG
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.ModelType
import org.tensorflow.lite.examples.poseestimation.ml.MoveNet
import org.tensorflow.lite.examples.poseestimation.ml.PoseClassifier
import org.tensorflow.lite.examples.poseestimation.ml.PoseNet
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
       /* private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }*/
    }

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default pose estimation model is 1 (MoveNet Thunder)
     * 0 == MoveNet Lightning model
     * 1 == MoveNet Thunder model
     * 2 == PoseNet model
     **/
    private var modelPos = 1

    /** Default device is GPU */
    private var device = Device.CPU

    private lateinit var tvScore: TextView
    private lateinit var tvFPS: TextView
    private lateinit var tvShoulderAngle: TextView
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var spnDevice: Spinner
    private lateinit var spnModel: Spinner
    private lateinit var tvClassificationValue1: TextView
    private lateinit var tvClassificationValue2: TextView
    private lateinit var tvClassificationValue3: TextView
    private lateinit var swClassification: SwitchCompat
    private var cameraSource: CameraSource? = null
    private var isClassifyPose = false
    private var tennisServeDetailDBHelper: TennisServeDetailDBHelper? = null
    lateinit var dataList: ArrayList<String>
    var playerID: Int = 0
    var playerName: String = ""
    var playerShoulderAngle: Float =0F
    private val requestPermissionLauncher =
            registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    openCamera()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                            .show(supportFragmentManager, FRAGMENT_DIALOG)
                }
            }
    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }

        override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
        ) {
            changeModel(position)
        }
    }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    private var setClassificationListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                showClassificationInfo(isChecked)
                isClassifyPose = isChecked
                isPoseClassifier()
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        var bundle :Bundle ? = intent.extras;
        if(bundle==null)
        {
            Log.d(TAG, "intent extra is null")
        }
        else
        {
            var id = bundle!!.getInt("id")
            Log.d(TAG, "playerid - " + id)
            var name = bundle!!.getString("name")
            Log.d(TAG, "playername - " + name)
            dataList = intent.getSerializableExtra("data") as ArrayList<String>

            if(dataList !=null && dataList.size > 1) {
                Log.i(TAG, "DataList size in Main - " + dataList.size)
                val playerID1 = dataList!![0]
                playerID = playerID1.toInt()
                playerName = dataList!![1]
                Log.i(TAG, "PlayerID in Main from Array Value - $playerID1")
                Log.i(TAG, "PlayerName in Main from Array Value - $playerName")
            }
          //  var strUser: String = intent.getStringExtra("value") // 2
        }
        tvScore = findViewById(R.id.tvScore)
        tvShoulderAngle = findViewById(R.id.tvShoulderAngle)
        tvFPS = findViewById(R.id.tvFps)
        spnModel = findViewById(R.id.spnModel)
        spnDevice = findViewById(R.id.spnDevice)
        surfaceView = findViewById(R.id.surfaceView)
        tvClassificationValue1 = findViewById(R.id.tvClassificationValue1)
        tvClassificationValue2 = findViewById(R.id.tvClassificationValue2)
        tvClassificationValue3 = findViewById(R.id.tvClassificationValue3)
        swClassification = findViewById(R.id.swPoseClassification)
        initSpinner()
        spnModel.setSelection(modelPos)
        swClassification.setOnCheckedChangeListener(setClassificationListener)
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
        btnSave = findViewById(R.id.button4)
        btnSave.setOnClickListener {
            saveTennisServeRecord(it);
        }
        btnCancel = findViewById(R.id.button5)
        btnCancel.setOnClickListener {
            ViewDashboard(it);
        }
        tennisServeDetailDBHelper = TennisServeDetailDBHelper( this)
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
                Manifest.permission.CAMERA,
                Process.myPid(),
                Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                        CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                            override fun onFPSListener(fps: Int) {
                                tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                            }

                            override fun onDetectedInfo(personScore: Float?, personShoulderAngle: Float?, poseLabels: List<Pair<String, Float>>?) {
                                tvScore.text = getString(R.string.tfe_pe_tv_score, personScore
                                        ?: 0f)
                                if (personShoulderAngle != null) {
                                    playerShoulderAngle = personShoulderAngle
                                }
                                tvShoulderAngle.text = getString(R.string.tfe_pe_tv_shoulderAngle, personShoulderAngle
                                        ?: 0f)
                                poseLabels?.sortedByDescending { it.second }?.let {
                                    tvClassificationValue1.text = getString(
                                            R.string.tfe_pe_tv_classification_value,
                                            convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
                                    )
                                    tvClassificationValue2.text = getString(
                                            R.string.tfe_pe_tv_classification_value,
                                            convertPoseLabels(if (it.size >= 2) it[1] else null)
                                    )
                                    tvClassificationValue3.text = getString(
                                            R.string.tfe_pe_tv_classification_value,
                                            convertPoseLabels(if (it.size >= 3) it[2] else null)
                                    )
                                }
                            }

                        }).apply {
                            prepareCamera()
                        }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.2f", pair.second)})"
    }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(this) else null)
    }

    // Init spinner that user can choose model and device they want.
    private fun initSpinner() {
        ArrayAdapter.createFromResource(
                this,
                R.array.tfe_pe_models_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnModel.adapter = adapter
            spnModel.onItemSelectedListener = changeModelListener
        }

        ArrayAdapter.createFromResource(
                this,
                R.array.tfe_pe_device_name, android.R.layout.simple_spinner_item
        ).also { adaper ->
            adaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnDevice.adapter = adaper
            spnDevice.onItemSelectedListener = changeDeviceListener
        }
    }

    // change model when app is running
    private fun changeModel(position: Int) {
        if (modelPos == position) return
        modelPos = position
        createPoseEstimator()
    }

    // change device type when app is running
    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.CPU
            1 -> Device.GPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    private fun createPoseEstimator() {
        val poseDetector = when (modelPos) {
            0 -> {
                MoveNet.create(this, device)
            }
            1 -> {
                MoveNet.create(this, device, ModelType.Thunder)
            }
            else -> {
                PoseNet.create(this, device)
            }
        }
        cameraSource?.setDetector(poseDetector)
    }

    private fun showClassificationInfo(isChecked: Boolean) {
        tvClassificationValue1.visibility = if (isChecked) View.VISIBLE else View.GONE
        tvClassificationValue2.visibility = if (isChecked) View.VISIBLE else View.GONE
        tvClassificationValue3.visibility = if (isChecked) View.VISIBLE else View.GONE
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun saveTennisServeRecord(view: View) {
        Log.d(TAG, "In saveTennisServeRecord")
        if (tennisServeDetailDBHelper == null)
        tennisServeDetailDBHelper = TennisServeDetailDBHelper( this)

        if(playerID != 0 && playerShoulderAngle !=0F) {
            //Save result here?
            var tennisServeDetail = TennisServeDetail()
            tennisServeDetail.SetPlayerID(playerID)
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val dateNow = Calendar.getInstance().time
            tennisServeDetail.SetRecordDate(dateNow)
            tennisServeDetail.SetServeAngle(playerShoulderAngle.toDouble())
            tennisServeDetailDBHelper?.addTennisServeDetail(tennisServeDetail)
            Log.d(TAG, "Saved TennisServeDetail")
            Message.message(applicationContext, "Tennis Serve record saved successfully")
        }
        else
        {
            Log.d(TAG, "Could not save TennisServeDetail")
        }
    }

    private fun ViewDashboard(view: View) {
        val myIntent = Intent(
            this@MainActivity,
            PlayerDashboardActivity::class.java
        )
        myIntent.putExtra("id", playerID)
        myIntent.putExtra("name", playerName)
        myIntent.putExtra("data", dataList)
        startActivityForResult(myIntent, 0)
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }
}
