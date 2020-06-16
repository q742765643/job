package com.htht.job.core.util;

import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.canvas.mxICanvas2D;
import com.mxgraph.reader.mxSaxOutputHandler;
import com.mxgraph.util.mxUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by zzj on 2018/3/19.
 */
public class MxImageExport {

    /**
     * 流程设计器传来的图片转为字节
     *
     * @param xml
     * @return
     * @throws Exception
     */
    public static byte[] getImageByte(String xml, int width, int height) throws Exception {
//		int width = 864;	//	width     width of the created image
//		int height = 445;	//	height    height of the created image

        BufferedImage image = mxUtils.createBufferedImage(width, height, Color.WHITE);

        // Creates handle and configures anti-aliasing
        Graphics2D g2 = image.createGraphics();
        mxUtils.setAntiAlias(g2, true, true);

        // Parses request into graphics canvas
        mxGraphicsCanvas2D gc2 = new mxGraphicsCanvas2D(g2);
        gc2.setAutoAntiAlias(true);
        parseXmlSax(xml, gc2);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        byte[] b = os.toByteArray();
        return b;
    }

    protected static void parseXmlSax(String xml, mxICanvas2D canvas) throws SAXException, ParserConfigurationException, IOException {
        // Creates SAX handler for drawing to graphics handle
        mxSaxOutputHandler handler = new mxSaxOutputHandler(canvas);

        // Creates SAX parser for handler
        XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
                .getXMLReader();
        reader.setContentHandler(handler);

        // Renders XML data into image
        InputSource inputSource = new InputSource(new StringReader(xml));
        inputSource.setEncoding("utf-8");
        reader.parse(inputSource);
    }
}