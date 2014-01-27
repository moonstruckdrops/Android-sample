package jp.co.moonstruckdrops.android.notepad.util;

import static android.content.Context.WINDOW_SERVICE;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * �A�v���P�[�V�����S�̂ŋ��L������e���W�߂��N���X.
 * @author moonstruckdrops
 *
 */
public class NotepadUtils {

    /**
     * �r���[���X�V����ׂ̃L�[.
     */
    public static final String NAME_RESTORE_VIEW = "RESTORE_VIEW";
    /**
     * �r���[���X�V����ׂ̒l.
     */
    public static final int VALUE_RESTORE_VIEW = 1;
    /**
     * ��L�[�����L����ׂ̒l.
     */
    public static final String NAME_ROW_ID = "KEY_ROW_ID";
    /**
     * ���̃f�o�C�X�̃s�N�Z�����x.
     */
    // CHECKSTYLE:OFF
    private static float density = 0.0f;


    // CHECKSTYLE:ON

    /**
     * ���[�e�B���e�B�N���X�Ȃ̂ŃC���X�^���X�𐶐����邱�Ƃ͂ł��Ȃ�.
     */
    private NotepadUtils() {

    }

    /**
     * ���b�Z�[�W��Toast�ŉ�ʏ㕔�ɕ\������.
     * @param context �R���e�L�X�g
     * @param messageId ���b�Z�[�W�������\��resource ID
     */
    public static void showDirection(Context context, int messageId) {
        Toast toast = Toast.makeText(context, messageId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    /**
     * DIP�P�ʂ��s�N�Z���P�ʂɊ��Z����.
     * @param context �R���e�L�X�g
     * @param dips DIP�P�ʂ̗�
     * @return ���Z�����s�N�Z���l
     */
    public static int dipToPixel(Context context, int dips) {
        return (int) (dips * getDisplayDensity(context));
    }

    /**
     * ���̃f�o�C�X�̃s�N�Z�����x���A160dpi�Ƃ̔䗦�ŕԂ�.
     * @param context �����擾���悤�Ƃ��Ă���Context
     * @return ���̃f�o�C�X�̃s�N�Z�����x
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
