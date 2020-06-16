package org.htht.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class XmlParseUtil
{

	private static List getRootNodes(String xmlContent)
	{
		xmlContent = xmlContent.replaceAll("<!--.*-->", "");// 删除注释内容
		byte[] b = null;
		try
		{
			b = xmlContent.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		if (null != b && b.length > 10)
		{
			try
			{
				xmlContent = new String(b, 3, b.length - 3, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		StringReader sr = new StringReader(xmlContent);
		InputSource source = new InputSource(sr);
		SAXBuilder saxb = new SAXBuilder();
		Document doc = null;
		try
		{
			doc = saxb.build(source);
		} catch (JDOMException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if(null != doc) {
			Element root = doc.getRootElement();
			List nodeChildren = root.getChildren();
			return nodeChildren;
		}
		return null;
	}

	private static BigDecimal formatResolution(String res)
	{
		BigDecimal r = new BigDecimal(res == null || "".equals(res) ? "0" : res);
		if (r.intValue() < 1)
		{
			r = r.multiply(new BigDecimal("100000"));
		}
		return r;
	}

	/**
	 * 解析投影输出文件，获得投影对象
	 * 
	 * @param xmlContent
	 * @return
	 
	public static ProjectionInfo getProjection(String xmlContent)
	{
		List rootNodes = getRootNodes(xmlContent);

		Element et = null;

		ProjectionInfo npi = new ProjectionInfo();
		OrbitInfo orbitInfo = new OrbitInfo();

		// 预定义xml节点名与轨道对象方法名对应关系
		String[][] OrbitFields =
		{
				{ "OrbitFilename", "setPath" },
				{ "Satellite", "setSatellite" },
				{ "Sensor", "setSensor" },
				{ "Level", "setDatalevel" },
				{ "DayOrNight", "setDayornight" },
				{ "ObservationDate", "setObservationdate" },
				{ "ObservationTime", "setObservationtime" },
				{ "Station", "setStation" },
				{ "OrbitIdentify", "setOrbitidentify" } };

		for (int i = 0; i < rootNodes.size(); i++)
		{
			et = (Element) rootNodes.get(i);// 循环依次得到子元素
			String ename = et.getName();
			if (ename.equals("OutputFiles"))
			{
				List subFileNode = et.getChildren(); // 得到内层子节点
				if (subFileNode != null)
					for (int sj = 0; sj < subFileNode.size(); sj++)
					{
						List fileAttrNotes = ((Element) subFileNode.get(sj)).getChildren();
						if (fileAttrNotes != null && fileAttrNotes.size() > 0)
						{

							for (int noteIndex = 0; noteIndex < fileAttrNotes.size(); noteIndex++)
							{
								Element ele = (Element) fileAttrNotes.get(noteIndex);
								if ("OutputFilename".equals(ele.getName()))
								{
									npi.setDatapath(ele.getValue());
								} else if ("Thumbnail".equals(ele.getName()))
								{
									npi.setThumbnail(ele.getValue());
								} else if ("ExtendFiles".equals(ele.getName()))
								{
									npi.setExtendfiles(ele.getValue());
								} else if ("ResolutionX".equals(ele.getName()))
								{
									npi.setResolutionx(formatResolution(ele.getValue()));
								} else if ("ResolutionY".equals(ele.getName()))
								{
									npi.setResolutiony(formatResolution(ele.getValue()));
								} else if ("Length".equals(ele.getName()))
								{
									npi.setFilesize(
											new BigDecimal(ele.getValue() == null || "".equals(ele.getValue()) ? "0"
													: ele.getValue()));
								} else if ("Envelope".equals(ele.getName()))
								{
									npi.setFilesize(
											new BigDecimal(ele.getValue() == null || "".equals(ele.getValue()) ? "0"
													: ele.getValue()));
									String envelopeName = ele.getAttributeValue("name");
									String envelopeminx = ele.getAttributeValue("minx");
									String envelopemaxx = ele.getAttributeValue("maxx");
									String envelopeminy = ele.getAttributeValue("miny");
									String envelopemaxy = ele.getAttributeValue("maxy");

									npi.setMaxx(new BigDecimal(
											envelopemaxx == null || "".equals(envelopemaxx) ? "0" : envelopemaxx));
									npi.setMaxy(new BigDecimal(
											envelopemaxy == null || "".equals(envelopemaxy) ? "0" : envelopemaxy));
									npi.setMinx(new BigDecimal(
											envelopeminx == null || "".equals(envelopeminx) ? "0" : envelopeminx));
									npi.setMiny(new BigDecimal(
											envelopeminy == null || "".equals(envelopeminy) ? "0" : envelopeminy));
								}
							}

							npi.setCreateTime(new Date());

						}
					}
			} else if (ename.equals("ProjectionIdentify"))
			{
				// npi.setProjectionIdentify(et.getValue());
				npi.setProjectionidentify(et.getValue());
			} else if (ename.equals("log"))
			{
				List subLogNode = et.getChildren(); // 得到内层子节点
				npi.setMessagetype(((Element) subLogNode.get(0)).getValue());
				npi.setMessageinfo(((Element) subLogNode.get(1)).getValue());
			} else
			{
				// 轨道数据信息
				for (String[] f : OrbitFields)
				{
					if ("Sensor".equals(et.getName()))
					{
						String sensorName = et.getValue();
						orbitInfo.setSensor(format(sensorName));
						break;
					}
					if ("Length".equals(et.getName()))
					{
						String fileSize = et.getValue();
						orbitInfo.setFilesize(new BigDecimal(fileSize == null ? "0" : fileSize));
						break;
					}
					if (f[0].equals(et.getName()))
					{
						Method method = null;
						try
						{
							
							method = orbitInfo.getClass().getMethod(f[1], String.class);
						} catch (NoSuchMethodException e)
						{
							e.printStackTrace();
						} catch (SecurityException e)
						{
							e.printStackTrace();
						}
						try
						{
							method.invoke(orbitInfo, et.getValue());
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
						{
							e.printStackTrace();
						}
						break;
					}
				}

				System.out.println(et.getName() + ": be fond");
			}

		}
		npi.setOrbitInfo(orbitInfo);
		return npi;
	}

	public static String format(String sensorName)
	{
		if (!StringUtils.isBlank(sensorName) && sensorName.length() < 5)
		{
			int xCount = 5 - sensorName.length();
			do
			{
				sensorName += "X";
			} while (--xCount > 0);

		}
		return sensorName;
	}
*/
	public static Map<String, String > getProjection(String xmlPath)
	{
		List rootNodes = getRootNodes(xmlPath);

		Element et = null;
		Map<String, String > map = new HashMap<String, String>(); 
		// 预定义xml节点名与轨道对象方法名对应关系
		String[][] OrbitFields =
		{
				{ "OrbitFilename", "setPath" },
				{ "Satellite", "setSatellite" },
				{ "Sensor", "setSensor" },
				{ "Level", "setDatalevel" },
				{ "DayOrNight", "setDayornight" },
				{ "ObservationDate", "setObservationdate" },
				{ "ObservationTime", "setObservationtime" },
				{ "Station", "setStation" },
				{ "OrbitIdentify", "setOrbitidentify" } };
		if(null != rootNodes) {
			for (int i = 0; i < rootNodes.size(); i++)
			{
				et = (Element) rootNodes.get(i);// 循环依次得到子元素
				String ename = et.getName();
				if (ename.equals("OutputFiles"))
				{
					List subFileNode = et.getChildren(); // 得到内层子节点
					if (subFileNode != null)
						for (int sj = 0; sj < subFileNode.size(); sj++)
						{
							List fileAttrNotes = ((Element) subFileNode.get(sj)).getChildren();
							if (fileAttrNotes != null && fileAttrNotes.size() > 0)
							{
								
								for (int noteIndex = 0; noteIndex < fileAttrNotes.size(); noteIndex++)
								{
									Element ele = (Element) fileAttrNotes.get(noteIndex);
									if ("OutputFilename".equals(ele.getName()))
									{
										map.put("OutputFilename", ele.getValue());
									} else if ("Thumbnail".equals(ele.getName()))
									{
										map.put("Thumbnail", ele.getValue());
									} else if ("ExtendFiles".equals(ele.getName()))
									{
										map.put("ExtendFiles", ele.getValue());
									} else if ("ResolutionX".equals(ele.getName()))
									{
										map.put("ResolutionX", formatResolution(ele.getValue())+"");
									} else if ("ResolutionY".equals(ele.getName()))
									{
										map.put("ResolutionY", formatResolution(ele.getValue())+"");
									} else if ("Length".equals(ele.getName()))
									{
										map.put("ResolutionY", new BigDecimal(ele.getValue() == null || "".equals(ele.getValue()) ? "0"
												: ele.getValue())+"");
									} else if ("Envelope".equals(ele.getName()))
									{
										String envelopeName = ele.getAttributeValue("name");
										String envelopeminx = ele.getAttributeValue("minx");
										String envelopemaxx = ele.getAttributeValue("maxx");
										String envelopeminy = ele.getAttributeValue("miny");
										String envelopemaxy = ele.getAttributeValue("maxy");
										map.put("minx", envelopeminx);
										map.put("maxx", envelopemaxx);
										map.put("miny", envelopeminy);
										map.put("maxy", envelopemaxy);
									}
								}
								
								
							}
						}
				} else if (ename.equals("ProjectionIdentify"))
				{
					// npi.setProjectionIdentify(et.getValue());
					map.put("ProjectionIdentify", et.getValue());
				} else if (ename.equals("log"))
				{
					List subLogNode = et.getChildren(); // 得到内层子节点
					map.put("loglevel", ((Element) subLogNode.get(0)).getValue());
					map.put("loginfo", ((Element) subLogNode.get(1)).getValue());
				} else
				{
					// 轨道数据信息
					for (String[] f : OrbitFields)
					{
						if ("Sensor".equals(et.getName()))
						{
							map.put("ProjectionIdentify", et.getValue());
							break;
						}
						if ("Length".equals(et.getName()))
						{
							map.put("Length", et.getValue());
							break;
						}
					}
					
					System.out.println(et.getName() + ": be fond");
				}
				
			}
			return map;
		}
		return map;
	}
	public static void main(String[] args) {
		String xmlPath = "d:/FY3B_VIRRX_GBAL_L1_20181005_0322_1000M_MS.HDF.xml";
		Map<String, String > map = XmlParseUtil.getProjection(xmlPath);
		System.out.println(map.get("OutputFilename"));
	}
}
