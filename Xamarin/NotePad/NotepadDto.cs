using System;

namespace NotePad
{
	public class NotepadDto
	{
		private long rowId;
		private String title;
		private String body;

		public NotepadDto ()
		{
		}

		public long RowId {
			get{ return this.rowId; }
			set{ this.rowId = value; }
		}

		public String Title {
			set { title = value; }
			get { return title; }
		}

		public String Body {
			get { return body; }
			set { body = value; }
		}
	}
}
