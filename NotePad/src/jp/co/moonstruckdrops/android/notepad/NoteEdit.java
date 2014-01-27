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
 * ノートパッドの内容を編集する.
 * @author moonstruckdrops
 *
 */
public class NoteEdit extends Activity {

    /**
     * ログに出力するクラス名.
     */
    private static final String TAG = "NoteEdit";
    /**
     * 表示内容によって大きさを変化させるパラメータ.
     */
    private static final int LAYOUT_PARAM_WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    /**
     * 幅と高さを画面表示いっぱいにするパラメータ.
     */
    private static final int LAYOUT_PARAM_FILL_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    /**
     * ビューを縦方向に追加するパラメータ.
     */
    private static final int LAYOUT_PARAM_ORIENTATION_VERTICAL = LinearLayout.VERTICAL;
    /**
     * ビューを縦方向に追加するパラメータ.
     */
    private static final int LAYOUT_PARAM_ORIENTATION_HORIZONTAL = LinearLayout.HORIZONTAL;
    /**
     * ビューを配置する際に占める比率(重み)のパラメータ.
     */
    private static final int LAYOUT_PARAM_WEIGHT = 1;
    /**
     * タイトルを表示するラベルを判別する為のindex値.
     */
    private static final int INDEX_TITLE_LABEL = 0;
    /**
     * タイトルを編集するテキストボックスを判別する為のindex値.
     */
    private static final int INDEX_TITLE_EDIT = 1;
    /**
     * コンテキスト.
     */
    private Context context;
    /**
     * 画面のレイアウト.
     */
    private LinearLayout mainLayout;
    /**
     * 永続化されているノートパッドの内容を取得する為の主キー.
     */
    private long primaryKey;
    /**
     * DBにアクセスするDAO.
     */
    private NotepadDao notepadDao;


    /**
     * 編集画面が選択されたときに呼ばれるメソッドである.
     * <ul>
     * <li>画面のレイアウトを設定する</li>
     * <li>主キーの値を取得する</li>
     * </ul>
     * @param savedInstanceState {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        mainLayout = new LinearLayout(context);
        // レイアウトの大きさをfill_parentに設定する.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_FILL_PARENT);
        mainLayout.setLayoutParams(layoutParams);
        // レイアウトの追加方向をverticalに設定する.
        mainLayout.setOrientation(LAYOUT_PARAM_ORIENTATION_VERTICAL);
        setContentView(mainLayout);
        // 主キーの値をNoteListアクティビティから受け取る
        // nullでは無い場合に主キーの値を代入する.
        // nullの場合は0.
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
        // 主キーに値があるときは内容を表示する
        if (primaryKey > 0) {
            notepadDto = notepadDao.findByRowId(primaryKey);
        }
        //タイトル編集部分を作成する.
        LinearLayout titleLayout = createTitle(notepadDto);
        // 編集内容を取得する為にタイトルを編集するテキストボックスを取得する.
        EditText editTitle = (EditText) titleLayout.getChildAt(INDEX_TITLE_EDIT);

        // 本文を表示するラベルを作成する.
        TextView bodyLabel = createLabel(getString(R.string.body));
        // 本文を編集するテキストエリアを作成する.
        EditText bodyEditText = createBodyEdit(notepadDto);

        // ボタンを作成する
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
     * ボタンを押したときの動作を定義する.
     * 主キーの値が0より大きいとき、つまり既にデータベースに永続化済のときは更新をする.
     * 主キーの値が0のときは、挿入を行う.
     * 永続化した後はこのアクティビティを終了する.
     * @param title ノートパッドのタイトル
     * @param body ノートパッドの本文
     * @return ボタンを押したときのリスナー
     */
    private OnClickListener createCLickListener(final EditText title, final EditText body) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                NotepadDto note = new NotepadDto();
                note.setTitle(title.getText().toString());
                note.setBody(body.getText().toString());
                Log.i(TAG, "primarykey" + primaryKey);
                // 主キーに値があるときは内容の更新なので表示する
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
     * 戻るボタンを押したときの振る舞いを定義する.
     * 
     */
    @Override
    public void onBackPressed() {
        closeNoteEdit(NotepadUtils.NAME_RESTORE_VIEW, NotepadUtils.VALUE_RESTORE_VIEW);
    }

