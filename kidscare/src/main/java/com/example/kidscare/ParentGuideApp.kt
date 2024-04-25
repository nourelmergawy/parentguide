import android.app.AlertDialog
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.kidscare.service.ScreenUnlockReceiver

class ParentGuideApp : Application() {

    override fun onCreate() {
        super.onCreate()
        registerScreenUnlockReceiver()
    }

    fun showParentDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.apply {
            setTitle("Parental Guide")
            setMessage("Your parents are watching out for you. Be safe!")
            setCancelable(false)
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun registerScreenUnlockReceiver() {
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        registerReceiver(ScreenUnlockReceiver(), filter)
    }
}

