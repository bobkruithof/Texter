package am.halfpastfour.android.apps.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import am.halfpastfour.texter.R;
import am.halfpastfour.texter.models.Contact;
import am.halfpastfour.texter.models.SMSConversation;

/**
 * Created by bobkruithof on 27/01/15.
 * Project: Texter
 * Package: am.halfpastfour.android.apps.data
 */
public class SMSAdapter extends ArrayAdapter<SMSConversation>
{
	public SMSAdapter( Context context, ArrayList<SMSConversation> smsConversations )
	{
		super( context, 0, smsConversations );
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		// Get the data item for this position
		SMSConversation conversation = getItem( position );

		// Check if an existing view is being reused, otherwise inflate the view
		if( convertView == null ) {
			convertView = LayoutInflater.from( getContext() ).inflate( R.layout.fragment_main, parent, false );
		}

		// Lookup view for data population
		TextView	tvNumber		= (TextView)	convertView.findViewById( R.id.tvNumber );
		TextView	tvMessage		= (TextView) 	convertView.findViewById( R.id.tvMessage );
		ImageView	ivContactImage	= (ImageView)	convertView.findViewById( R.id.image_contact );

		// Get contact name(s) from conversation
		ArrayList<Contact> contacts = conversation.getContacts( getContext() );
		List<String> names	= new ArrayList<>();

		for ( int i = 0; i < contacts.size(); i++ ) {
			String name	= contacts.get( i ).getName();
			if( !names.contains( name ) ) {
				names.add( name );
			}
		}

		tvNumber.setText( am.halfpastfour.android.apps.utils.Strings.join( names, ", " ) );
		tvMessage.setText( conversation.getSnippet() );

//		BitmapDrawable resourceImage	= null;
//
//		if( contacts.size() == 1 ) {
//			resourceImage = (BitmapDrawable) contacts.get( 0 ).getPhotoThumbnailDrawable( getContext() );
//		}
//
//		if( resourceImage == null ) {
//			resourceImage = (BitmapDrawable) getContext().getResources().getDrawable( R.drawable.image_newyork_ribbonstore );
//		}
//
//
//		Bitmap bitmap = ImageHelper.getRoundedCornerBitmap( resourceImage.getBitmap(), 5000 );
//		ivContactImage.setMaxWidth( 150 );
//		ivContactImage.setMaxHeight( 150 );
//
//		ivContactImage.setImageBitmap( bitmap );

		return convertView;
	}
}