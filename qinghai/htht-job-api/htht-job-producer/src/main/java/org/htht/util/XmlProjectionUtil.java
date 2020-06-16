package org.htht.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.types.resources.comparators.Date;
import org.apache.tools.ant.util.DateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 投影结果解析
 * @author yuguoqing
 * @Date 2018年10月23日 下午6:34:15
 *
 *
 */
public class XmlProjectionUtil {
	@SuppressWarnings("unchecked")
	private static List<Element> getRootNodes(String xmlPath) {
		SAXReader reader = new SAXReader();

		Document document = null;
		try {
			document = reader.read(xmlPath);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null != document) {
			Element root = document.getRootElement();
			return root.elements();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, String> getProjection(String xmlPath) {
		List<Element> rootNodes = getRootNodes(xmlPath);

		Element et = null;
		Map<String, String> map = new HashMap<String, String>();
		// 预定义xml节点名与轨道对象方法名对应关系
		String[][] OrbitFields = {{"OrbitFilename", "setPath"},
				{"Satellite", "setSatellite"}, {"Sensor", "setSensor"},
				{"Level", "setDatalevel"}, {"DayOrNight", "setDayornight"},
				{"ObservationDate", "setObservationdate"},
				{"ObservationTime", "setObservationtime"},
				{"Station", "setStation"},
				{"OrbitIdentify", "setOrbitidentify"}};
		if(null != rootNodes){
			for (int i = 0; i < rootNodes.size(); i++) {
				et = (Element) rootNodes.get(i);// 循环依次得到子元素
				String ename = et.getName();
				if (ename.equals("OutputFiles")) {
					List<Element> subFileNode = et.elements(); // 得到内层子节点
					if (subFileNode != null)
						for (int sj = 0; sj < subFileNode.size(); sj++) {
							List<Element> fileAttrNotes = ((Element) subFileNode
									.get(sj)).elements();
							if (fileAttrNotes != null && fileAttrNotes.size() > 0) {

								for (int noteIndex = 0; noteIndex < fileAttrNotes
										.size(); noteIndex++) {
									Element ele = (Element) fileAttrNotes
											.get(noteIndex);
									if ("OutputFilename".equals(ele.getName())) {
										map.put("OutputFilename",
												ele.getStringValue());
									} else if ("Thumbnail".equals(ele.getName())) {
										map.put("Thumbnail", ele.getStringValue());
									} else if ("ExtendFiles"
											.equals(ele.getName())) {
										map.put("ExtendFiles",
												ele.getStringValue());
									} else if ("ResolutionX"
											.equals(ele.getName())) {
										map.put("ResolutionX",
												ele.getStringValue());
									} else if ("ResolutionY"
											.equals(ele.getName())) {
										map.put("ResolutionY",
												ele.getStringValue());
									} else if ("Length".equals(ele.getName())) {
										map.put("ResolutionY", new BigDecimal(ele
												.getStringValue() == null
												|| "".equals(ele.getStringValue())
												? "0"
												: ele.getStringValue())
												+ "");
									} else if ("Envelope".equals(ele.getName())) {
//									String envelopeName = ele
//											.attributeValue("name");
										String envelopeminx = ele
												.attributeValue("minx");
										String envelopemaxx = ele
												.attributeValue("maxx");
										String envelopeminy = ele
												.attributeValue("miny");
										String envelopemaxy = ele
												.attributeValue("maxy");
										map.put("minx", envelopeminx);
										map.put("maxx", envelopemaxx);
										map.put("miny", envelopeminy);
										map.put("maxy", envelopemaxy);
									}
								}

							}
						}
				} else if (ename.equals("ProjectionIdentify")) {
					map.put("ProjectionIdentify", et.getStringValue());
				} else if (ename.equals("log")) {
					List<Element>  subLogNode = et.elements(); // 得到内层子节点
					map.put("loglevel",
							((Element) subLogNode.get(0)).getStringValue());
					map.put("loginfo",
							((Element) subLogNode.get(1)).getStringValue());
				} else if (ename.equals("OrbitIdentify")) {
					map.put("OrbitIdentify",et.getText());
				}else if (ename.equals("Level")) {
					map.put("Level",et.getText());
				}else if (ename.equals("DayOrNight")) {
					map.put("DayOrNight",et.getText());
				}else if (ename.equals("InputOrbitId")) {
					map.put("InputOrbitId",et.getText());
				}else {
					// 轨道数据信息
					for (@SuppressWarnings("unused") String[] f : OrbitFields) {
						if ("Sensor".equals(et.getName())) {
							map.put("ProjectionIdentify", et.getStringValue());
							break;
						}
						if ("Length".equals(et.getName())) {
							map.put("Length", et.getStringValue());
							break;
						}
					}

				}

			}
			return map;
		}
		return map;
	}

	public static void main(String[] args) {
		String xmlPath = "C:\\Users\\Administrator\\Desktop\\FY3B_VIRRX_GBAL_L1_20181019_0231_1000M_MS.HDF.xml";
		Map<String, String> projectionResultMap = XmlProjectionUtil.getProjection(xmlPath);


		String minx =  projectionResultMap.get("minx");
		String maxx = projectionResultMap.get("maxx");
		String miny =  projectionResultMap.get("miny");
		String maxy = projectionResultMap.get("maxy");
		String resolutionx = "1000";
		String uuid = "111111";
		String satellite = "AAA";
		String sensor = "BBB";
		Date fileDate = new Date();
//		String fileDate = DateUtils.format(fileDateee, "yyyy-MM-dd HH:mm:ss");

		// 影像信息
		String sql = "insert into htht_dms_meta_img";
		StringBuffer colSql = new StringBuffer("(f_dataid");
		StringBuffer valSql = new StringBuffer("('" + uuid + "'");
		colSql.append(",F_SATELLITEID");
		valSql.append(",'" + satellite + "'");
		colSql.append(",F_SENSORID");
		valSql.append(",'" + sensor + "'");
		colSql.append(",F_RECEIVETIME");
		valSql.append(",'" + fileDate + "'");
		colSql.append(",F_PRODUCETIME");
		valSql.append(",'" + fileDate + "'");
		colSql.append(",F_CENTERTIME");
		valSql.append(",'" + fileDate + "'");
		colSql.append(",f_level");
		valSql.append(",'L2'");
		colSql.append(",f_resolutionx");
		valSql.append(", '"+resolutionx+"'");

		colSql.append(",F_DATAUPPERLEFTLAT");//图像左上角纬度
		valSql.append(", '"+minx+"'");
		colSql.append(",F_DATAUPPERLEFTLONG");//图像左上角经度
		valSql.append(", '"+maxy+"'");

		colSql.append(",F_DATAUPPERRIGHTLAT");//图像右上角纬度
		valSql.append(", '"+maxx+"'");
		colSql.append(",F_DATAUPPERRIGHTLONG");//图像右上角经度
		valSql.append(", '"+maxy+"'");

		colSql.append(",F_DATALOWERLEFTLAT");//左下角纬度
		valSql.append(", '"+minx+"'");
		colSql.append(",F_DATALOWERLEFTLONG");//左下角经度
		valSql.append(", '"+miny+"'");

		colSql.append(",F_DATALOWERRIGHTLAT");//右下角纬度
		valSql.append(", '"+maxx+"'");
		colSql.append(",F_DATALOWERRIGHTLONG");//右下角经度
		valSql.append(", '"+miny+"'");

		colSql.append(")");
		valSql.append(")");
		Object[] obj = {};

		System.out.println("=========>" + sql + colSql.toString() + " values "
				+ valSql.toString());
	}
}
