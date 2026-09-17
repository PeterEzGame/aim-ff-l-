package com.theone.rootbridge

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnActivate: Button
    private lateinit var rbFFNormal: RadioButton
    private lateinit var rbFFMax: RadioButton

    private val SHIZUKU_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnActivate = findViewById(R.id.btnActivate)
        rbFFNormal = findViewById(R.id.rbFFNormal)
        rbFFMax = findViewById(R.id.rbFFMax)

        checkShizukuStatus()

        btnActivate.setOnClickListener {
            // Yêu cầu cấp quyền vẽ đè với URI chuẩn xác giúp máy tìm thấy tên ứng dụng ngay lập tức
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                Toast.makeText(this, "Hãy tìm và bật cho phép hiển thị trên ứng dụng khác!", Toast.LENGTH_LONG).show()
            } else {
                val targetGame = if (rbFFMax.isChecked) "Free Fire MAX" else "Free Fire Thường"
                Toast.makeText(this, "Đã kích hoạt cho $targetGame!", Toast.LENGTH_SHORT).show()
                startService(Intent(this, FloatingService::class.java))
            }
        }
    }

    private fun checkShizukuStatus() {
        try {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                tvStatus.text = "Trạng thái: Shizuku Đã cấp quyền (Sẵn sàng)"
            } else {
                tvStatus.text = "Trạng thái: Đang yêu cầu quyền Shizuku..."
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
            }
        } catch (e: Exception) {
            tvStatus.text = "Trạng thái: Không tìm thấy Shizuku Service!"
        }
    }
}
