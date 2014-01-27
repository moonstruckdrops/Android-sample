package jp.co.moonstruckdrops.android.notepad.util;

import static android.content.Context.WINDOW_SERVICE;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * アプリケーション全体で共有する内容を集めたクラス.
 * @author moonstruckdrops
 *
 */
public class NotepadUtils {

    /**
     * ビューを更新する為のキー.
     */
    public static final String NAME_RESTORE_VIEW = "RESTORE_VIEW";
    /**
     * ビューを更新する為の値.
     */
    public static final int VALUE_RESTORE_VIEW = 1;
    /**
     * 主キーを共有する為の値.
     */
    public static final String NAME_ROW_ID = "KEY_ROW_ID";
    /**
     * このデバイスのピクセル密度.
     */
    // CHECKSTYLE:OFF
    private static float density = 0.0f;


    // CHECKSTYLE:ON

    /**
     * ユーティリティクラスなのでインスタンスを生成することはできない.
     */
    private NotepadUtils() {

    }

    /**
     * メッセージをToastで画面上部に表示する.
     * @param context コンテキスト
     * @param messageId メッセージ文字列を表すresource ID
     */
    public static void showDirection(Context context, int messageId) {
        Toast toast = Toast.makeText(context, messageId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    /**
     * DIP単位をピクセル単位に換算する.
     * @param context コンテキスト
     * @param dips DIP単位の量
     * @return 換算したピクセル値
     */
    public static int dipToPixel(Context context, int dips) {
        return (int) (dips * getDisplayDensity(context));
    }

    /**
     * このデバイスのピクセル密度を、160dpiとの比率で返す.
     * @param context 情報を取得しようとしているContext
     * @return このデバイスのピクセル密度
     */
    public static float getDisplayDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        if (density == 0.0f) {
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            density = dm.density;
        }
        return density;
    }
}
