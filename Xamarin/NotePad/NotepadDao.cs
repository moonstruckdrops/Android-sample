using System;
using System.Collections.Generic;
using Android.Content;
using Android.Database;
using Android.Database.Sqlite;

namespace NotePad
{
	public class NotepadDao
	{
		// DB設定
		private const String DATABASE_FILE_NAME = "notepad.db";
		private const int DATABASE_VERSION = 1;
		// テーブル名、カラム、SQL設定
		// TODO : LINQで設定してあげるのがC#的な感じ
		private const String TABLE_NAME = "notes";
		private const String COLUMN_ROWID = "rowid";
		private const String COLUMN_TITLE = "title";
		private const String COLUMN_BODY = "body";
		private static String[] COLUMNS = { COLUMN_ROWID, COLUMN_TITLE, COLUMN_BODY };
		private const String SELECTION_ROWID = COLUMN_ROWID + " = ?";
		private const String DATABASE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ROWID + " integer primary key autoincrement, " + COLUMN_TITLE + " text not null, " + COLUMN_BODY + " text not null);";
		private const String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
		// 変数類
		private Context context;
		private DatabaseHelper dbHelper;
		private SQLiteDatabase db;

		class DatabaseHelper : SQLiteOpenHelper
		{
			public DatabaseHelper (Context context)
				: base (context, DATABASE_FILE_NAME, null, DATABASE_VERSION)
			{
			}

			public override void OnCreate (SQLiteDatabase db)
			{
				db.ExecSQL (DATABASE_CREATE);
			}

			public override void OnUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
			{
				db.ExecSQL (DROP_TABLE);
				OnCreate (db);
			}
		}

		public NotepadDao (Context context)
		{
			this.context = context;
			if (db == null) {
				dbHelper = new DatabaseHelper (this.context);
				db = dbHelper.WritableDatabase;
			}
		}

		public long Insert (NotepadDto notepadDto)
		{
			long resultCode = -1;
			if (notepadDto == null) {
				return resultCode;
			}
			var values = CreateContentValues (notepadDto);
			try {
				db.BeginTransaction ();
				resultCode = db.Insert (TABLE_NAME, null, values);
				db.SetTransactionSuccessful ();
				return resultCode;
			} finally {
				db.EndTransaction ();
			}
		}

		public Boolean Update (NotepadDto notepadDto)
		{
			if (notepadDto == null) {
				return false;
			}
			var values = CreateContentValues (notepadDto);
			try {
				db.BeginTransaction ();
				String[] whereArgs = new String[] { notepadDto.RowId.ToString () };
				var resultCode = db.Update (TABLE_NAME, values, SELECTION_ROWID, whereArgs);
				db.SetTransactionSuccessful ();
				return (resultCode != 0);
			} finally {
				db.EndTransaction ();
			}
		}

		public Boolean Delete (long rowId)
		{
			try {
				db.BeginTransaction ();
				String[] whereArgs = new String[] { rowId.ToString () };
				long resultCode = db.Delete (TABLE_NAME, SELECTION_ROWID, whereArgs);
				db.SetTransactionSuccessful ();
				return (resultCode != 0);
			} finally {
				db.EndTransaction ();
			}
		}

		public List<NotepadDto> FindAll ()
		{
			var list = new List<NotepadDto> ();
			ICursor cursor = db.Query (TABLE_NAME, COLUMNS, null, null, null, null, null);
			try {
				if (cursor.Count < 1) {
					return null;
				}
				while (cursor.MoveToNext ()) {
					list.Add (CreateNotepadDto (cursor));
				}
				return list;
			} finally {
				cursor.Close ();
			}
		}

		public NotepadDto FindByRowId (long rowId)
		{
			var list = new List <NotepadDto> ();
			String[] selectionArgs = new String[] { rowId.ToString () };
			ICursor cursor = db.Query (TABLE_NAME, COLUMNS, SELECTION_ROWID, selectionArgs, null, null, null);
			try {
				if (cursor.Count < 1) {
					return null;
				}
				while (cursor.MoveToNext ()) {
					list.Add (CreateNotepadDto (cursor));
				}
				return list [0];
			} finally {
				cursor.Close ();
			}
		}

		public void CloseDb ()
		{
			db.Close ();
			dbHelper.Close ();
		}

		private NotepadDto CreateNotepadDto (ICursor cursor)
		{
			NotepadDto notepadDto = new NotepadDto ();
			notepadDto.RowId = cursor.GetLong (0);
			notepadDto.Title = cursor.GetString (1);
			notepadDto.Body = cursor.GetString (2);
			return notepadDto;
		}

		private ContentValues CreateContentValues (NotepadDto notepadDto)
		{
			ContentValues values = new ContentValues ();
			if (notepadDto.RowId > 0) {
				values.Put (COLUMN_ROWID, notepadDto.RowId);
			}
			values.Put (COLUMN_TITLE, notepadDto.Title);
			values.Put (COLUMN_BODY, notepadDto.Body);
			return values;
		}
	}
}

