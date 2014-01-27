package jp.co.moonstruckdrops.android.notepad;

import java.util.List;

import jp.co.moonstruckdrops.android.notepad.dao.NotepadDao;
import jp.co.moonstruckdrops.android.notepad.dto.NotepadDto;
import jp.co.moonstruckdrops.android.notepad.util.NotepadUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * �ۑ�����Ă���m�[�g�̈ꗗ��\������.
 * @author moonstruckdrops
 *
 */
public class NoteList extends Activity {

    /**
     * ���O�ɏo�͂���N���X��.
     */
    private static final String TAG = "NoteList";
    /**
     * �\�����e�ɂ���đ傫����ω�������p�����[�^.
     */
    private static final int LAYOUT_PARAM_WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    /**
     * ���ƍ�������ʕ\�������ς��ɂ���p�����[�^.
     */
    private static final int LAYOUT_PARAM_FILL_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    /**
     * ��ʂɕ\������r���[�𔻕ʂ���ׂ�index�l.
     */
    private static final int INDEX_VIEW = 0;
    /**
     * �m�[�g�̈ꗗ��\������ׂ̏��ԂɎg�p����C���f�b�N�X.
     */
    private static final int NOTE_LIST_INDEX = 0;
    /**
     * �I�v�V�������j���[�Łu�m�[�g�p�b�h��ǉ�����v��\���A�C�e��ID.
     */
    private static final int MENU_ADD_NOTE = 0x0fde1234;
    /**
     * �I�v�V�������j���[�Łu�m�[�g�p�b�h���폜����v��\���A�C�e��ID.
     */
    private static final int MENU_DELETE_NOTE = 0x0fde1235;
    /**
     * �m�[�g��ҏW����ׂ̃��N�G�X�g�R�[�h
     */
    private static final int REQUEST_CODE_NOTE_EDIT = 0x0fde2345;
    /**
     * �^�C�g����\������ׂ̃p�f�B���O��(�P��dip)
     */
    private static final int TITLE_PADDING_SIZE = 6;
    /**
     * �R���e�L�X�g.
     */
    private Context context;
    /**
     * ��ʂ̃��C�A�E�g.
     */
    private LinearLayout layout;
    /**
     * DB�ɃA�N�Z�X����DAO.
     */
    private NotepadDao notepadDao;


    /**
     * �A�v���P�[�V�������N�������Ƃ��ɌĂ΂�郁�\�b�h�ł���.
     * �ȉ��̓��e���s��.
     * <ul>
     * <li>�ϐ��̏�����</li>
     * <li>��ʂ̃��C�A�E�g���쐬</li>
     * </ul>
     * @param savedInstanceState {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "NoteListActivity State create.");
        // �ϐ��̏��������s��.
        context = getApplicationContext();
        // ��ʂ̃��C�A�E�g���쐬����.
        layout = new LinearLayout(context);
        // ���C�A�E�g�̑傫����WRAP_CONTENT�ɐݒ肷��.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        layout.setVerticalScrollBarEnabled(true);
        setContentView(layout);
    }

    /**
     * ���݂̏�Ԃ����O�ɏo�͂���.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "NoteListActivity State start.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "NoteListActivity State resume.");
        createView();
    }

    /**
     * ���݂̏�Ԃ����O�ɏo�͂���.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "NoteListActivity State pause.");
    }

    /**
     * ���݂̏�Ԃ����O�ɏo�͂���.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "NoteListActivity State stop.");
    }

    /**
     * ���݂̏�Ԃ����O�ɏo�͂���.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "NoteListActivity State restart.");
    }

    /**
     * ���݂̏�Ԃ����O�ɏo�͂���.
     * ���̃��\�b�h���Ă΂ꂽ�Ƃ��ɂ̓A�v���P�[�V�������I������.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "NoteListActivity State destory.");
        Log.i(TAG, "NoteListActivity finish.");
        finish();
    }

    /**
     * �I�v�V�������j���[�����߂ČĂяo���ꂽ�Ƃ��Ɏ��s����.
     * �I�v�V�������j���[�ɂ͈����̏��Ɉȉ��̐ݒ���s���Ă���(�����Ɏw�肵�Ă���).
     * <ol>
     * <li>�I�v�V�������j���[�̃O���[�vID��0</li>
     * <li>�I�v�V�������j���[�̃A�C�e��ID�͋�ʂł���悤�ɑ��ϐ����g�p���Ă���</li>
     * <li>�I�v�V�������j���[�̕\�����͒ǉ��Ȃ̂�0</li>
     * <li>�I�v�V�������j���[�ɕ\�����镶����strings.xml�ɋL�q�������e</li>
     * </ol>
     * @param menu ���j���[
     * @return �I�v�V�������j���[��\������ꍇ��true���A�\�����Ȃ��ꍇ��false��Ԃ�.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_ADD_NOTE, Menu.NONE, getString(R.string.menu_insert));
        menu.add(Menu.NONE, MENU_DELETE_NOTE, Menu.NONE, getString(R.string.menu_delete));
        return true;
    }

    /**
     * �I�v�V�������j���[��\������.
     * true��Ԃ��Ă���̂�super�N���X�ł͖����A
     * ���̃N���X�Ŏ����������j���[��\����������ł���.
     * @param menu �\�����郁�j���[
     * @return �I�v�V�������j���[��\������̂�true��Ԃ�.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * �I�v�V�������j���[��I�������Ƃ��̓�����`����.
     * �ǉ��̏ꍇ�A�m�[�g�ҏW��ʂɑJ�ڂ���.
     * �폜�̏ꍇ�A�폜����_�C�A���O��\������.
     * @param item �I�����ꂽ����.
     * @return �I�v�V�������j���[�̓��e�����s�����̂�true��Ԃ�.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD_NOTE:
                Intent intent = new Intent(context, NoteEdit.class);
                startActivityForResult(intent, REQUEST_CODE_NOTE_EDIT);
                break;
            case MENU_DELETE_NOTE:
                showDeleteDialog();
                break;
            default:
                NotepadUtils.showDirection(context, R.string.toast_error_message);
                onDestroy();
                break;
        }
        return true;
    }

    /**
     * �폜�Ώۂ�I������_�C�A���O��\������.
     * �_�C�A���O�őI�������m�[�g���폜����.
     * �폜��ɉ�ʂ��X�V����.����̓_�C�A���O�\�����ɂ̓A�N�e�B�r�e�B��onPause��ԂɂȂ��Ă���
     * �_�C�A���O��������Ƃ̓A�N�e�B�r�e�B�̏�Ԃ�onResume�ɂȂ邩��ł���.
     */
    private void showDeleteDialog() {
        notepadDao = new NotepadDao(context);
        final List<NotepadDto> noteList = notepadDao.findAll();
        String[] items = createTitleList(noteList);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.menu_delete);
        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                long rowId = noteList.get(which).getRowId();
                notepadDao.delete(rowId);
                notepadDao.closeDb();
                restoreView();
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        restoreView();
    }

    /**
     * ��ʂ̍X�V���s��.
     * ��ʂ̃��C�A�E�g����S�Ẵr���[����菜��.
     * �m�[�g�ꗗ��\������.
     */
    private void restoreView() {
        layout.removeViewAt(INDEX_VIEW);
        createView();
    }

    /**
     * ���݁A�i�����ς݂̃m�[�g�ꗗ��\������.
     */
    private void createView() {
        notepadDao = new NotepadDao(context);
        // DB�Ɋi�[����Ă���m�[�g�p�b�h�̓��e��S���擾����.
        final List<NotepadDto> noteList = notepadDao.findAll();
        // �擾�����������m�F����.
        if (noteList == null) {
            // �m�[�g�̓��e���P���������̂Ńe�L�X�g�r���[���쐬����.
            TextView textView = new TextView(context);
            LayoutParams textViewparams = new LayoutParams(LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WRAP_CONTENT);
            textView.setLayoutParams(textViewparams);
            //��ʂɁuNo Notes!�v�ƕ\������.
            textView.setText(getString(R.string.no_notes));
            layout.addView(textView, INDEX_VIEW);
        } else {
            // �^�C�g���̈ꗗ��\�����郌�C�A�E�g���쐬����
            LinearLayout titleLayout = createListView(noteList);

            // �^�C�g���̕\������ʂ����ς��ɂȂ��Ă��X�N���[���\�Ȃ悤�ɃX�N���[���r���[���쐬����
            ScrollView scrollView = new ScrollView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT);
            scrollView.setLayoutParams(layoutParams);
            scrollView.addView(titleLayout, INDEX_VIEW);

            // �X�N���[���r���[����ʂɕ\������
            layout.addView(scrollView, INDEX_VIEW);
        }
        notepadDao.closeDb();
    }

    /**
     * ���X�g�r���[�Ɠ�����ʂ��쐬����.
     * �����̒l����m�[�g�̈ꗗ��\������.
     * �B���ꂽxml��`�ł���android.R.layout.simple_list_item_1�͈ȉ��̃\�[�X�R�[�h���Q�l�ɂ��č쐬���Ă���.
     * http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/res/res/layout/simple_list_item_1.xml;h=c9c77a5f9c113a9d331d5e11a6016aaa815ec771;hb=refs/heads/froyo
     * @param noteList �i�������ꂽ�m�[�g�̃��X�g
     * @return ���X�g�r���[�Ƃقړ������e�̉�ʃ��C�A�E�g
     */
    private LinearLayout createListView(final List<NotepadDto> noteList) {

        // �^�C�g����ݒ肷��
        LinearLayout titleLayout = new LinearLayout(context);
        // ���C�A�E�g�̑傫����ݒ肷��.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT);
        titleLayout.setLayoutParams(layoutParams);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        // �m�[�g���e��\������
        for (int i = 0; i < noteList.size(); i++) {
            final NotepadDto note = noteList.get(i);
            // ListView�Ɏg�p����android.R.layout.simple_list_item_1�Ƃقړ����Ȏ������s��
            TextView titleView = new TextView(context);
            LayoutParams titleParam = new LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT);
            titleView.setLayoutParams(titleParam);
            titleView.setGravity(Gravity.CENTER_VERTICAL);
            titleView.setPadding(NotepadUtils.dipToPixel(context, TITLE_PADDING_SIZE), 0, 0, 0);
            // �r���[�̕����̑傫����ݒ肷��
            // ���\�b�h�̈�����android�̃��\�[�XID�w��Ȃ̂ł����͈ˑ�
            titleView.setTextAppearance(context, android.R.attr.textAppearanceLarge);
            titleView.setText(note.getTitle());
            titleView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    // �N���b�N�����^�C�g���̃m�[�g��ҏW�ł���悤�ɂ��Ă���
                    Intent intent = new Intent(context, NoteEdit.class);
                    intent.putExtra(NotepadUtils.NAME_ROW_ID, note.getRowId());
                    startActivityForResult(intent, REQUEST_CODE_NOTE_EDIT);
                }
            });
            titleLayout.addView(titleView, NOTE_LIST_INDEX + i);
        }
        return titleLayout;
    }

    /**
     * �r���[�ɕ\������^�C�g�����쐬����.
     * @param noteList �i��������Ă���m�[�g
     * @return �ۑ�����Ă���m�[�g�p�b�h�̃^�C�g���ꗗ
     */
    private String[] createTitleList(final List<NotepadDto> noteList) {
        String[] titleList = new String[noteList.size()];
        for (int i = 0; i < noteList.size(); i++) {
            titleList[i] = noteList.get(i).getTitle();
        }
        return titleList;
    }
}