    /**
     * このアクティビティを終了する.
     * データベースが開いていた場合はメモリーリークにつながるのでクローズをする.
     * 引数の値をノートの一覧画面の動作内容に設定する.
     * ノート一覧が面にインテントを送信後、このアクティビティを終了する.
     * @param name ノート一覧画面に渡す値のキー
     * @param value ノート一覧画面に渡す値のキーに対応する値
     */
    private void closeNoteEdit(String name, int value) {
        // データベースが開いていればクローズを行う.
        if (notepadDao != null) {
            notepadDao.closeDb();
        }
        Intent intent = new Intent();
        // インテントにノートパッドの一覧画面の動作内容を設定する.
        intent.putExtra(name, value);
        // メイン画面に返却コードを送信する.
        setResult(RESULT_OK, intent);
        // このアクティビティを終了する.
        finish();
    }

    /**
     * 本文を編集するテキストエリアを作成する.
     * @param notepadDto 永続化されたノートパッド
     * @return テキストエリア
     */
    private EditText createBodyEdit(NotepadDto notepadDto) {
        // 本文を編集する部分を作成する.
        EditText bodyEditText = new EditText(context);
        LayoutParams bodyEditLayoutParams = new LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WEIGHT);
        bodyEditText.setLayoutParams(bodyEditLayoutParams);
        // 入力したテキストがEditTextよりも大きい場合に垂直方向にスクロールバーを表示する(falseの場合はスクロールバーを表示しない)
        bodyEditText.setVerticalScrollBarEnabled(true);
        if (notepadDto != null) {
            bodyEditText.setText(notepadDto.getBody());
        }
        return bodyEditText;
    }

    /**
     * タイトルを編集する部分のレイアウトを作成する.
     * <ol>
     * <li>タイトルを編集する部分全体のレイアウトを作成する</li>
     * <li>ラベル部分である「title」表示部分を作成する</li>
     * <li>タイトル編集部分であるテキストボックスを作成する</li>
     * <li>上記2,3を1で作成したレイアウトに追加する</li>
     * </ol>
     * @param notePad 永続化されたノートパッド
     * @return タイトル編集する部分のレイアウト
     */
    private LinearLayout createTitle(NotepadDto notePad) {
        // タイトルを設定する
        LinearLayout titleLayout = new LinearLayout(context);
        // レイアウトの大きさを設定する.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT);
        titleLayout.setLayoutParams(layoutParams);
        // レイアウトの追加方向をhorizontalに設定する.
        titleLayout.setOrientation(LAYOUT_PARAM_ORIENTATION_HORIZONTAL);

        // タイトルを編集するラベルを表示する.
        TextView titleTextView = createLabel(getString(R.string.title));

        //タイトルを編集するテキストボックスを作成する.
        EditText titleEditText = new EditText(context);
        LayoutParams titleEditLayoutParams = new LayoutParams(LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WEIGHT);
        titleEditText.setLayoutParams(titleEditLayoutParams);
        // 永続化された値が取得できた場合、永続化されたノートパッドのタイトルを設定する.
        if (notePad != null) {
            titleEditText.setText(notePad.getTitle());
        }

        titleLayout.addView(titleTextView, INDEX_TITLE_LABEL);
        titleLayout.addView(titleEditText, INDEX_TITLE_EDIT);
        return titleLayout;
    }

    /**
     * 「title」や「body」を表示するラベル部分を作成する.
     * @param label 表示するラベル
     * @return ラベル表示部分のビュー
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
