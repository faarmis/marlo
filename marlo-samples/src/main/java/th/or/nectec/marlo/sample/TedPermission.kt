package th.or.nectec.marlo.sample

import android.content.Context
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*

fun Context.tedPermission(vararg permissions: String,
        block: TedPermission.Builder.() -> Unit): TedPermission.Builder {
    return TedPermission.with(this).setPermissions(*permissions).apply(block)
}

fun TedPermission.Builder.listener(listener: DslPermissionListener = DslPermissionListener(),
        block: DslPermissionListener.() -> Unit) {
    setPermissionListener(listener.apply(block))
}

class DslPermissionListener : PermissionListener {
    var onGranted: (() -> Unit)? = null
    var onDenied: ((deniedPermissions: List<String>) -> Unit)? = null

    override fun onPermissionGranted() {
        onGranted?.invoke()
    }

    override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
        onDenied?.invoke(deniedPermissions!!)
    }

    fun onGranted(block: () -> Unit) {
        onGranted = block
    }

    fun onDenied(block: (deniedPermissions: List<String>) -> Unit) {
        onDenied = block
    }

}
