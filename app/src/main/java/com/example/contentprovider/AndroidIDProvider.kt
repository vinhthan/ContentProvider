package com.example.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils

class AndroidIDProvider() : ContentProvider() {
    /**
     * Database specific constant declarations
     */
    private var db: SQLiteDatabase? = null

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
            onCreate(db)
        }
    }

    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context)
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.writableDatabase
        return if (db == null) false else true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        /**
         * Add a new student record
         */
        val rowID = db!!.insert(TABLE_NAME, "", values)
        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            val _uri = ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }
        throw SQLException("Failed to add a record into $uri")
    }

    override fun query(
        uri: Uri, projection: Array<String>?,
        selection: String?, selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = TABLE_NAME
        when (uriMatcher!!.match(uri)) {
            URI_ALL_ITEMS_CODE -> qb.projectionMap =
                STUDENTS_PROJECTION_MAP

            URI_ONE_ITEM_CODE -> qb.appendWhere(_ID + "=" + uri.pathSegments[1])
            else -> {}
        }
        if (sortOrder == null || sortOrder === "") {
            /**
             * By default sort on student names
             */
            sortOrder = VALUE
        }
        val c = qb.query(
            db, projection, selection,
            selectionArgs, null, null, sortOrder
        )
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        when (uriMatcher!!.match(uri)) {
            URI_ALL_ITEMS_CODE -> count = db!!.delete(TABLE_NAME, selection, selectionArgs)
            URI_ONE_ITEM_CODE -> {
                val id = uri.pathSegments[1]
                count = db!!.delete(
                    TABLE_NAME,
                    _ID + " = " + id +
                            if (!TextUtils.isEmpty(selection)) "AND ($selection)" else "",
                    selectionArgs
                )
            }

            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun update(
        uri: Uri, values: ContentValues?,
        selection: String?, selectionArgs: Array<String>?
    ): Int {
        var count = 0
        when (uriMatcher!!.match(uri)) {
            URI_ALL_ITEMS_CODE -> count = db!!.update(TABLE_NAME, values, selection, selectionArgs)
            URI_ONE_ITEM_CODE -> count = db!!.update(
                TABLE_NAME,
                values,
                (_ID + " = " + uri.pathSegments[1] +
                        (if (!TextUtils.isEmpty(selection)) "AND ($selection)" else "")),
                selectionArgs
            )

            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun getType(uri: Uri): String? {
        when (uriMatcher!!.match(uri)) {
            URI_ALL_ITEMS_CODE -> return "vnd.android.cursor.dir/vnd.example.backupdata"
            URI_ONE_ITEM_CODE -> return "vnd.android.cursor.item/vnd.example.backupdata"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    companion object {
        val AUTHORITY = "com.android.example.checkandroidid.AndroidIDProvider"
        val CONTENT_PATH = "backupdata"
        val URL = "content://" + AUTHORITY + "/" + CONTENT_PATH
        val CONTENT_URI = Uri.parse(URL)
        val _ID = "_id"
        val VALUE = "_value"
        private val STUDENTS_PROJECTION_MAP: HashMap<String, String>? = null
        val URI_ALL_ITEMS_CODE = 1
        val URI_ONE_ITEM_CODE = 2
        var uriMatcher: UriMatcher? = null

        init {
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher!!.addURI(AUTHORITY, CONTENT_PATH, URI_ALL_ITEMS_CODE)
            uriMatcher!!.addURI(AUTHORITY, CONTENT_PATH + "/#", URI_ONE_ITEM_CODE)
        }

        val DATABASE_NAME = "SampleDatabase"
        val TABLE_NAME = "AndroidID"
        val DATABASE_VERSION = 1
        val CREATE_DB_TABLE = (" CREATE TABLE " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " name TEXT NOT NULL);")
    }
}
