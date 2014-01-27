package jp.co.moonstruckdrops.android.notepad;

import jp.co.moonstruckdrops.android.notepad.dao.NotepadDao;
import jp.co.moonstruckdrops.android.notepad.dto.NotepadDto;
import jp.co.moonstruckdrops.android.notepad.util.NotepadUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * �m�[�g�p�b�h�̓��e��ҏW����.
 * @author moonstruckdrops
 *
 */
public class NoteEdit extends Activity {

    /**
     * ���O�ɏo�͂���N���X��.
     */
    private static final String TAG = "NoteEdit";
    /**
     * �\�����e�ɂ���đ傫����ω�������p�����[�^.
     */
    private static final int LAYOUT_PARAM_WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    /**
     * ���ƍ�������ʕ\�������ς��ɂ���p�����[�^.
     */
    private static final int LAYOUT_PARAM_FILL_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    /**
     * �r���[���c�����ɒǉ�����p�����[�^.
     */
    private static final int LAYOUT_PARAM_ORIENTATION_VERTICAL = LinearLayout.VERTICAL;
    /**
     * �r���[���c�����ɒǉ�����p�����[�^.
     */
    private static final int LAYOUT_PARAM_ORIENTATION_HORIZONTAL = LinearLayout.HORIZONTAL;
    /**
     * �r���[��z�u����ۂɐ�߂�䗦(�d��)�̃p�����[�^.
     */
    private static final int LAYOUT_PARAM_WEIGHT = 1;
    /**
     * �^�C�g����\�����郉�x���𔻕ʂ���ׂ�index�l.
     */
    private static final int INDEX_TITLE_LABEL = 0;
    /**
     * �^�C�g����ҏW����e�L�X�g�{�b�N�X�𔻕ʂ���ׂ�index�l.
     */
    private static final int INDEX_TITLE_EDIT = 1;
    /**
     * �R���e�L�X�g.
     */
    private Context context;
    /**
     * ��ʂ̃��C�A�E�g.
     */
    private LinearLayout mainLayout;
    /**
     * �i��������Ă���m�[�g�p�b�h�̓��e���擾����ׂ̎�L�[.
     */
    private long primaryKey;
    /**
     * DB�ɃA�N�Z�X����DAO.
     */
    private NotepadDao notepadDao;


    /**
     * �ҏW��ʂ��I�����ꂽ�Ƃ��ɌĂ΂�郁�\�b�h�ł���.
     * <ul>
     * <li>��ʂ̃��C�A�E�g��ݒ肷��</li>
     * <li>��L�[�̒l���擾����</li>
     * </ul>
     * @param savedInstanceState {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        mainLayout = new LinearLayout(context);
        // ���C�A�E�g�̑傫����fill_parent�ɐݒ肷��.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_FILL_PARENT);
        mainLayout.setLayoutParams(layoutParams);
        // ���C�A�E�g�̒ǉ�������vertical�ɐݒ肷��.
        mainLayout.setOrientation(LAYOUT_PARAM_ORIENTATION_VERTICAL);
        setContentView(mainLayout);
        // ��L�[�̒l��NoteList�A�N�e�B�r�e�B����󂯎��
        // null�ł͖����ꍇ�Ɏ�L�[�̒l��������.
        // null�̏ꍇ��0.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            primaryKey = extras.getLong(NotepadUtils.NAME_ROW_ID);
        } else {
            primaryKey = 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        notepadDao = new NotepadDao(context);
        NotepadDto notepadDto = null;
        // ��L�[�ɒl������Ƃ��͓��e��\������
        if (primaryKey > 0) {
            notepadDto = notepadDao.findByRowId(primaryKey);
        }
        //�^�C�g���ҏW�������쐬����.
        LinearLayout titleLayout = createTitle(notepadDto);
        // �ҏW���e���擾����ׂɃ^�C�g����ҏW����e�L�X�g�{�b�N�X���擾����.
        EditText editTitle = (EditText) titleLayout.getChildAt(INDEX_TITLE_EDIT);

        // �{����\�����郉�x�����쐬����.
        TextView bodyLabel = createLabel(getString(R.string.body));
        // �{����ҏW����e�L�X�g�G���A���쐬����.
        EditText bodyEditText = createBodyEdit(notepadDto);

        // �{�^�����쐬����
        Button confirmButton = new Button(context);
        LayoutParams buttonParam = new LayoutParams(LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WRAP_CONTENT);
        confirmButton.setText(getString(R.string.confirm));
        confirmButton.setOnClickListener(createCLickListener(editTitle, bodyEditText));

        mainLayout.addView(titleLayout);
        mainLayout.addView(bodyLabel);
        mainLayout.addView(bodyEditText);
        mainLayout.addView(confirmButton, buttonParam);
    }

    /**
     * �{�^�����������Ƃ��̓�����`����.
     * ��L�[�̒l��0���傫���Ƃ��A�܂���Ƀf�[�^�x�[�X�ɉi�����ς̂Ƃ��͍X�V������.
     * ��L�[�̒l��0�̂Ƃ��́A�}�����s��.
     * �i����������͂��̃A�N�e�B�r�e�B���I������.
     * @param title �m�[�g�p�b�h�̃^�C�g��
     * @param body �m�[�g�p�b�h�̖{��
     * @return �{�^�����������Ƃ��̃��X�i�[
     */
    private OnClickListener createCLickListener(final EditText title, final EditText body) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                NotepadDto note = new NotepadDto();
                note.setTitle(title.getText().toString());
                note.setBody(body.getText().toString());
                Log.i(TAG, "primarykey" + primaryKey);
                // ��L�[�ɒl������Ƃ��͓��e�̍X�V�Ȃ̂ŕ\������
                if (primaryKey > 0) {
                    note.setRowId(primaryKey);
                    notepadDao.update(note);
                } else {
                    notepadDao.insert(note);
                }
                closeNoteEdit(NotepadUtils.NAME_RESTORE_VIEW, NotepadUtils.VALUE_RESTORE_VIEW);
            }
        };
    }

    /**
     * �߂�{�^�����������Ƃ��̐U�镑�����`����.
     * 
     */
    @Override
    public void onBackPressed() {
        closeNoteEdit(NotepadUtils.NAME_RESTORE_VIEW, NotepadUtils.VALUE_RESTORE_VIEW);
    }

    /**
     * ���̃A�N�e�B�r�e�B���I������.
     * �f�[�^�x�[�X���J���Ă����ꍇ�̓������[���[�N�ɂȂ���̂ŃN���[�Y������.
     * �����̒l���m�[�g�̈ꗗ��ʂ̓�����e�ɐݒ肷��.
     * �m�[�g�ꗗ���ʂɃC���e���g�𑗐M��A���̃A�N�e�B�r�e�B���I������.
     * @param name �m�[�g�ꗗ��ʂɓn���l�̃L�[
     * @param value �m�[�g�ꗗ��ʂɓn���l�̃L�[�ɑΉ�����l
     */
    private void closeNoteEdit(String name, int value) {
        // �f�[�^�x�[�X���J���Ă���΃N���[�Y���s��.
        if (notepadDao != null) {
            notepadDao.closeDb();
        }
        Intent intent = new Intent();
        // �C���e���g�Ƀm�[�g�p�b�h�̈ꗗ��ʂ̓�����e��ݒ肷��.
        intent.putExtra(name, value);
        // ���C����ʂɕԋp�R�[�h�𑗐M����.
        setResult(RESULT_OK, intent);
        // ���̃A�N�e�B�r�e�B���I������.
        finish();
    }

    /**
     * �{����ҏW����e�L�X�g�G���A���쐬����.
     * @param notepadDto �i�������ꂽ�m�[�g�p�b�h
     * @return �e�L�X�g�G���A
     */
    private EditText createBodyEdit(NotepadDto notepadDto) {
        // �{����ҏW���镔�����쐬����.
        EditText bodyEditText = new EditText(context);
        LayoutParams bodyEditLayoutParams = new LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WEIGHT);
        bodyEditText.setLayoutParams(bodyEditLayoutParams);
        // ���͂����e�L�X�g��EditText�����傫���ꍇ�ɐ��������ɃX�N���[���o�[��\������(false�̏ꍇ�̓X�N���[���o�[��\�����Ȃ�)
        bodyEditText.setVerticalScrollBarEnabled(true);
        if (notepadDto != null) {
            bodyEditText.setText(notepadDto.getBody());
        }
        return bodyEditText;
    }

    /**
     * �^�C�g����ҏW���镔���̃��C�A�E�g���쐬����.
     * <ol>
     * <li>�^�C�g����ҏW���镔���S�̂̃��C�A�E�g���쐬����</li>
     * <li>���x�������ł���utitle�v�\���������쐬����</li>
     * <li>�^�C�g���ҏW�����ł���e�L�X�g�{�b�N�X���쐬����</li>
     * <li>��L2,3��1�ō쐬�������C�A�E�g�ɒǉ�����</li>
     * </ol>
     * @param notePad �i�������ꂽ�m�[�g�p�b�h
     * @return �^�C�g���ҏW���镔���̃��C�A�E�g
     */
    private LinearLayout createTitle(NotepadDto notePad) {
        // �^�C�g����ݒ肷��
        LinearLayout titleLayout = new LinearLayout(context);
        // ���C�A�E�g�̑傫����ݒ肷��.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT);
        titleLayout.setLayoutParams(layoutParams);
        // ���C�A�E�g�̒ǉ�������horizontal�ɐݒ肷��.
        titleLayout.setOrientation(LAYOUT_PARAM_ORIENTATION_HORIZONTAL);

        // �^�C�g����ҏW���郉�x����\������.
        TextView titleTextView = createLabel(getString(R.string.title));

        //�^�C�g����ҏW����e�L�X�g�{�b�N�X���쐬����.
        EditText titleEditText = new EditText(context);
        LayoutParams titleEditLayoutParams = new LayoutParams(LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WEIGHT);
        titleEditText.setLayoutParams(titleEditLayoutParams);
        // �i�������ꂽ�l���擾�ł����ꍇ�A�i�������ꂽ�m�[�g�p�b�h�̃^�C�g����ݒ肷��.
        if (notePad != null) {
            titleEditText.setText(notePad.getTitle());
        }

        titleLayout.addView(titleTextView, INDEX_TITLE_LABEL);
        titleLayout.addView(titleEditText, INDEX_TITLE_EDIT);
        return titleLayout;
    }

    /**
     * �utitle�v��ubody�v��\�����郉�x���������쐬����.
     * @param label �\�����郉�x��
     * @return ���x���\�������̃r���[
     */
    private TextView createLabel(String label) {
        TextView textView = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setText(label);
        return textView;
    }

    //    @Override
    //    protected void onDestroy() {
    //        super.onDestroy();
    //        Log.i(TAG, "NoteEditActivity State destory.");
    //        Log.i(TAG, "NoteEditActivity finish.");
    //        finish();
    //    }
}
