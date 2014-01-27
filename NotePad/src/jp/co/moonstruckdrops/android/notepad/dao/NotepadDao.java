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
 * �m�[�g��DB�ɉi��������DAO.
 * @author moonstruckdrops
 *
 */
public class NotepadDao {
    /**
     * ���O�ɏo�͂���N���X��.
     */
    private static final String TAG = "NotepadDao";
    /**
     * �f�[�^�x�[�X��.
     */
    private static final String DATABASE_FILE_NAME = "notepad.db";
    /**
     * �f�[�^�x�[�X�̃o�[�W������.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * �f�[�^�x�[�X�̃e�[�u����.
     */
    private static final String TABLE_NAME = "notes";
    /**
     * �J������"��L�[".
     */
    private static final String COLUMN_ROWID = "rowid";
    /**
     * �J������"�^�C�g��"
     */
    private static final String COLUMN_TITLE = "title";
    /**
     * �J������"�{��"
     */
    private static final String COLUMN_BODY = "body";
    // CHECKSTYLE:OFF eclipse�̃C���f���g�̎������`�ŋ󔒂��}������Ă��܂���
    /**
     * ���R�[�h���o�Ώۂ̃J����.
     */
    private static final String[] COLUMNS = { COLUMN_ROWID, COLUMN_TITLE, COLUMN_BODY };
    // CHECKSTYLE:ON
    /**
     * ��L�[���g���ă��R�[�h��I���A�폜����ۂ̏���.
     */
    private static final String SELECTION_ROWID = COLUMN_ROWID + " = ?";
    /**
     * �f�[�^�x�[�X���쐬����SQL
     */
    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ROWID + " integer primary key autoincrement, " + COLUMN_TITLE + " text not null, " + COLUMN_BODY + " text not null);";
    /**
     * �f�[�^�x�[�X�w���p�[�̃C���X�^���X�B
     */
    private DatabaseHelper dbHelper;
    /**
     * �f�[�^�x�[�X�̃C���X�^���X�B
     */
    private SQLiteDatabase db;
    /**
     * �R���e�L�X�g.
     */
    private final Context context;


    /**
     * �m�[�g�p�b�h�A�v���Ŏg�p����f�[�^�x�[�X�̃w���p�[.
     * ��Ƀf�[�^�x�[�X�̍쐬�ƃA�b�v�O���[�h���s��.
     * @author moonstruckdrops
     *
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * �R���X�g���N�^�𐶐�����.
         * @param context �R���e�L�X�g
         */
        public DatabaseHelper(Context context) {
            super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        }

        /**
         * �f�[�^�x�[�X�̃e�[�u�����쐬����.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Create database : " + DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        /**
         * �f�[�^�x�[�X�̃e�[�u�����A�b�v�O���[�h����.
         * �����ł͈�x�e�[�u�����폜���A�ēx�e�[�u�����쐬���Ă���.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }


    /**
     * �R���X�g���N�^�̐���������.
     * ���̂Ƃ�dbHelper�ϐ���null�̏ꍇ�ɂ�db�ϐ��������ɏ�����������.
     * �����db�ϐ����f�[�^�x�[�X�ւ̏������ݏ�Ԃ��擾����̂ɁAdbHelper�ϐ��Ɉˑ����Ă���ׂł���.
     * @param context �R���e�L�X�g
     */
    public NotepadDao(Context context) {
        this.context = context.getApplicationContext();
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(this.context);
            db = dbHelper.getWritableDatabase();
        }
    }

    /**
     * �f�[�^�x�[�X�Ƀm�[�g�p�b�h�̓��e��V�K�ɑ}������.
     * �}���������ʐ������}�����ꂽ�ꍇ�͎�L�[��Ԃ����A
     * �������}������Ȃ������ꍇ��-1�̒l��Ԃ�.
     * @param notepadDto �m�[�g�p�b�h�̓��e
     * @return �f�[�^�x�[�X�ւ̑}�����ʂ�Ԃ��B
     */
    public long insert(NotepadDto notepadDto) {
        long resultCode = -1;
        //�����̒l��null�̎��ُ͈��ԂȂ̂�-1��Ԃ�.
        if (notepadDto == null) {
            Log.w(TAG, "insert(NotepadDto notepadDto) : notepadDto is null.");
            return resultCode;
        }
        ContentValues values = createContentValues(notepadDto);
        try {
            //�g�����U�N�V�������J�n����
            db.beginTransaction();
            resultCode = db.insert(TABLE_NAME, null, values);
            //���݂̃g�����U�N�V�����̐����������Ƃ�����
            db.setTransactionSuccessful();
            return resultCode;
        } finally {
            //�g�����U�N�V�������I������.
            db.endTransaction();
        }
    }

    /**
     * �f�[�^�x�[�X�ɉi��������Ă���m�[�g�p�b�h�̓��e���X�V����.
     * �X�Vm�ɐ��������Ƃ��ɂ�true��Ԃ����A����ȊO�̏ꍇ��false��Ԃ�.
     * @param notepadDto �m�[�g�p�b�h�̓��e
     * @return �X�V�ɐ��������Ƃ�true,���s�����ꍇ��false��Ԃ�.
     */
    public boolean update(NotepadDto notepadDto) {
        //������null�̎��ُ͈��ԂȂ̂�false��Ԃ�
        if (notepadDto == null) {
            Log.w(TAG, "update(NotepadDto notepadDto) : notepadDto is null.");
            return false;
        }
        ContentValues values = createContentValues(notepadDto);
        try {
            db.beginTransaction();
            // CHECKSTYLE:OFF eclipse�̃C���f���g�̎������`�ŋ󔒂��}������Ă��܂���
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
     * �f�[�^�x�[�X�ɉi��������Ă���m�[�g�p�b�h�̓��e���폜����.
     * �폜������e�͈����Ŏw�肵����L�[�̂��̂��Ώۂł���.
     * @param rowId �폜�Ώۃm�[�g�p�b�h�̓��e�̎�L�[�̒l
     * @return �폜�ɐ��������ꍇtrue�A����ȊO�̏ꍇ��false��Ԃ�.
     */
    public boolean delete(long rowId) {
        //������null�̎��ُ͈��ԂȂ̂�false��Ԃ�
        if (Long.valueOf(rowId) == null) {
            Log.w(TAG, "delete(long rowId) : rowId is illegal number.");
            return false;
        }
        try {
            db.beginTransaction();
            // CHECKSTYLE:OFF eclipse�̃C���f���g�̎������`�ŋ󔒂��}������Ă��܂���
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
     * �f�[�^�x�[�X����S�Ẵ��R�[�h���擾����.
     * @return �f�[�^�x�[�X�̃��R�[�h�̑S�Ẵm�[�g�p�b�h�̓��e
     */
    public List<NotepadDto> findAll() {
        List<NotepadDto> noteList = new ArrayList<NotepadDto>();
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null, null);
        try {
            // 1����������Ȃ������Ƃ��̓f�[�^�x�[�X�ɉ������R�[�h�������̂�null��Ԃ�
            if (cursor.getCount() < 1) {
                Log.i(TAG, "findAll() : multiple records found.");
                return null;
            }
            // ���R�[�h�̌������A���R�[�h�̓��e��ǂݏo��
            while (cursor.moveToNext()) {
                noteList.add(createNotepadDto(cursor));
            }
            return noteList;
        } finally {
            cursor.close();
        }
    }

    /**
     * �f�[�^�x�[�X��������Ŏw�肵���l(��L�[)�̃��R�[�h���擾����.
     * @param rowId ��L�[�̒l
     * @return �����Ŏw�肵����L�[�̒l�̃��R�[�h
     */
    public NotepadDto findByRowId(long rowId) {
        List<NotepadDto> noteList = new ArrayList<NotepadDto>();
        // CHECKSTYLE:OFF eclipse�̃C���f���g�̎������`�ŋ󔒂��}������Ă��܂���
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
            //�����l�����邱�Ƃ͂��蓾�Ȃ����O�ׂ̈Ƀ��X�g�̐擪�Ɋi�[���ꂽ�l��ԋp����
            return noteList.get(0);
        } finally {
            cursor.close();
        }
    }

    /**
     * �f�[�^�x�[�X���N���[�Y����.
     */
    public void closeDb() {
        db.close();
        dbHelper.close();
    }

    /**
     * �����Ŏw�肵��Cursor����A�v���P�[�V�����Ŏg�p�\��Dto�ɕϊ�����.
     * �ϊ������l��NotepadDto�ł���.
     * @param cursor ���݂̃��R�[�h���w������cursor
     * @return �ϊ����ʂł���NotePadDto
     */
    private NotepadDto createNotepadDto(Cursor cursor) {
        NotepadDto notepadDto = new NotepadDto();
        // CHECKSTYLE:OFF cursor�̃J�����w�肪�}�W�b�N�i���o�[�Ƃ��Čx������邽�߁ACHECKSTYLE���O���B
        notepadDto.setRowId(cursor.getLong(0));
        notepadDto.setTitle(cursor.getString(1));
        notepadDto.setBody(cursor.getString(2));
        // CHECKSTYLE:ON
        return notepadDto;
    }

    /**
     * �����Ŏw�肵��Dto��Android�̃f�[�^�x�[�X�Ŏg����悤�ɕϊ�����.
     * �ϊ������l��insert��update�Ɏg�p����ContentValues�ł���.
     * @param notepadDto �m�[�g�p�b�h�̓��e
     * @return Dto����ϊ�����ContentValues
     */
    private ContentValues createContentValues(NotepadDto notepadDto) {
        ContentValues values = new ContentValues();
        //��L�[���������ꍇ�͍X�V�Ȃ̂ŕϊ�������.
        if (notepadDto.getRowId() > 0) {
            values.put(COLUMN_ROWID, notepadDto.getRowId());
        }
        values.put(COLUMN_TITLE, notepadDto.getTitle());
        values.put(COLUMN_BODY, notepadDto.getBody());
        return values;

    }
}
