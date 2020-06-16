/**
 * 图片黑边处理
 * @author 熊成
 */

package com.htht.job.executor.hander.dataarchiving.util.img;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class TransferProcess {

	/**
	 * 对图片中的 黑色或白色进行透明化处理 
	 * @param sourcePath 原始图 
	 * @param
	 * targetPath 目标图,为null时在原始图同级目录下生成目标图 
	 * @param type B:黑色 W:白色 
	 * @return
	 * 结果图字节数据组
	 */
	public byte[] transferAlpha(String sourcePath,String imgName, String targetPath, String type, PointBean pointBean)throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			File iFile = new File(sourcePath);
			if (!iFile.exists())
				return byteArrayOutputStream.toByteArray();

			ImageIcon imageIcon = new ImageIcon(ImageIO.read(iFile));
			BufferedImage bufferedImage = new BufferedImage(
					imageIcon.getIconWidth(), imageIcon.getIconHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
			g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
			int alpha = 0;
			int offset = 20;
			boolean isBlack = type.equals("B");
			
			// pointbean

			Map mapXY = getImageXY(sourcePath, pointBean);
			for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage
					.getHeight(); j1++) {
				for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage
						.getWidth(); j2++) {
					int rgb = bufferedImage.getRGB(j2, j1);

					int R = (rgb & 0xff0000) >> 16;
					int G = (rgb & 0xff00) >> 8;
					int B = (rgb & 0xff);
					boolean checkW = ((255 - R) < offset) && ((255 - G) < offset) && ((255 - B) < offset);
					boolean checkB = ((R < offset) && (G < offset) && (B < offset));
					boolean isConstant = isContaor(mapXY, j2, j1);
					if (isConstant == false) {
						rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
						rgb = 0x00000000;
						bufferedImage.setRGB(j2, j1, rgb);
					}

					
					// if (isBlack?checkB:checkW) {
					// System.out.println(j2 +"   "+j1);
					// }
					
				}
			}

			g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
			
			File targetFile = null;
			if (targetPath == null) {
//				targetFile = new File(ArcsdeProperss.STA_IMG_TORASTER_PATH + "\\" + imgName);
				targetFile = new File("D:\\zzj\\" + imgName);
			} else {
				targetFile = new File(targetPath);
				if (!targetFile.exists()) {
					File dir = new File(targetFile.getParent());
					if (!dir.exists())
						dir.mkdirs();
				}
			}
			ImageIO.write(bufferedImage, "PNG", targetFile);
			
			// 返回处理后图像的byte[]
			// ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
		} catch (Exception e) {
			//ErrorLog.errorLog(sourcePath.substring(sourcePath.lastIndexOf("\\")), "去除图片黑边异常",e.getMessage());
		}
		return byteArrayOutputStream.toByteArray();

	}
	
	/**
	 * 对图片中的 黑色或白色进行透明化处理 
	 * @param sourcePath 原始图 
	 * @param
	 * targetPath 目标图,为null时在原始图同级目录下生成目标图 
	 * @param type B:黑色 W:白色 
	 * @return
	 * 结果图字节数据组
	 */
	public byte[] transferAlphaGF3(String sourcePath,String imgName, String targetPath, String type, PointBean pointBean)throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			File iFile = new File(sourcePath);
			if (!iFile.exists())
				return byteArrayOutputStream.toByteArray();

			ImageIcon imageIcon = new ImageIcon(ImageIO.read(iFile));
			BufferedImage bufferedImage = new BufferedImage(
					imageIcon.getIconWidth(), imageIcon.getIconHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
			g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
			int alpha = 0;
			int offset = 20;
			boolean isBlack = type.equals("B");
			
			// pointbean

			Map mapXY = getImageXY(sourcePath, pointBean);
			for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage
					.getHeight(); j1++) {
				for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage
						.getWidth(); j2++) {
					int rgb = bufferedImage.getRGB(j2, j1);

					int R = (rgb & 0xff0000) >> 16;
					int G = (rgb & 0xff00) >> 8;
					int B = (rgb & 0xff);
					boolean checkW = ((255 - R) < offset) && ((255 - G) < offset) && ((255 - B) < offset);
					boolean checkB = ((R < offset) && (G < offset) && (B < offset));
//					boolean isConstant = isContaor(mapXY, j2, j1);
//					if (checkB) {
//						rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
//						rgb = 0x00000000;
//						bufferedImage.setRGB(j2, j1, rgb);
//					}

					
					// if (isBlack?checkB:checkW) {
					// System.out.println(j2 +"   "+j1);
					// }
					
				}
			}

			g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
			
			File targetFile = null;
			if (targetPath == null) {
//				targetFile = new File(ArcsdeProperss.STA_IMG_TORASTER_PATH + "\\" + imgName);
				targetFile = new File("D:\\" + imgName);
			} else {
				targetFile = new File(targetPath);
				if (!targetFile.exists()) {
					File dir = new File(targetFile.getParent());
					if (!dir.exists())
						dir.mkdirs();
				}
			}
			ImageIO.write(bufferedImage, "PNG", targetFile);
			
			// 返回处理后图像的byte[]
			// ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
		} catch (Exception e) {
			//ErrorLog.errorLog(sourcePath.substring(sourcePath.lastIndexOf("\\")), "去除图片黑边异常",e.getMessage());
		}
		return byteArrayOutputStream.toByteArray();

	}

	/**
	 * 对图片中的 黑色或白色进行透明化处理 
	 * @param sourcePath 原始图 
	 * @param
	 * targetPath 目标图,为null时在原始图同级目录下生成目标图 
	 * @param type B:黑色 W:白色 
	 * @return
	 * 结果图字节数据组
	 */
	public byte[] transferAlphaNo(String sourcePath,String imgName, String targetPath) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			File iFile = new File(sourcePath);
			if (!iFile.exists())
				return byteArrayOutputStream.toByteArray();

			ImageIcon imageIcon = new ImageIcon(ImageIO.read(iFile));
			BufferedImage bufferedImage = new BufferedImage(
					imageIcon.getIconWidth(), imageIcon.getIconHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
			int alpha = 0;
			int offset = 20;
			// pointbean
			Map mapXY = getImageXY(sourcePath, imgName);
			System.out.println(mapXY);
			if (mapXY != null) {
				for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage
						.getHeight(); j1++) {
					for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage
							.getWidth(); j2++) {
						int rgb = bufferedImage.getRGB(j2, j1);
		
						int R = (rgb & 0xff0000) >> 16;
						int G = (rgb & 0xff00) >> 8;
						int B = (rgb & 0xff);
						boolean checkW = ((255 - R) < offset) && ((255 - G) < offset) && ((255 - B) < offset);
						boolean checkB = ((R < offset) && (G < offset) && (B < offset));
						boolean isConstant = isContaor(mapXY, j2, j1);
						if (isConstant == false) {
		//					rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
							rgb = 0x00000000;
							bufferedImage.setRGB(j2, j1, rgb);
						}
		
						
						// if (isBlack?checkB:checkW) {
						// System.out.println(j2 +"   "+j1);
						// }
						
					}
				}
			}
			g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
			File targetFile = new File(targetPath + "\\" + imgName);
			ImageIO.write(bufferedImage, "PNG", targetFile);
			
			// 返回处理后图像的byte[]
			// ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toByteArray();

	}
	/**
	 * 对图片中的 黑色或白色进行透明化处理 
	 * @param sourcePath 原始图 
	 * @param
	 * targetPath 目标图,为null时在原始图同级目录下生成目标图 
	 * @param type B:黑色 W:白色 
	 * @return
	 * 结果图字节数据组
	 */
	public Map getImageXY(String sourcePath,String imgName) {
		try {
			File iFile = new File(sourcePath);
			ImageIcon imageIcon = new ImageIcon(ImageIO.read(iFile));
			BufferedImage bufferedImage = new BufferedImage(
					imageIcon.getIconWidth(), imageIcon.getIconHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
			g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
			
			// pointbean

			int upx = 0;
			int leftY = 0;
			int rightY = 0;
			int lowx = 0;
			for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
				for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {
					int rgb = bufferedImage.getRGB(j2, j1);
					int R = (rgb & 0xff0000) >> 16;
					int G = (rgb & 0xff00) >> 8;
					int B = (rgb & 0xff);
					if (j1 == 0) {
						if (R >0 && G>0 && B>0) {
							upx = j2;
							break;
						}
					}
					if (j1 == bufferedImage.getHeight()-1) {
						if (R >0 && G>0 && B>0) {
							lowx = j2;
							break;
						}
					}
					
					if (j2 == 0) {
						if (R >0 && G>0 && B>0) {
							leftY = j1;
							break;
						}
					}
					
					if (j2 == bufferedImage.getWidth()-1) {
						if (R >0 && G>0 && B>0) {
							rightY = j1;
							break;
						}
					}
				}
			}
			Point leftupP = new Point();
			leftupP.x = upx;
			leftupP.y = 0;

			Point leftLowP = new Point();
			leftLowP.x = 0;
			leftLowP.y = leftY;

			Point rightUpP = new Point();
			rightUpP.x = bufferedImage.getWidth();
			rightUpP.y = rightY;

			Point rightLowP = new Point();
			rightLowP.x = lowx;
			rightLowP.y = bufferedImage.getHeight();
			if (upx == 0 && rightY== 0) {
				return null;
			}
			HashMap map = new HashMap();
			map.put("leftupP", leftupP);
			map.put("leftLowP", leftLowP);
			map.put("rightUpP", rightUpP);
			map.put("rightLowP", rightLowP);
			return map;
			// 杩斿洖澶勭悊鍚庡浘鍍忕殑byte[]
			// ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * 通过图片路径和经纬度获取数据在图片中的真实位置
	 * @param fileName  图片名称
	 * @param pointBean 数据经纬度
	 * @return Map 真实位置map
	 */
	public Map getImageXY(String fileName, PointBean pointBean)throws Exception {
		Image image = new Image(fileName);

		double PRODUCTUPPERLEFTLAT = pointBean.getTopLeftY();
		double PRODUCTUPPERLEFTLONG = pointBean.getTopLeftX();

		double PRODUCTUPPERRIGHTLAT = pointBean.getTopRightY();
		double PRODUCTUPPERRIGHTLONG = pointBean.getTopRightX();

		double PRODUCTLOWERLEFTLAT = pointBean.getBottomLeftY();
		double PRODUCTLOWERLEFTLONG = pointBean.getBottomLeftX();

		double PRODUCTLOWERRIGHTLAT = pointBean.getBottomRightY();
		double PRODUCTLOWERRIGHTLONG = pointBean.getBottomRightX();

		double DATAUPPERLEFTLONG = pointBean.getDATAUPPERLEFTLONT();

		double DATAUPPERRIGHTLAT = pointBean.getDATAUPPERRIGHTLAT();
		double DATALOWERRIGHTLONG = pointBean.getDATALOWERRIGHTLONG();
		// 产品经度差值
		double UPPERX = PRODUCTUPPERRIGHTLONG - PRODUCTUPPERLEFTLONG;
		// 每个经度的宽度
		double upperx1 = image.getWidth() / UPPERX;
		// 现实经度差
		double UPPERX1 = DATAUPPERLEFTLONG - PRODUCTUPPERLEFTLONG;
		// 数据x坐标
		int upx = (int) (upperx1 * UPPERX1);

		// 产品经度差值
		double righty = PRODUCTUPPERRIGHTLAT - PRODUCTLOWERRIGHTLAT;
		// 每个经度的宽度
		double righty1 = image.getHeight() / righty;
		// 现实经度差
		double righty1T = DATAUPPERRIGHTLAT - PRODUCTLOWERRIGHTLAT;
		// 数据x坐标
		int leftY = (int) (righty1T * righty1);

		// 产品经度差值
		double lefty = PRODUCTUPPERLEFTLAT - PRODUCTLOWERLEFTLAT;
		// 每个经度的宽度
		double lefty1 = image.getHeight() / lefty;
		// 现实经度差
		double left1T = PRODUCTUPPERLEFTLAT - DATAUPPERRIGHTLAT;
		// 数据x坐标
		int rightY = (int) (left1T * lefty1);

		// 产品经度差值
		double UPPERX11 = PRODUCTLOWERRIGHTLONG - PRODUCTLOWERLEFTLONG;
		// 每个经度的宽度
		double upperx11 = image.getWidth() / UPPERX11;
		// 现实经度差
		double UPPERX111 = DATALOWERRIGHTLONG - PRODUCTLOWERLEFTLONG;
		// 数据x坐标
		int lowx = (int) (upperx11 * UPPERX111);

		Point leftupP = new Point();
		leftupP.x = upx;
		leftupP.y = 0;

		Point leftLowP = new Point();
		leftLowP.x = 0;
		leftLowP.y = leftY;

		Point rightUpP = new Point();
		rightUpP.x = image.getWidth();
		rightUpP.y = rightY;

		Point rightLowP = new Point();
		rightLowP.x = lowx;
		rightLowP.y = image.getHeight();

		HashMap map = new HashMap();
		map.put("leftupP", leftupP);
		map.put("leftLowP", leftLowP);
		map.put("rightUpP", rightUpP);
		map.put("rightLowP", rightLowP);
		return map;
	}

	/**
	 * 判断图片中的xy是否在图片元数据中
	 * @param mapXY 图片数据的xy坐标
	 * @param x 当前判断的x
	 * @param y 当前判断的y
	 * @return boolean true | false
	 */
	public boolean isContaor(Map mapXY, int x, int y) {
		Point leftupP = (Point) mapXY.get("leftupP");
		Point leftLowP = (Point) mapXY.get("leftLowP");
		Point rightUpP = (Point) mapXY.get("rightUpP");
		Point rightLowP = (Point) mapXY.get("rightLowP");

		Point points = new Point();
		points.x = x;
		points.y = y;

		boolean contains = inTriangle(points, leftupP, leftLowP, rightLowP);
		if (contains == false) {
			contains = inTriangle(points, leftupP, rightUpP, rightLowP);
		}
		return contains;
	}

	// 由给定的三个顶点的坐标，计算三角形面积。
	// Point(java.awt.Point)代表点的坐标。
	private static double triangleArea(Point pos1, Point pos2, Point pos3) {
		double result = Math
				.abs((pos1.x * pos2.y + pos2.x * pos3.y + pos3.x * pos1.y
						- pos2.x * pos1.y - pos3.x * pos2.y - pos1.x * pos3.y) / 2.0D);
		return result;
	}

	// 判断点pos是否在指定的三角形内。
	private static boolean inTriangle(Point pos, Point posA, Point posB,
			Point posC) {
		double triangleArea = triangleArea(posA, posB, posC);
		double area = triangleArea(pos, posA, posB);
		area += triangleArea(pos, posA, posC);
		area += triangleArea(pos, posB, posC);
		double epsilon = 0.0001; // 由于浮点数的计算存在着误差，故指定一个足够小的数，用于判定两个面积是否(近似)相等。
		if (Math.abs(triangleArea - area) < epsilon) {
			return true;
		}
		return false;
	}

	/**
	 * 处理
	 * @param url
	 * @param pointBean
	 */
	public void disImageBackground(String url,String imgName, PointBean pointBean) throws Exception{
		transferAlpha(url, imgName, null, "B", pointBean);
	}
	
	public void disImageBackgroundByPath(String url,String imgName,String path, PointBean pointBean)throws Exception {
		transferAlpha(url, imgName, path, "B", pointBean) ;
	}
	
	public void disImageBackgroundByPathGF3(String url,String imgName,String path, PointBean pointBean)throws Exception {
		transferAlphaGF3(url, imgName, path, "B", pointBean) ;
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// 去除影像黑边
//		PointBean pointBean = new PointBean ();
//		topleftlatitude, topleftlongitude, toprightlatitude, toprightlongitude, 
//		bottomleftlatitude, bottomleftlongitude,bottomrightlatitude, bottomrightlongitude
//		pointBean.setTopLeftY(rs.getDouble(1));
//		pointBean.setTopLeftX(rs.getDouble(6));
//		pointBean.setTopRightY(rs.getDouble(1));
//		pointBean.setTopRightX(rs.getDouble(4));
//		pointBean.setBottomLeftY(rs.getDouble(7));
//		pointBean.setBottomLeftX(rs.getDouble(6));
//		pointBean.setBottomRightY(rs.getDouble(7));
//		pointBean.setBottomRightX(rs.getDouble(4));
//		
//		
//		pointBean.setDATAUPPERLEFTLAT(rs.getDouble(1));
//		pointBean.setDATAUPPERLEFTLONT(rs.getDouble(2));
//		
//		pointBean.setDATAUPPERRIGHTLAT(rs.getDouble(3));
//		pointBean.setDATAUPPERRIGHTLONG(rs.getDouble(4));
//		
//		pointBean.setDATALOWERLEFTLAT(rs.getDouble(5));
//		pointBean.setDATALOWERLEFTLONG(rs.getDouble(6));
//		
//		pointBean.setDATALOWERRIGHTLAT(rs.getDouble(7));
//		pointBean.setDATALOWERRIGHTLONG(rs.getDouble(8));
		
		try {
			PointBean pointBean = new PointBean();
//			pointBean.setTopLeftY(37.906985);
//			pointBean.setTopLeftX(118.553297);
//			pointBean.setTopRightY(37.906985);
//			pointBean.setTopRightX(119.052777);
//			pointBean.setBottomLeftY(37.443532);
//			pointBean.setBottomLeftX(118.553297);
//			pointBean.setBottomRightY(37.443532);
//			pointBean.setBottomRightX(119.052777);
			pointBean.setTopLeftY(38.034266);//setDATAUPPERRIGHTLAT
			pointBean.setTopLeftX(118.375000);//setDATAUPPERLEFTLONT
			pointBean.setTopRightY(38.034266);//setDATAUPPERRIGHTLAT
			pointBean.setTopRightX(119.225786);//setDATALOWERRIGHTLONG
			pointBean.setBottomLeftY(37.316818);//setDATALOWERLEFTLAT
			pointBean.setBottomLeftX(118.375000);//setDATAUPPERLEFTLONT
			pointBean.setBottomRightY(37.316818);//setDATALOWERLEFTLAT
			pointBean.setBottomRightX(119.225786);//setDATALOWERRIGHTLONG
			
//			pointBean.setDATAUPPERLEFTLAT(37.906985);
//			pointBean.setDATAUPPERLEFTLONT(118.375000);
//			
//			pointBean.setDATAUPPERRIGHTLAT(38.034266);
//			pointBean.setDATAUPPERRIGHTLONG(119.052777);
//			
//			pointBean.setDATALOWERLEFTLAT(37.316818);
//			pointBean.setDATALOWERLEFTLONG(118.553297);
//			
//			pointBean.setDATALOWERRIGHTLAT(37.443532);
//			pointBean.setDATALOWERRIGHTLONG(119.225786);
			
			pointBean.setDATAUPPERLEFTLAT(38.034266);//setDATAUPPERRIGHTLAT
			pointBean.setDATAUPPERLEFTLONT(119.052777);//setDATAUPPERRIGHTLONG
			
			pointBean.setDATAUPPERRIGHTLAT(37.443532);//setDATALOWERRIGHTLAT
			pointBean.setDATAUPPERRIGHTLONG(119.225786);//setDATALOWERRIGHTLONG
			
			pointBean.setDATALOWERLEFTLAT(37.906985);//setDATAUPPERLEFTLAT
			pointBean.setDATALOWERLEFTLONG(118.375000);//setDATAUPPERLEFTLONT
			
			pointBean.setDATALOWERRIGHTLAT(37.316818);//setDATALOWERLEFTLAT
			pointBean.setDATALOWERRIGHTLONG(118.553297);//setDATALOWERLEFTLONG
			new TransferProcess().disImageBackgroundByPath("E:\\09-work\\Share\\work\\GF5_VIMS_E118.0_N37.0_20180601_000347_L10000020931\\gf5_vims_e118.0_n37.0_20180601_000347_l10000020931_raster.jpg", 
			"gf5_vims_e118.0_n37.0_20180601_000347_l10000020931_raster.jpg", 
			"E:\\09-work\\Share\\GF5_VIMS_E118.0_N37.0_20180601_000347_L10000020931.jpg",
			pointBean);
//			new TransferProcess().disImageBackgroundByPath("D:\\zzj\\GF1_WFV3_E103.0_N5.4_20150103_L2A0000566131\\GF1_WFV3_E103.0_N5.4_20150103_L2A0000566131.jpg","GF1_WFV3_E103.0_N5.4_20150103_L2A0000566131.jpg", "D:\\zzj\\GF1_WFV3_E103.0_N5.4_20150103_L2A0000566131.jpg",pointBean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		File file = new File ("C:\\Users\\Administrator\\Desktop\\new");
//		File []filelist = file.listFiles();
//		TransferProcess process = new TransferProcess();
//		for (int i=0;i<filelist.length ;i++) {
//			File file1 = filelist [i] ; 
//			if (file1.getPath().indexOf("HRC") == -1) {
//				process.transferAlphaNo(file1.getPath(),file1.getName(), "C:\\Users\\Administrator\\Desktop\\create");
//			}
//		}
		
	}
}
