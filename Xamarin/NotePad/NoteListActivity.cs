using System;
using System.Collections.Generic;
using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;
using Android.Util;

namespace NotePad
{
	// delegate
	delegate void RestoreView ();
	[Activity (Label = "NotePad", MainLauncher = true)]
	public class NoteListActivity : Activity
	{
		// log
		private const String TAG = "NoteList";
		// menu
		private const int MENU_ADD_NOTE = 0x0fde1234;
		private const int MENU_DELETE_NOTE = 0x0fde1235;
		// View
		private const int INDEX_VIEW = 0;
		private const int TITLE_PADDING_SIZE = 6;
		private ScrollView scrollView;
		// intent
		private const int REQUEST_CODE_NOTE_EDIT = 0x0fde2345;

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.NoteList);
			Initialize ();
		}

		protected override void OnResume ()
		{
			base.OnResume ();
			Log.Info (TAG, "NoteListActivity State resume.");
			CreateView ();
		}

		protected override void OnActivityResult (int requestCode, Result resultCode, Intent data)
		{
			base.OnActivityResult (requestCode, resultCode, data);
			RestoreView ();
		}

		public override bool OnCreateOptionsMenu (IMenu menu)
		{
			menu.Add (Menu.None, MENU_ADD_NOTE, Menu.None, GetString (Resource.String.menu_insert));
			menu.Add (Menu.None, MENU_DELETE_NOTE, Menu.None, GetString (Resource.String.menu_delete));
			return true;
		}

		public override bool OnPrepareOptionsMenu (IMenu menu)
		{
			return true;
		}

		public override bool OnOptionsItemSelected (IMenuItem item)
		{
			switch (item.ItemId) {
			case MENU_ADD_NOTE:
				Intent intent = new Intent (this, typeof(NoteEdit));
				this.StartActivityForResult (intent, REQUEST_CODE_NOTE_EDIT);
				break;
			case MENU_DELETE_NOTE:
				ShowDeleteDialog ();
				break;
			default:
				NotePadUtils.ShowDirection (this, Resource.String.toast_error_message);
				break;
			}
			return true;
		}

		void Initialize ()
		{
			scrollView = FindViewById<ScrollView> (Resource.Id.scrollView);
		}

		void RestoreView ()
		{
			scrollView.RemoveViewAt (INDEX_VIEW);
			CreateView ();
		}

		void CreateView ()
		{
			var dao = new NotepadDao (this);
			var noteList = dao.FindAll ();
			if (noteList == null || noteList.Count == 0) {
				var v = LayoutInflater.Inflate (Resource.Layout.NoNoteView, null);
				scrollView.AddView (v, INDEX_VIEW);
			} else {
				var layout = CreateListView (noteList);
				scrollView.AddView (layout, INDEX_VIEW);
			}
		}

		LinearLayout CreateListView (List<NotepadDto> noteList)
		{
			LinearLayout layout = new LinearLayout (this);
			var layoutParams = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FillParent, LinearLayout.LayoutParams.WrapContent);
			layout.LayoutParameters = layoutParams;
			layout.Orientation = Orientation.Vertical;

			var i = 0;
			foreach (var note in noteList) {
				TextView titleView = new TextView (this);
				var param = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.FillParent, ViewGroup.LayoutParams.WrapContent);
				titleView.LayoutParameters = param;
				titleView.Gravity = GravityFlags.CenterVertical;
				titleView.SetPadding (NotePadUtils.DipToPixel (this, TITLE_PADDING_SIZE), 0, 0, 0);
				titleView.SetTextAppearance (this, Android.Resource.Attribute.TextAppearanceLarge);
				titleView.Text = note.Title;
				titleView.Click += delegate {
					var intent = new Intent (this, typeof(NoteEdit));
					intent.PutExtra (NotePadUtils.NAME_ROW_ID, note.RowId);
					StartActivityForResult (intent, REQUEST_CODE_NOTE_EDIT);
				};
				layout.AddView (titleView, i);
				i++;
			}
			return layout;
		}

		void ShowDeleteDialog ()
		{
			var dao = new NotepadDao (this);
			var noteList = dao.FindAll ();
			var items = CreateTitleList (noteList);
			RestoreView restoreMethod = new NotePad.RestoreView (RestoreView);
			AlertDialog.Builder dialog = new AlertDialog.Builder (this);
			dialog.SetTitle (GetString (Resource.String.menu_delete));
			dialog.SetItems (items, new DeleteNoteDialogLister (this, noteList, restoreMethod));
			dialog.Show ();
		}

		String[] CreateTitleList (List<NotepadDto> noteList)
		{
			var max = noteList.Count;
			String[] titleList = new String[max];
			for (var i = 0; i < max; i++) {
				titleList [i] = noteList [i].Title;
			}
			return titleList;
		}

		class DeleteNoteDialogLister : Java.Lang.Object, IDialogInterfaceOnClickListener
		{
			private Context context;
			private List<NotepadDto> list;
			private RestoreView delegateMethod;

			public DeleteNoteDialogLister (Context context, List<NotepadDto> list, RestoreView delegateMethod)
			{
				this.context = context;
				this.list = list;
				this.delegateMethod = delegateMethod;
			}

			public void OnClick (IDialogInterface dialog, int which)
			{
				long rowId = list [which].RowId;
				var dao = new NotepadDao (context);
				dao.Delete (rowId);
				dao.CloseDb ();
				delegateMethod ();
			}
		}
	}
}


