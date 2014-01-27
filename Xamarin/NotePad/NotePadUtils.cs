using System;
using Android.Runtime;
using Android.Content;
using Android.Util;
using Android.Views;
using Android.Widget;

namespace NotePad
{
	public class NotePadUtils
	{
		// View
		public const String NAME_RESTORE_VIEW = "RESTORE_VIEW";
		public const int VALUE_RESTORE_VIEW = 1;
		// DB
		public const String NAME_ROW_ID = "KEY_ROW_ID";

		private NotePadUtils ()
		{
		}
		// メッセージをToastで画面上部に表示する.
		public static void ShowDirection (Context context, int resId)
		{
			var mToast = Toast.MakeText (context, resId, ToastLength.Long);
			mToast.SetGravity (GravityFlags.Top, 0, 0);
			mToast.Show ();
		}
		// DIP単位をピクセル単位に換算する.
		public static int DipToPixel (Context context, int dips)
		{
			return (int)(dips * GetDisplayDensity (context));
		}
		// このデバイスのピクセル密度を、160dpiとの比率で返す.
		public static float GetDisplayDensity (Context context)
		{
			if (context == null) {
				// TODO : 引数がnullってことで代替した.
				throw new ArgumentNullException ();
			}
			var mDm = new DisplayMetrics ();
			var mWm = context.GetSystemService (Context.WindowService).JavaCast<IWindowManager> ();
			mWm.DefaultDisplay.GetMetrics (mDm);
			return mDm.Density;
		}
	}
}
