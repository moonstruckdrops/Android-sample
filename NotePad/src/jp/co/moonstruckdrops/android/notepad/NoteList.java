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
 * 保存されているノートの一覧を表示する.
 * @author moonstruckdrops
 *
 */
public class NoteList extends Activity {

    /**
     * ログに出力するクラス名.
     */
    private static final String TAG = "NoteList";
    /**
     * 表示内容によって大きさを変化させるパラメータ.
     */
    private static final int LAYOUT_PARAM_WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    /**
     * 幅と高さを画面表示いっぱいにするパラメータ.
     */
    private static final int LAYOUT_PARAM_FILL_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    /**
     * 画面に表示するビューを判別する為のindex値.
     */
    private static final int INDEX_VIEW = 0;
    /**
     * ノートの一覧を表示する為の順番に使用するインデックス.
     */
    private static final int NOTE_LIST_INDEX = 0;
    /**
     * オプションメニューで「ノートパッドを追加する」を表すアイテムID.
     */
    private static final int MENU_ADD_NOTE = 0x0fde1234;
    /**
     * オプションメニューで「ノートパッドを削除する」を表すアイテムID.
     */
    private static final int MENU_DELETE_NOTE = 0x0fde1235;
    /**
     * ノートを編集する為のリクエストコード
     */
    private static final int REQUEST_CODE_NOTE_EDIT = 0x0fde2345;
    /**
     * タイトルを表示する為のパディング幅(単位dip)
     */
    private static final int TITLE_PADDING_SIZE = 6;
    /**
     * コンテキスト.
     */
    private Context context;
    /**
     * 画面のレイアウト.
     */
    private LinearLayout layout;
    /**
     * DBにアクセスするDAO.
     */
    private NotepadDao notepadDao;


    /**
     * アプリケーションが起動したときに呼ばれるメソッドである.
     * 以下の内容を行う.
     * <ul>
     * <li>変数の初期化</li>
     * <li>画面のレイアウトを作成</li>
     * </ul>
     * @param savedInstanceState {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "NoteListActivity State create.");
        // 変数の初期化を行う.
        context = getApplicationContext();
        // 画面のレイアウトを作成する.
        layout = new LinearLayout(context);
        // レイアウトの大きさをWRAP_CONTENTに設定する.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);
        layout.setVerticalScrollBarEnabled(true);
        setContentView(layout);
    }

    /**
     * 現在の状態をログに出力する.
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
     * 現在の状態をログに出力する.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "NoteListActivity State pause.");
    }

    /**
     * 現在の状態をログに出力する.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "NoteListActivity State stop.");
    }

    /**
     * 現在の状態をログに出力する.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "NoteListActivity State restart.");
    }

    /**
     * 現在の状態をログに出力する.
     * このメソッドが呼ばれたときにはアプリケーションを終了する.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "NoteListActivity State destory.");
        Log.i(TAG, "NoteListActivity finish.");
        finish();
    }

    /**
     * オプションメニューが初めて呼び出されたときに実行する.
     * オプションメニューには引数の順に以下の設定を行っている(引数に指定している).
     * <ol>
     * <li>オプションメニューのグループIDは0</li>
     * <li>オプションメニューのアイテムIDは区別できるように大域変数を使用している</li>
     * <li>オプションメニューの表示順は追加なので0</li>
     * <li>オプションメニューに表示する文言はstrings.xmlに記述した内容</li>
     * </ol>
     * @param menu メニュー
     * @return オプションメニューを表示する場合はtrueを、表示しない場合はfalseを返す.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_ADD_NOTE, Menu.NONE, getString(R.string.menu_insert));
        menu.add(Menu.NONE, MENU_DELETE_NOTE, Menu.NONE, getString(R.string.menu_delete));
        return true;
    }

    /**
     * オプションメニューを表示する.
     * trueを返しているのはsuperクラスでは無く、
     * このクラスで実装したメニューを表示したからである.
     * @param menu 表示するメニュー
     * @return オプションメニューを表示するのでtrueを返す.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * オプションメニューを選択したときの動作を定義する.
     * 追加の場合、ノート編集画面に遷移する.
     * 削除の場合、削除するダイアログを表示する.
     * @param item 選択された項目.
     * @return オプションメニューの内容を実行したのでtrueを返す.
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
     * 削除対象を選択するダイアログを表示する.
     * ダイアログで選択したノートを削除する.
     * 削除後に画面を更新する.これはダイアログ表示中にはアクティビティがonPause状態になっており
     * ダイアログを閉じたあとはアクティビティの状態がonResumeになるからである.
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
     * 画面の更新を行う.
     * 画面のレイアウトから全てのビューを取り除く.
     * ノート一覧を表示する.
     */
    private void restoreView() {
        layout.removeViewAt(INDEX_VIEW);
        createView();
    }

