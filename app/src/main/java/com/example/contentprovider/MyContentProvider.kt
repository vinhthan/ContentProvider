package com.example.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class MyContentProvider : ContentProvider() {
    // Khai báo Uri của Content Provider
    companion object {
        private const val AUTHORITY = "com.example.myapp.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/data")
    }

    override fun onCreate(): Boolean {
        // Khởi tạo và cấu hình Content Provider
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // Thực hiện truy vấn dữ liệu từ nguồn dữ liệu của bạn
        // Ví dụ: query từ SQLite Database
        return null
    }

    override fun getType(uri: Uri): String? {
        // Trả về kiểu MIME của dữ liệu trả về
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Thêm dữ liệu vào nguồn dữ liệu của bạn
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        // Xóa dữ liệu từ nguồn dữ liệu của bạn
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        // Cập nhật dữ liệu trong nguồn dữ liệu của bạn
        return 0
    }
}
