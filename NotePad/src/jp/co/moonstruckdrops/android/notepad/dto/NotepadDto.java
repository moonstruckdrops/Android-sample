package jp.co.moonstruckdrops.android.notepad.dto;

/**
 * �m�[�g�p�b�h�̓��e���i��������N���X.
 * @author moonstruckdrops
 */
public class NotepadDto {
    private long rowId;
    private String title;
    private String body;


    /**
     * �m�[�g�̓��e�̃R���X�g���N�^.
     */
    public NotepadDto() {
    }

    /**
     * �i�������̎�L�[�̒l���擾����.
     * @return ��L�[
     */
    public long getRowId() {
        return rowId;
    }

    /**
     * �i�������̎�L�[��ݒ肷��.
     * @param rowId ��L�[
     */
    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    /**
     * �m�[�g�̃^�C�g�����擾����.
     * @return �m�[�g�̃^�C�g��
     */
    public String getTitle() {
        return title;
    }

    /**
     * �m�[�g�̃^�C�g����ݒ肷��.
     * @param title �m�[�g�̃^�C�g��
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * �m�[�g�̖{�����擾����.
     * @return �m�[�g�̖{��
     */
    public String getBody() {
        return body;
    }

    /**
     * �m�[�g�̖{����ݒ肷��.
     * @param body �{��
     */
    public void setBody(String body) {
        this.body = body;
    }

}
