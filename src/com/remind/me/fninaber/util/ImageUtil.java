package com.remind.me.fninaber.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.remind.me.fninaber.Constants;

public class ImageUtil {

	public static Uri resizeWriteUrI(Activity act, Uri source, boolean temp) {
		int size = ScreenUtil.width(act);
		Bitmap bmp = null;
		try {
			Log.e("f.ninaber", "Source : " + source);
			bmp = uriToScreenSize(act, source, size);
			if (null == bmp) {
				return null;
			}

			Matrix matrix = new Matrix();
			matrix.postRotate(setImageOrientation(source.getPath()));
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
		} catch (FileNotFoundException e) {
			Log.e("f.ninaber", "exception : " + e.getMessage());
			return null;
		}

		if (temp) {
			return writeTempBitmap(act, bmp);
		} else {
			return writeBitmap(bmp, act);
		}
	}

	public static Uri writeBitmap(Uri uri, Activity act) {
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(act.getContentResolver(), uri);
		} catch (Exception e) {
			Log.e("f.ninaber", "Write Bitmap e : " + e);
		}

		if (null != bitmap) {
			return writeBitmap(bitmap, act);
		} else {
			return null;
		}
	}

	public static Uri writeBitmap(Bitmap bmp, Activity act) {
		File f = new File(Environment.getExternalStorageDirectory() + Constants.IMAGE_FOLDER);
		if (!f.exists()) {
			f.mkdirs();
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + ".jpg";
		File file = new File(f, imageFileName);
		try {
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			Log.e("f.ninaber", "Error write bitmap : " + e);
		} finally {
			if (bmp != null) {
				bmp.recycle();
				bmp = null;
			}
		}
		return Uri.fromFile(file);
	}

	public static Uri writeTempBitmap(Activity act, Bitmap bmp) {
		File cacheDir = act.getCacheDir();
		File f = new File(cacheDir, "temp_image");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			Log.e("f.ninaber", "Error write bitmap : " + e);
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
			if (width_tmp < requiredSize && height_tmp < requiredSize) {
				break;
			}
			scale *= 2;
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

	public static File createTempImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = new File(Environment.getExternalStorageDirectory() + Constants.TEMP_IMAGE_FOLDER);
		if (!storageDir.exists()) {
			storageDir.mkdirs();
		}

		File file = File.createTempFile(imageFileName, ".jpg", storageDir);
		return file;
	}

	public static void deleteTempFile() {
		File storageDir = new File(Environment.getExternalStorageDirectory() + Constants.TEMP_IMAGE_FOLDER);
		File[] files = storageDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[0].delete();
		}
	}

}
