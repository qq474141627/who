package com.qm.gangsdk.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageTools {

	public static String USERIMAGES = "/aaa/userimages";

	public static Bitmap zoomImage(Bitmap bgimage) {
		return zoomImage(bgimage, 320, 240);
	}

	public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {

		if (bgimage != null) {
			int width = bgimage.getWidth();
			int height = bgimage.getHeight();

			Matrix matrix = new Matrix();
			float scaleWidth = 1.0f;
			float scaleHeight = 1.0f;
			if (newWidth < width) {
				scaleWidth = ((float) newWidth) / width;
			}
			if (newHeight < height) {
				scaleHeight = ((float) newHeight) / height;
			}
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,
					matrix, true);
			return bitmap;
		} else {
			return null;
		}

	}

	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {

		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	public static byte[] getBytesFromInputStream(InputStream is, int bufsiz)
			throws IOException {
		int total = 0;
		byte[] bytes = new byte[4096];
		ByteBuffer bb = ByteBuffer.allocate(bufsiz);

		while (true) {
			int read = is.read(bytes);
			if (read == -1)
				break;
			bb.put(bytes, 0, read);
			total += read;
		}

		byte[] content = new byte[total];
		bb.flip();
		bb.get(content, 0, total);

		return content;
	}

	public static Bitmap comp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}

		Log.e("mb","compressImage -- baos.toByteArray().length / 1024 : " + baos.toByteArray().length / 1024);

		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		return bitmap;

//		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	// 保存拍摄的照片到手机的sd卡
	public static String SavePicInLocal(Bitmap bitmap) {
		String fileName = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null; // 字节数组输出流
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);



//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > 120) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 1;// 每次都减少10
			}

			Log.e("mb","compressImage -- baos.toByteArray().length / 1024 : " + baos.toByteArray().length / 1024);





			Log.e("mb","SavePicInLocal -- baos.toByteArray().length / 1024 : " + baos.toByteArray().length / 1024);


			byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
			String saveDir = Environment.getExternalStorageDirectory() + USERIMAGES;
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdir(); // 创建文件夹
			}
			fileName = saveDir + "/" + System.currentTimeMillis() + ".JPG";
			File file = new File(fileName);
			// file.delete();
			if (!file.exists()) {
				file.createNewFile();// 创建文件
			}
			// 将字节数组写入到刚创建的图片文件中
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
			return file.getPath();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return fileName;

	}

	/**
	 * Save Bitmap to a file.保存图片到SD卡。
	 * 
	 * @param bitmap
	 * @return
	 * @return error message if the saving is failed. null if the saving is
	 *         successful.
	 * @throws IOException
	 */
	public static String saveBitmapToFile(Bitmap bitmap, String _file)
			throws IOException {
		BufferedOutputStream os = null;
		try {
			_file = Environment.getExternalStorageDirectory()
					+ USERIMAGES + "/" + System.currentTimeMillis()
					+ ".PNG";
			File file = new File(_file);
			// String _filePath_file.replace(File.separatorChar +
			// file.getName(), "");
			int end = _file.lastIndexOf(File.separator);
			String _filePath = _file.substring(0, end);
			File filePath = new File(_filePath);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			file.createNewFile();
			os = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			return _file;
		} catch (Exception e) {
			e.printStackTrace();
			return _file;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
