package am.halfpastfour.android.apps.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by bobkruithof on 24/06/16.
 * Project: Texter
 * Package: am.halfpastfour.android.apps.utils
 */

public class CacheStore
{
	private HashMap<String, String> cacheMap;
	private HashMap<String, Bitmap> bitmapMap;
	private static       String     TAG            = "CacheStore";
	private static       CacheStore INSTANCE       = null;
	private static final String     cacheDirRoot   = "/Android/data/am.halfpastfour.texter/cache/";
	private static final String     CACHE_FILENAME = ".cache";
	private static       String     cacheDir       = cacheDirRoot;

	/**
	 * Set the cache directory
	 *
	 * @param p_directoryName The name of the directory
	 */
	public static void setCacheDirectory( String p_directoryName )
	{
		cacheDir	= cacheDirRoot + p_directoryName;
		getInstance().initialize();
	}

	@SuppressWarnings( "unchecked" )
	private CacheStore()
	{
		initialize();
	}

	private void initialize()
	{
		cacheMap			= new HashMap<String, String>();
		bitmapMap			= new HashMap<String, Bitmap>();
		String	externalStorageDirectory	= Environment.getExternalStorageDirectory().toString();
		File	fullCacheDir				= new File( externalStorageDirectory, cacheDir );

		if ( !fullCacheDir.exists() ) {
			Log.i( "CACHE", "Directory doesn't exist" );
			cleanCacheStart();
			return;
		}
		try {
			ObjectInputStream inputStream	= new ObjectInputStream( new BufferedInputStream(
				new FileInputStream( new File( fullCacheDir.toString(), CACHE_FILENAME ) )
			) );
			cacheMap	= (HashMap<String, String>) inputStream.readObject();
			inputStream.close();
		} catch ( StreamCorruptedException e ) {
			Log.i( "CACHE", "Corrupted stream" );
			cleanCacheStart();
		} catch ( FileNotFoundException e ) {
			Log.i( "CACHE", "File not found" );
			cleanCacheStart();
		} catch ( IOException e ) {
			Log.i( "CACHE", "Input/Output error" );
			cleanCacheStart();
		} catch ( ClassNotFoundException e ) {
			Log.i( "CACHE", "Class not found" );
			cleanCacheStart();
		}
	}

	private void cleanCacheStart()
	{
		cacheMap = new HashMap<String, String>();
		File fullCacheDir = new File(
			Environment.getExternalStorageDirectory().toString(),
			cacheDir
		);
		fullCacheDir.mkdirs();
		File noMedia = new File( fullCacheDir.toString(), ".nomedia" );
		try {
			noMedia.createNewFile();
			Log.i( "CACHE", "Cache created" );
		} catch ( IOException e ) {
			Log.i( "CACHE", "Couldn't create .nomedia file" );
			e.printStackTrace();
		}
	}

	private synchronized static void createInstance()
	{
		if ( INSTANCE == null ) {
			INSTANCE = new CacheStore();
		}
	}

	public static CacheStore getInstance()
	{
		if ( INSTANCE == null ) createInstance();
		return INSTANCE;
	}

	public void saveCacheFile( String cacheUri, Bitmap image )
	{
		File fullCacheDir = new File(
			Environment.getExternalStorageDirectory().toString(),
			cacheDir
		);
		String fileLocalName = new SimpleDateFormat( "ddMMyyhhmmssSSS" )
			.format( new java.util.Date() ) + ".PNG";
		File             fileUri = new File( fullCacheDir.toString(), fileLocalName );
		FileOutputStream outStream;

		try {
			if ( image != null ) {
				outStream = new FileOutputStream( fileUri );
				image.compress( Bitmap.CompressFormat.PNG, 80, outStream );
				outStream.flush();
				outStream.close();
				cacheMap.put( cacheUri, fileLocalName );
				Log.i(
					"CACHE",
					"Saved file " + cacheUri + " (which is now " + fileUri.toString()
						+ ") correctly"
				);
				bitmapMap.put( cacheUri, image );
				ObjectOutputStream
					os
					= new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream( new
					File(
					fullCacheDir.toString(), CACHE_FILENAME ) ) ) );
				os.writeObject( cacheMap );
				os.close();
			} else {
				Log.d( TAG, "savedCacheFile: image is null" );
			}
		} catch ( FileNotFoundException e ) {
			Log.i( "CACHE", "Error: File " + cacheUri + " was not found!" );
			e.printStackTrace();
		} catch ( IOException e ) {
			Log.i( "CACHE", "Error: File could not be stuffed!" );
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param cacheUri The uri for the given cached image
	 * @return
	 */
	public Bitmap getCacheFile( String cacheUri )
	{
		if ( bitmapMap.containsKey( cacheUri ) ) return bitmapMap.get( cacheUri );
		if ( !cacheMap.containsKey( cacheUri ) ) return null;
		String fileLocalName = cacheMap.get( cacheUri );
		File fullCacheDir = new File(
			Environment.getExternalStorageDirectory().toString(),
			cacheDir
		);
		File fileUri = new File( fullCacheDir.toString(), fileLocalName );
		if ( !fileUri.exists() ) return null;
		Log.i( "CACHE", "File " + cacheUri + " has been found in the Cache" );
		Bitmap bm = BitmapFactory.decodeFile( fileUri.toString() );
		bitmapMap.put( cacheUri, bm );
		return bm;
	}
}

