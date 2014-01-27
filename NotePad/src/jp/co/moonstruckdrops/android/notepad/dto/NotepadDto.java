package jp.co.moonstruckdrops.android.notepad.dto;

/**
 * ノートパッドの内容を永続化するクラス.
 * @author moonstruckdrops
 */
public class NotepadDto {
    private long rowId;
    private String title;
    private String body;


    /**
     * ノートの内容のコンストラクタ.
     */
    public NotepadDto() {
    }

    /**
     * 永続化時の主キーの値を取得する.
     * @return 主キー
     */
    public long getRowId() {
        return rowId;
    }

    /**
     * 永続化時の主キーを設定する.
     * @param rowId 主キー
     */
    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    /**
     * ノートのタイトルを取得する.
     * @return ノートのタイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * ノートのタイトルを設定する.
     * @param title ノートのタイトル
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * ノートの本文を取得する.
     * @return ノートの本文
     */
    public String getBody() {
        return body;
    }

    /**
     * ノートの本文を設定する.
     * @param body 本文
     */
    public void setBody(String body) {
        this.body = body;
    }

}
