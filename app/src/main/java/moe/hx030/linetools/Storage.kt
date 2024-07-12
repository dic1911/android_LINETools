package moe.hx030.linetools

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.OpenParams
import android.util.Log
import androidx.core.database.getStringOrNull
import kotlinx.coroutines.flow.callbackFlow
import moe.hx030.linetools.Constants.Companion.LINE_DATABASE_NAME
import moe.hx030.linetools.data.Chat
import moe.hx030.linetools.data.Group
import java.io.File
import java.lang.Long.parseLong
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class Storage {
    companion object {
        lateinit var prefs: SharedPreferences
        lateinit var db: SQLiteDatabase
        var inc: Long = 0L

        fun loadDb(ctx: Context): Boolean {
            val dbFile = ctx.getDatabasePath(LINE_DATABASE_NAME)
            if (!dbFile.exists()) return false
            db = SQLiteDatabase.openDatabase(dbFile, OpenParams.Builder().setOpenFlags(SQLiteDatabase.OPEN_READWRITE).build())
            return true
        }

        fun deleteDb(ctx: Context): Boolean {
            return ctx.getDatabasePath(LINE_DATABASE_NAME).delete()
        }

        fun getColumns(db: SQLiteDatabase, table: String): List<String> {
            val ret = ArrayList<String>()
            val args = Array(0) {""}
            val cursor = db.rawQuery("select name from pragma_table_info('$table') as info;", args)
            while (cursor.moveToNext()) {
                ret.add(cursor.getString(0))
            }
            cursor.close()
            return ret
        }

        fun mergeTable(targetDb: SQLiteDatabase, oldDbPath: String, table: String): Boolean {
            val oldDb = SQLiteDatabase.openDatabase(File(oldDbPath), OpenParams.Builder().setOpenFlags(SQLiteDatabase.OPEN_READONLY).build())
            val oldColumns = getColumns(oldDb, table)
            val newColumns = getColumns(targetDb, table)
            val columns = ArrayList<String>(0)

            var lastSeq = 0L
            var cursor = oldDb.rawQuery("SELECT seq FROM sqlite_sequence WHERE name = '$table'", emptyArray())
            if (cursor.moveToNext()) lastSeq = cursor.getLong(0) // old last seq
            cursor.close()

            for (col in oldColumns) {
                if (newColumns.contains(col)) columns.add(col)
            }
            val colsStr = columns.joinToString(",")

            // update id - TODO: better check if necessary
            var hasId = false
            if (columns.contains("id")) {
                hasId = true
                targetDb.execSQL("UPDATE $table SET id = id + $lastSeq")
            }
            oldDb.close()

            targetDb.execSQL("ATTACH DATABASE '$oldDbPath' AS source")
            targetDb.execSQL("INSERT INTO $table ($colsStr) SELECT $colsStr FROM source.$table")
            targetDb.execSQL("DETACH DATABASE source")

            // ensure seq in sqlite_sequence is correct
            if (hasId) {
                cursor = targetDb.rawQuery(
                    "SELECT id FROM $table ORDER BY id DESC LIMIT 1",
                    emptyArray()
                )
                if (cursor.moveToNext()) {
                    lastSeq = cursor.getLong(0) // new last seq
                }
                cursor.close()
                targetDb.execSQL("UPDATE sqlite_sequence SET seq = $lastSeq WHERE name = '$table'")
            }

            return true
        }

        fun listGroups(): List<Group>? {
            if (!this::db.isInitialized) {
                return null
            }

            val cursor = db.rawQuery("SELECT id, name FROM groups", emptyArray())

            if (cursor.count == 0) return emptyList()

            val ret = ArrayList<Group>()
            while (cursor.moveToNext()) {
                ret.add(Group(cursor.getString(0), cursor.getString(1)))
            }
            cursor.close()

            return ret
        }

        fun listChats(): List<Chat>? {
            if (!this::db.isInitialized) return null

            val cursor = db.rawQuery(
                "SELECT ct.name, ct.server_name, g.name, c.chat_id, c.last_message, c.last_created_time, c.message_count\n" +
                    "FROM chat c\n" +
                    "LEFT JOIN contacts ct ON ct.m_id = c.chat_id\n" +
                    "LEFT JOIN groups g ON g.id = c.chat_id\n" +
                    "WHERE NOT c.last_created_time IS NULL\n" +
                    "ORDER BY c.last_created_time DESC", emptyArray())

            if (cursor.count == 0) return emptyList()

            val ret = ArrayList<Chat>()
            while (cursor.moveToNext()) {
                val rawTimestampStr = cursor.getStringOrNull(5)
                var rawTimestamp = 0L
                if (rawTimestampStr != null) rawTimestamp = parseLong(rawTimestampStr)
                val timestampCal = Calendar.Builder().setInstant(rawTimestamp).build()
                val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault(Locale. Category. FORMAT))
                val timestamp = fmt.format(timestampCal.time)

                Log.d("030-time", "$rawTimestampStr $timestamp")
                var title = cursor.getStringOrNull(0)
                if (title == null) {
                    title = cursor.getStringOrNull(2)
                    if (title == null) {
                        title = "N/A"
                    }
                } else {
                    val orig = cursor.getStringOrNull(1)
                    if (orig != null && orig != title) title = "$title ($orig)"
                }
                var msg = cursor.getStringOrNull(4)
                if (msg == null) msg = "(empty)"
                ret.add(
                    Chat(
                        cursor.getString(3),
                        title, msg, timestamp,
                        cursor.getLong(6)
                    )
                )
            }
            return ret
        }
    }
}