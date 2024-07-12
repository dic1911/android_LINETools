package moe.hx030.linetools

import android.R.attr.process
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Arrays
import java.util.concurrent.Executors
import kotlin.math.ceil


class Utils {
    companion object {
        lateinit var ctx: Context
        var density = 1F
        var executorSvc = Executors.newSingleThreadExecutor()
        var cmdExitCode = 0
        var cmdOutput = ""

        val copyDB = Runnable {
            val TAG = "030-cmd-cp"
            val target = ctx.getDatabasePath("naver_line")
//            val cmd = "/system/bin/su -c '/system/bin/yes | /system/bin/cp -fv /data/data/jp.naver.line.android/databases/naver_line $target'"
//            val cmd = "/system/bin/su -c /system/bin/cat /mnt/t"
//            val cmd = "/system/bin/su -c /system/bin/ls -l /data/data/jp.naver.line.android/databases/naver_line > $target"
//            val cmd = "/system/bin/su -c /system/bin/cp /data/data/jp.naver.line.android/databases/naver_line $target"
            val cmd = "ls /data/data/"
//            val cmd = "/system/bin/ls /system/bin/su"
            Log.d(TAG, cmd)
            val sa = Array<String>(3) {""}
            sa[0] = "su"
            sa[1] = "-c"
            sa[2] = cmd
            try {
                Log.d(TAG, sa.contentToString())
                val proc = Runtime.getRuntime().exec(sa)

                val reader = BufferedReader(
                    InputStreamReader(proc.inputStream)
                )
                var read: Int
                val buffer = CharArray(4096)
                val output = StringBuffer()
                while ((reader.read(buffer).also { read = it }) > 0) {
                    output.append(buffer, 0, read)
                }
                reader.close()
                cmdOutput = output.toString()
                Log.d(TAG, cmdOutput)

                proc.waitFor()
                cmdExitCode = proc.exitValue()
                Log.d(TAG, "cp done $cmdExitCode")
            } catch (ex: Exception) {
                Log.e(TAG, "cp failed", ex)
                cmdExitCode = -1
            }
        }

        fun init(context: Context) {
            ctx = context
            density = ctx.resources.displayMetrics.density
        }

        fun dp(v: Int): Int {
            return ceil((v * density).toDouble()).toInt()
        }

        fun showToast(resId: Int) {
            showToast(ctx.getString(resId), Toast.LENGTH_LONG)
        }
        fun showToast(str: String) {
            showToast(str, Toast.LENGTH_LONG)
        }
        fun showToast(str: String, len: Int) {
            Toast.makeText(ctx, str, len).show()
        }

        fun databaseExists(name: String): Boolean {
            return ctx.getDatabasePath(name).exists()
        }
    }
}