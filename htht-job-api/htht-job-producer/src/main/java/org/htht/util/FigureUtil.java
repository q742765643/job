package org.htht.util;

import org.htht.util.bean.Point;

public class FigureUtil {

	/**
	 * 回转数法判断一个点是否在一个多边形内
	 * @param standardPoints 多边形的顶点 
	 * @param checkedPoint 待检查的点
	 * @return
	 */
	public static boolean windingNumber(Point[] standardPoints,
			Point checkedPoint) {
		double px = checkedPoint.getX();
		double py = checkedPoint.getY();
		double sum = 0;

		for (int i = 0, l = standardPoints.length, j = l - 1; i < l; j = i, i++) {
			double sx = standardPoints[i].getX(), sy = standardPoints[i].getY(), tx = standardPoints[j]
					.getX(), ty = standardPoints[j].getY();

			// 点与多边形顶点重合或在多边形的边上
			if ((sx - px) * (px - tx) >= 0 && (sy - py) * (py - ty) >= 0
					&& (px - sx) * (ty - sy) == (py - sy) * (tx - sx)) {
				return true;
			}

			// 点与相邻顶点连线的夹角
			double angle = Math.atan2(sy - py, sx - px)
					- Math.atan2(ty - py, tx - px);

			// 确保夹角不超出取值范围（-π 到 π）
			if (angle >= Math.PI) {
				angle = angle - Math.PI * 2;
			} else if (angle <= -Math.PI) {
				angle = angle + Math.PI * 2;
			}

			sum += angle;
		}

		// 计算回转数并判断点和多边形的几何关系
		return Math.round(sum / Math.PI) == 0 ? false : true;
	}
	
	/**
	 * 射线法判断一个点是否在一个多边形内
	 * @param p1 多边形的顶点
	 * @param p 待检查的点
	 * @return
	 */
	public static boolean rayCasting(Point[] p1, Point p) {
		double px = p.getX(), py = p.getY();
		boolean flag = false;
		for (int i = 0, l = p1.length, j = l - 1; i < l; j = i, i++) {
			double sx = p1[i].getX(), sy = p1[i].getY(), tx = p1[j].getX(), ty = p1[j].getY();

			// 点与多边形顶点重合
			if ((sx == px && sy == py) || (tx == px && ty == py)) {
				return true;
			}

			// 判断线段两端点是否在射线两侧
			if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
				// 线段上与射线 Y 坐标相同的点的 X 坐标
				double x = sx + (py - sy) * (tx - sx) / (ty - sy);

				// 点在多边形的边上
				if (x == px) {
					return true;
				}

				// 射线穿过多边形的边界
				if (x > px) {
					flag = !flag;
				}
			}
		}

		return flag;
	}
	
	/**
	 * 回转数法判断一组点是否在一个多边形内
	 * @param standardPoints  多边形的顶点
	 * @param checkedPoints 待检查的点
	 * @return
	 */
	public static boolean windingNumberArr(Point[] standardPoints,
			Point[] checkedPoints) {
		boolean result = false;
		for(Point p:checkedPoints){
			result= windingNumber(standardPoints,p);
			if(result){
				break;
			}
		}
		return result;
	}
	
	/**
	 * 射线法判断一组点是否在一个多边形内
	 * @param standardPoints 多边形的顶点
	 * @param checkedPoints  待检查的点
	 * @return
	 */
	public static boolean rayCastingWithArr(Point[] standardPoints,
			Point[] checkedPoints) {
		boolean result = false;
		for(Point p:checkedPoints){
			result= rayCasting(standardPoints,p);
			if(result){
				break;
			}
		}
		return result;
	}

}
