package jp.co.moonstruckdrops.android.notepad.dao;

import java.util.ArrayList;
import java.util.List;

import jp.co.moonstruckdrops.android.notepad.dto.NotepadDto;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ノートをDBに永続化するDAO.
 * @author moonstruckdrops
 *
 */
public class NotepadDao {
    /**
     * ログに出力するクラス名.
     */
    private static final String TAG = "NotepadDao";
    /**
     * データベース名.
     */
    private static final String DATABASE_FILE_NAME = "notepad.db";
    /**
     * データベースのバージョン名.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * データベースのテーブル名.
     */
    private static final String TABLE_NAME = "notes";
    /**
     * カラム名"主キー".
     */
    private static final String COLUMN_ROWID = "rowid";
    /**
     * カラム名"タイトル"
     */
    private static final String COLUMN_TITLE = "title";
    /**
     * カラム名"本文"
     */
    private static final String COLUMN_BODY = "body";
    // CHECKSTYLE:OFF eclipseのインデントの自動整形で空白が挿入されてしまう為
    /**
     * レコード抽出対象のカラム.
     */
    private static final String[] COLUMNS = { COLUMN_ROWID, COLUMN_TITLE, COLUMN_BODY };
    // CHECKSTYLE:ON
    /**
     * 主キーを使ってレコードを選択、削除する際の条件.
     */
    private static final String SELECTION_ROWID = COLUMN_ROWID + " = ?";
    /**
     * データベースを作成するSQL
     */
    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ROWID + " integer primary key autoincrement, " + COLUMN_TITLE + " text not null, " + COLUMN_BODY + " text not null);";
    /**
     * データベースヘルパーのインスタンス。
     */
    private DatabaseHelper dbHelper;
    /**
     * データベースのインスタンス。
     */
    private SQLiteDatabase db;
    /**
     * コンテキスト.
     */
    private final Context context;


    /**
     * ノートパッドアプリで使用するデータベースのヘルパー.
     * 主にデータベースの作成とアップグレードを行う.
     * @author moonstruckdrops
     *
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * コンストラクタを生成する.
         * @param context コンテキスト
         */
        public DatabaseHelper(Context context) {
            super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        }

