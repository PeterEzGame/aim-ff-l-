import rikka.shizuku.Shizuku
import android.content.pm.PackageManager
import android.widget.Toast

// Thêm đoạn kiểm tra quyền Shizuku khi mở app
private val SHIZUKU_PERMISSION_REQUEST_CODE = 1001

private fun checkShizukuPermission() {
    if (Shizuku.isPreV11()) {
        Toast.makeText(this, "Phiên bản Shizuku quá cũ, hãy cập nhật!", Toast.LENGTH_LONG).show()
        return
    }
    
    if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
        // Đã được cấp quyền, tiến hành bật dịch vụ
        initHariDwgService()
    } else if (Shizuku.shouldShowRequestPermissionRationale()) {
        Toast.makeText(this, "Vui lòng cấp quyền Shizuku trong ứng dụng quản lý!", Toast.LENGTH_LONG).show()
    } else {
        Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
    }
}
