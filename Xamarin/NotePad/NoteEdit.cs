using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace NotePad
{
	[Activity (Label = "NoteEdit")]			
	public class NoteEdit : Activity
	{
		// View
		private EditText titleView;
		private EditText bodyView;
		private Button confirmButton;
		// DB
		private NotepadDao notepadDao;
		private const long DEFAULT_PK = 0;
		private long primaryKey;

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentView (Resource.Layout.NoteEdit);
			Initialize ();
		}

		protected override void OnResume ()
		{
			base.OnResume ();
			if (primaryKey != DEFAULT_PK) {
				RestoreData ();
			}
		}

		void Initialize ()
		{
			titleView = FindViewById<EditText> (Resource.Id.title);
			bodyView = FindViewById<EditText> (Resource.Id.body);
			confirmButton = FindViewById<Button> (Resource.Id.confirm);
			confirmButton.Click += delegate {
				SaveNoteAndCloseEdit (NotePadUtils.NAME_RESTORE_VIEW, NotePadUtils.VALUE_RESTORE_VIEW);
			};
			notepadDao = new NotepadDao (this);

			var extras = Intent.Extras;
			if (extras != null) {
				primaryKey = extras.GetLong (NotePadUtils.NAME_ROW_ID);
			} else {
				primaryKey = DEFAULT_PK;
			}
		}

		void RestoreData ()
		{
			var notepadDto = notepadDao.FindByRowId (primaryKey);
			titleView.Text = notepadDto.Title;
			bodyView.Text = notepadDto.Body;
		}

		void SaveNoteAndCloseEdit (String name, int value)
		{
			SaveEdit ();
			CloseEdit (name, value);
		}

		void SaveEdit ()
		{
			var note = new NotepadDto ();
			note.Title = titleView.Text.ToString ();
			note.Body = bodyView.Text.ToString ();

			if (primaryKey == DEFAULT_PK) {
				notepadDao.Insert (note);
				return;
			}
			note.RowId = primaryKey;
			notepadDao.Update (note);
		}

		void CloseEdit (String name, int value)
		{
			if (notepadDao != null) {
				notepadDao.CloseDb ();
			}
			Intent intent = new Intent ();
			intent.PutExtra (name, value);
			SetResult (Result.Ok, intent);
			Finish ();
		}
	}
}