        /**
         * データベースのテーブルを作成する.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Create database : " + DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        /**
         * データベースのテーブルをアップグレードする.
         * ここでは一度テーブルを削除し、再度テーブルを作成している.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }


    /**
     * コンストラクタの生成をする.
     * このときdbHelper変数がnullの場合にはdb変数も同時に初期化をする.
     * これはdb変数がデータベースへの書き込み状態を取得するのに、dbHelper変数に依存している為である.
     * @param context コンテキスト
     */
    public NotepadDao(Context context) {
        this.context = context.getApplicationContext();
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(this.context);
            db = dbHelper.getWritableDatabase();
        }
    }

    /**
     * データベースにノートパッドの内容を新規に挿入する.
     * 挿入した結果正しく挿入された場合は主キーを返すが、
     * 正しく挿入されなかった場合は-1の値を返す.
     * @param notepadDto ノートパッドの内容
     * @return データベースへの挿入結果を返す。
     */
    public long insert(NotepadDto notepadDto) {
        long resultCode = -1;
        //引数の値がnullの時は異常状態なので-1を返す.
        if (notepadDto == null) {
            Log.w(TAG, "insert(NotepadDto notepadDto) : notepadDto is null.");
            return resultCode;
        }
        ContentValues values = createContentValues(notepadDto);
        try {
            //トランザクションを開始する
            db.beginTransaction();
            resultCode = db.insert(TABLE_NAME, null, values);
            //現在のトランザクションの成功したことを示す
            db.setTransactionSuccessful();
            return resultCode;
        } finally {
            //トランザクションを終了する.
            db.endTransaction();
        }
    }

    /**
     * データベースに永続化されているノートパッドの内容を更新する.
     * 更新mに成功したときにはtrueを返すが、それ以外の場合はfalseを返す.
     * @param notepadDto ノートパッドの内容
     * @return 更新に成功したときtrue,失敗した場合はfalseを返す.
     */
    public boolean update(NotepadDto notepadDto) {
        //引数がnullの時は異常状態なのでfalseを返す
        if (notepadDto == null) {
            Log.w(TAG, "update(NotepadDto notepadDto) : notepadDto is null.");
            return false;
        }
        ContentValues values = createContentValues(notepadDto);
        try {
            db.beginTransaction();
            // CHECKSTYLE:OFF eclipseのインデントの自動整形で空白が挿入されてしまう為
            String[] whereArgs = new String[] { Long.toString(notepadDto.getRowId()) };
            // CHECKSTYLE:ON
            Log.i(TAG, "primarykey = " + notepadDto.getRowId());
            int resultCode = db.update(TABLE_NAME, values, SELECTION_ROWID, whereArgs);
            db.setTransactionSuccessful();
            return (resultCode != 0);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * データベースに永続化されているノートパッドの内容を削除する.
     * 削除する内容は引数で指定した主キーのものが対象である.
     * @param rowId 削除対象ノートパッドの内容の主キーの値
     * @return 削除に成功した場合true、それ以外の場合はfalseを返す.
     */
    public boolean delete(long rowId) {
        //引数がnullの時は異常状態なのでfalseを返す
        if (Long.valueOf(rowId) == null) {
            Log.w(TAG, "delete(long rowId) : rowId is illegal number.");
            return false;
        }
        try {
            db.beginTransaction();
            // CHECKSTYLE:OFF eclipseのインデントの自動整形で空白が挿入されてしまう為
            String[] whereArgs = new String[] { Long.toString(rowId) };
            // CHECKSTYLE:ON
            long resultCode = db.delete(TABLE_NAME, SELECTION_ROWID, whereArgs);
            db.setTransactionSuccessful();
            return (resultCode != 0);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * データベースから全てのレコードを取得する.
     * @return データベースのレコードの全てのノートパッドの内容
     */
    public List<NotepadDto> findAll() {
        List<NotepadDto> noteList = new ArrayList<NotepadDto>();
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null, null);
        try {
            // 1件も見つからなかったときはデータベースに何もレコードが無いのでnullを返す
            if (cursor.getCount() < 1) {
                Log.i(TAG, "findAll() : multiple records found.");
                return null;
            }
            // レコードの件数分、レコードの内容を読み出す
            while (cursor.moveToNext()) {
                noteList.add(createNotepadDto(cursor));
            }
            return noteList;
        } finally {
            cursor.close();
        }
    }

    /**
     * データベースから引数で指定した値(主キー)のレコードを取得する.
     * @param rowId 主キーの値
     * @return 引数で指定した主キーの値のレコード
     */
    public NotepadDto findByRowId(long rowId) {
        List<NotepadDto> noteList = new ArrayList<NotepadDto>();
        // CHECKSTYLE:OFF eclipseのインデントの自動整形で空白が挿入されてしまう為
        String[] selectionArgs = new String[] { Long.toString(rowId) };
        //CHECKSTYLE:ON
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, SELECTION_ROWID, selectionArgs, null, null, null);
        try {
            if (cursor.getCount() < 1) {
                Log.i(TAG, "findByRowId(long rowId) : record found. rowId = " + rowId);
                return null;
            }
            while (cursor.moveToNext()) {
                noteList.add(createNotepadDto(cursor));
            }
            //複数値があることはあり得ないが念の為にリストの先頭に格納された値を返却する
            return noteList.get(0);
        } finally {
            cursor.close();
        }
    }

    /**
     * データベースをクローズする.
     */
    public void closeDb() {
        db.close();
        dbHelper.close();
    }

    /**
     * 引数で指定したCursorからアプリケーションで使用可能なDtoに変換する.
     * 変換した値はNotepadDtoである.
     * @param cursor 現在のレコードを指し示すcursor
     * @return 変換結果であるNotePadDto
     */
    private NotepadDto createNotepadDto(Cursor cursor) {
        NotepadDto notepadDto = new NotepadDto();
        // CHECKSTYLE:OFF cursorのカラム指定がマジックナンバーとして警告されるため、CHECKSTYLEを外す。
        notepadDto.setRowId(cursor.getLong(0));
        notepadDto.setTitle(cursor.getString(1));
        notepadDto.setBody(cursor.getString(2));
        // CHECKSTYLE:ON
        return notepadDto;
    }

    /**
     * 引数で指定したDtoをAndroidのデータベースで使えるように変換する.
     * 変換した値はinsertやupdateに使用するContentValuesである.
     * @param notepadDto ノートパッドの内容
     * @return Dtoから変換したContentValues
     */
    private ContentValues createContentValues(NotepadDto notepadDto) {
        ContentValues values = new ContentValues();
        //主キーがあった場合は更新なので変換をする.
        if (notepadDto.getRowId() > 0) {
            values.put(COLUMN_ROWID, notepadDto.getRowId());
        }
        values.put(COLUMN_TITLE, notepadDto.getTitle());
        values.put(COLUMN_BODY, notepadDto.getBody());
        return values;

    }
}
