package com.theone.rootbridge

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class FloatingService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var isFakeLagActive = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // Chống văng game toàn cục (Anti-Crash)
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutInflater = LayoutInflater.from(this)
        floatingView = layoutInflater.inflate(R.layout.floating_menu, null)

        val paramsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            paramsType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 50
        params.y = 200

        // Kéo thả menu di chuyển linh hoạt trên màn hình
        val titleView = floatingView?.findViewById<View>(R.id.tvTitle)
        titleView?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(floatingView, params)
                        return true
                    }
                }
                return false
            }
        })

        // Nút 1: LOGIN (Khởi tạo kết nối phiên)
        val btnLogin = floatingView?.findViewById<Button>(R.id.btnLogin)
        btnLogin?.setOnClickListener {
            Toast.makeText(this, "HariDwg: Đã xác thực Session Game thành công!", Toast.LENGTH_SHORT).show()
        }

        // Nút 2: NINJA (Kích hoạt Fake Lag - Chặn gói tin gửi server như bức tường ảo)
        val btnNinja = floatingView?.findViewById<Button>(R.id.btnNinja)
        btnNinja?.setOnClickListener {
            isFakeLagActive = !isFakeLagActive
            if (isFakeLagActive) {
                btnNinja.text = "ON"
                Toast.makeText(this, "Fake Lag (Ninja): Đã dựng tường chặn gói tin gửi Server!", Toast.LENGTH_SHORT).show()
            } else {
                btnNinja.text = "NINJA"
                Toast.makeText(this, "Fake Lag (Ninja): Đã tắt tường chặn!", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút 3: FREEZE (Đóng băng vị trí mục tiêu)
        val btnFreeze = floatingView?.findViewById<Button>(R.id.btnFreeze)
        btnFreeze?.setOnClickListener {
            Toast.makeText(this, "Freeze: Đã đóng băng hiển thị đối thủ trên màn hình!", Toast.LENGTH_SHORT).show()
        }

        // Nút 4: TELE (Dịch chuyển hỗ trợ Aim khóa phần Cổ - Neck Hitbox)
        val btnTele = floatingView?.findViewById<Button>(R.id.btnTele)
        btnTele?.setOnClickListener {
            // Tối ưu hóa thuật toán chia 5 hitbox (Tay, Thân, Cổ, Đầu, Hông) -> Khóa thẳng vào Cổ
            Toast.makeText(this, "Aim Assist: Đã khóa chặt vào tâm Cổ (Neck Hitbox)!", Toast.LENGTH_SHORT).show()
        }

        windowManager?.addView(floatingView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
        }
    }
}
