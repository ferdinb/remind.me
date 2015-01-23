package com.f.ninaber.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

public class ImageUtil {

	public static Uri getReziedImageUri(Activity act, Uri source) {
		int size = ScreenUtil.width(act);
		Bitmap bmp = null;
		try {
			bmp = uriToScreenSize(act, source, size);
			if(null == bmp){
				return null;
			}
			
			Log.e("f.ninaber", "Width : " + bmp.getWidth() + " | Height : " + bmp.getHeight());
			Matrix matrix = new Matrix();
			matrix.postRotate(setImageOrientation(source.getPath()));
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
		} catch (FileNotFoundException e) {
			Log.e("f.ninaber", "exception : " + e.getMessage());
			return null;
		}

		File cacheDir = act.getCacheDir();
		File f = new File(cacheDir, "shout");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			Log.e("f.ninaber", "Width 2 : " + bmp.getWidth() + " | Height 2 : " + bmp.getHeight());
			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bmp != null) {
				bmp.recycle();
				bmp = null;
			}
		}

		return Uri.fromFile(f);
	}

	public static int setImageOrientation(String imagePath) {
		int rotate = 0;
		try {

			File imageFile = new File(imagePath);
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.e("f.ninaber", "Rotate : " + rotate);
		return rotate;
	}

	public static Bitmap uriToScreenSize(Context c, Uri uri, final int requiredSize) throws FileNotFoundException {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);
		float width_tmp = o.outWidth, height_tmp = o.outHeight;
		Log.e("f.ninaber", "Width tmp : " + width_tmp + " | Height tmp : " + height_tmp);
		
		float scale = 1;
		while (true) {
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
			if (width_tmp < requiredSize && height_tmp < requiredSize) {
				break;
			}
		}

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = (int) scale;
		return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static String convertMediaToBase64(Context context, Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] arrayByte = stream.toByteArray();
			return Base64.encodeToString(arrayByte, Base64.DEFAULT);
		} catch (Exception e) {
			Log.e("f.ninaber", "Exception : " + ImageUtil.class.toString() + e.getMessage());
		}
		return null;
	}

	public static byte[] convertMediaToByte(Context context, Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] arrayByte = stream.toByteArray();
			return arrayByte;
		} catch (Exception e) {
			Log.e("f.ninaber", "Exception : " + ImageUtil.class.toString() + e.getMessage());
		}
		return null;
	}

	public static File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = File.createTempFile(imageFileName, ".jpg", storageDir);
		return file;
	}

}