    /**
     * 現在、永続化済みのノート一覧を表示する.
     */
    private void createView() {
        notepadDao = new NotepadDao(context);
        // DBに格納されているノートパッドの内容を全件取得する.
        final List<NotepadDto> noteList = notepadDao.findAll();
        // 取得した件数を確認する.
        if (noteList == null) {
            // ノートの内容が１件も無いのでテキストビューを作成する.
            TextView textView = new TextView(context);
            LayoutParams textViewparams = new LayoutParams(LAYOUT_PARAM_WRAP_CONTENT, LAYOUT_PARAM_WRAP_CONTENT);
            textView.setLayoutParams(textViewparams);
            //画面に「No Notes!」と表示する.
            textView.setText(getString(R.string.no_notes));
            layout.addView(textView, INDEX_VIEW);
        } else {
            // タイトルの一覧を表示するレイアウトを作成する
            LinearLayout titleLayout = createListView(noteList);

            // タイトルの表示が画面いっぱいになってもスクロール可能なようにスクロールビューを作成する
            ScrollView scrollView = new ScrollView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT);
            scrollView.setLayoutParams(layoutParams);
            scrollView.addView(titleLayout, INDEX_VIEW);

            // スクロールビューを画面に表示する
            layout.addView(scrollView, INDEX_VIEW);
        }
        notepadDao.closeDb();
    }

    /**
     * リストビューと同じ画面を作成する.
     * 引数の値からノートの一覧を表示する.
     * 隠されたxml定義であるandroid.R.layout.simple_list_item_1は以下のソースコードを参考にして作成している.
     * http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/res/res/layout/simple_list_item_1.xml;h=c9c77a5f9c113a9d331d5e11a6016aaa815ec771;hb=refs/heads/froyo
     * @param noteList 永続化されたノートのリスト
     * @return リストビューとほぼ同じ内容の画面レイアウト
     */
    private LinearLayout createListView(final List<NotepadDto> noteList) {

        // タイトルを設定する
        LinearLayout titleLayout = new LinearLayout(context);
        // レイアウトの大きさを設定する.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT);
        titleLayout.setLayoutParams(layoutParams);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        // ノート内容を表示する
        for (int i = 0; i < noteList.size(); i++) {
            final NotepadDto note = noteList.get(i);
            // ListViewに使用するandroid.R.layout.simple_list_item_1とほぼ等価な実装を行う
            TextView titleView = new TextView(context);
            LayoutParams titleParam = new LayoutParams(LAYOUT_PARAM_FILL_PARENT, LAYOUT_PARAM_WRAP_CONTENT);
            titleView.setLayoutParams(titleParam);
            titleView.setGravity(Gravity.CENTER_VERTICAL);
            titleView.setPadding(NotepadUtils.dipToPixel(context, TITLE_PADDING_SIZE), 0, 0, 0);
            // ビューの文字の大きさを設定する
            // メソッドの引数がandroidのリソースID指定なのでここは依存
            titleView.setTextAppearance(context, android.R.attr.textAppearanceLarge);
            titleView.setText(note.getTitle());
            titleView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    // クリックしたタイトルのノートを編集できるようにしている
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
     * ビューに表示するタイトルを作成する.
     * @param noteList 永続化されているノート
     * @return 保存されているノートパッドのタイトル一覧
     */
    private String[] createTitleList(final List<NotepadDto> noteList) {
        String[] titleList = new String[noteList.size()];
        for (int i = 0; i < noteList.size(); i++) {
            titleList[i] = noteList.get(i).getTitle();
        }
        return titleList;
    }
}
