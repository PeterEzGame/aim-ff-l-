package com.theone.rootbridge

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.topjohnwu.superuser.Shell
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var btnExecute: Button

    private val shizukuPermissionListener =
        Shizuku.OnRequestPermissionResultListener { _, grantResult ->
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                statusText.text = "Shizuku đã cấp quyền! Đang kiểm tra môi trường..."
                initRootBridge()
            } else {
                statusText.text = "Shizuku bị từ chối quyền."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 60, 60, 60)
        }

        statusText = TextView(this).apply {
            text = "Trạng thái: Sẵn sàng khởi động..."
            textSize = 15f
        }

        btnExecute = Button(this).apply {
            text = "Kích hoạt Cầu nối hệ thống"
            setOnClickListener { checkEnvironmentAndShizuku() }
        }

        layout.addView(statusText)
        layout.addView(btnExecute)
        setContentView(layout)

        Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
    }

    private fun checkEnvironmentAndShizuku() {
        if (RootShield.isEnvironmentCompromised(this)) {
            statusText.text = "Cảnh báo: Phát hiện dấu vết môi trường. Đang ẩn tiến trình..."
        }

        if (!Shizuku.pingBinder()) {
            statusText.text = "Lỗi: Chưa bật Shizuku hoặc chưa kết nối ADB Wireless!"
            Toast.makeText(this, "Hãy khởi chạy Shizuku trước", Toast.LENGTH_SHORT).show()
            return
        }

        if (Shizuku.isPreV11()) {
            statusText.text = "Lỗi: Phiên bản Shizuku quá cũ (< v11.3)."
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            statusText.text = "Shizuku đã sẵn sàng. Đang tiến hành leo thang..."
            initRootBridge()
        } else {
            Shizuku.requestPermission(0)
        }
    }

    private fun initRootBridge() {
        try {
            val isRootAvailable = Shell.getShell().isRoot
            if (isRootAvailable) {
                val result = Shell.cmd("su -c 'id; setenforce 0'").exec()
                if (result.isSuccess) {
                    statusText.text = "Thành công: Đã kết nối Root & Shizuku Bridge trên Oppo!"
                } else {
                    statusText.text = "Cảnh báo: Có quyền Root nhưng lệnh su bị chặn."
                }
            } else {
                statusText.text = "Thất bại: Thiết bị chưa được Root (Magisk/KernelSU)."
            }
        } catch (e: Exception) {
            statusText.text = "Lỗi ngoại lệ: ${e.message}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
    }
}
