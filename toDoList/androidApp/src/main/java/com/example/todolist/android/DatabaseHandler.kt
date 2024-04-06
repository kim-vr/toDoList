package com.example.todolist.android

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TASK_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TASK_TABLE")
        onCreate(db)
    }

    fun insertTask(task: Task) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(NAME, task.name)
        cv.put(DATE, task.date)
        cv.put(DESCRIPTION, task.description)
        cv.put(IS_CHECKED, if (task.isChecked) 1 else 0)
        db.insert(TASK_TABLE, null, cv)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllTasks(): List<Task> {
        val db = this.readableDatabase
        val taskList = mutableListOf<Task>()
        val cursor = db.rawQuery("SELECT * FROM $TASK_TABLE", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val date = cursor.getString(cursor.getColumnIndex(DATE))
                val description = cursor.getString(cursor.getColumnIndex(DESCRIPTION))
                val isChecked = cursor.getInt(cursor.getColumnIndex(IS_CHECKED)) == 1
                val stateName = cursor.getString(cursor.getColumnIndex(STATE))
                val state = TaskState.valueOf(stateName)

                val task = Task(id, name, date, description, isChecked)
                taskList.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    fun getAllTasksCursor(): Cursor {
        return this.readableDatabase.query(
            TASK_TABLE,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    fun updateTask(task: Task) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(NAME, task.name)
        cv.put(DATE, task.date)
        cv.put(DESCRIPTION, task.description)
        cv.put(IS_CHECKED, if (task.isChecked) 1 else 0)
        cv.put(STATE, task.state.name)
        db.update(TASK_TABLE, cv, "$ID = ?", arrayOf(task.id.toString()))
        db.close()
    }

    fun deleteTask(taskId: Int) {
        val db = this.writableDatabase
        db.delete(TASK_TABLE, "$ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "TasksDatabase"
        private const val TASK_TABLE = "Tasks"
        private const val ID = "id"
        private const val NAME = "name"
        private const val DATE = "date"
        private const val DESCRIPTION = "description"
        private const val IS_CHECKED = "isChecked"
        private const val STATE = "state"

        private const val CREATE_TASK_TABLE = "CREATE TABLE $TASK_TABLE (" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$NAME TEXT, " +
                "$DATE TEXT, " +
                "$DESCRIPTION TEXT, " +
                "$IS_CHECKED INTEGER," +
                "$STATE TEXT DEFAULT 'TODO')"
    }
}
