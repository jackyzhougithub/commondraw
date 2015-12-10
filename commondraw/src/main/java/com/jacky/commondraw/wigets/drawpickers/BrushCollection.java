package com.jacky.commondraw.wigets.drawpickers;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by $ zhoudeheng on 2015/12/9.
 * Email zhoudeheng@qccr.com
 */
public class BrushCollection {
    private static final String BRUSH_LIST = "brushlist";
    private static final String BRUSH_NODE = "brush";
    private static final String BRUSH_FILE = "brush.xml";
    private static final int BRUSH_MAX_NUM = 12;

    private List<BrushInfo> mBrushList = null;
    private Context mContext = null;
    private boolean isDataChanged = false;

    public BrushCollection(Context context)
    {
        mContext = context;
        mBrushList = new ArrayList<BrushInfo>();

        getBrushsFromXml();
    }

    private void getBrushsFromXml(){
        FileInputStream inputStream = null;

        try {
            File file = new File(mContext.getFilesDir().getPath() + "/" + BRUSH_FILE);
            if(!file.exists()) return;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            inputStream = mContext.openFileInput(BRUSH_FILE);
            Document document = builder.parse(inputStream);

            Element root = document.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(BRUSH_NODE);

            BrushInfo brushInfo = null;
            for(int i = 0; i < nodes.getLength(); i++){
                Element element = (Element)(nodes.item(i));

                float width = Float.parseFloat(element.getAttribute(MetaData.PREFERENCE_DOODLE_STROKE));
                int toolCode = Integer.parseInt(element.getAttribute(MetaData.PREFERENCE_DOODLE_BRUSH));
                int alpha = Integer.parseInt(element.getAttribute(MetaData.PREFERENCE_DOODLE_ALPHA));
                boolean isPalette = Boolean.parseBoolean(element.getAttribute(MetaData.PREFERENCE_IS_PALETTE));
                boolean isColorMode = Boolean.parseBoolean(element.getAttribute(MetaData.PREFERENCE_IS_COLOR_MODE));
                int color = Integer.parseInt(element.getAttribute(MetaData.PREFERENCE_PALETTE_COLOR));
                int index = Integer.parseInt(element.getAttribute(MetaData.PREFERENCE_DOODLE_SEL_COLOR_INDEX));
                int x = Integer.parseInt(element.getAttribute(MetaData.PREFERENCE_PALETTE_COLORX));
                int y = Integer.parseInt(element.getAttribute(MetaData.PREFERENCE_PALETTE_COLORY));

                brushInfo = new BrushInfo(width, toolCode, alpha, isPalette, isColorMode, color, index, x, y);
                mBrushList.add(brushInfo);
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }catch (ParserConfigurationException e) {
            e.printStackTrace();
        }finally{
            try {
                if(inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setBrushsToXMl()
    {
        if(!isDataChanged) return;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement(BRUSH_LIST);
            doc.appendChild(root);

            for(BrushInfo brushInfo : mBrushList)
            {
                Element element = doc.createElement(BRUSH_NODE);
                element.setAttribute(MetaData.PREFERENCE_DOODLE_STROKE, String.valueOf(brushInfo.getStrokeWidth()));
                element.setAttribute(MetaData.PREFERENCE_DOODLE_BRUSH, String.valueOf(brushInfo.getDoodleToolCode()));
                element.setAttribute(MetaData.PREFERENCE_DOODLE_ALPHA, String.valueOf(brushInfo.getDoodleToolAlpha()));
                element.setAttribute(MetaData.PREFERENCE_IS_PALETTE, String.valueOf(brushInfo.getIsPalette()));
                element.setAttribute(MetaData.PREFERENCE_IS_COLOR_MODE, String.valueOf(brushInfo.getIsColorMode()));
                element.setAttribute(MetaData.PREFERENCE_PALETTE_COLOR, String.valueOf(brushInfo.getCustomColor()));
                element.setAttribute(MetaData.PREFERENCE_DOODLE_SEL_COLOR_INDEX, String.valueOf(brushInfo.getSelectedColorIndex()));
                element.setAttribute(MetaData.PREFERENCE_PALETTE_COLORX, String.valueOf(brushInfo.getCurrentX()));
                element.setAttribute(MetaData.PREFERENCE_PALETTE_COLORY, String.valueOf(brushInfo.getCurrentY()));

                root.appendChild(element);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            DOMSource xmlSource = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");

            FileOutputStream outStream = mContext.openFileOutput(BRUSH_FILE, mContext.MODE_PRIVATE);
            StreamResult xmlResult = new StreamResult(outStream);
            transformer.transform(xmlSource, xmlResult);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }finally{

        }
    }

    public List<BrushInfo> getBrushList()
    {
        return mBrushList;
    }

    public int brushSize()
    {
        return mBrushList.size();
    }

    public BrushInfo getBrush(int index)
    {
        return mBrushList.get(index);
    }

    public Boolean addBrush(float width, int toolCode, int alpha, boolean isPalette, boolean isColorMode,
                            int color, int index, int cx, int cy)
    {
        //SharedPreferences sharedPreference = mContext.getSharedPreferences(MetaData.PREFERENCE_NAME, Activity.MODE_PRIVATE);
        //int colorX = sharedPreference.getInt(MetaData.PREFERENCE_PALETTE_COLORX, 0) ;
        //int colorY = sharedPreference.getInt(MetaData.PREFERENCE_PALETTE_COLORY, 0) ;
        BrushInfo brushInfo = new BrushInfo(width, toolCode, alpha, isPalette, isColorMode, color, index, cx, cy);
        boolean isExist = false;
        for(BrushInfo brush : mBrushList)
        {
            if(brush.getStrokeWidth() == width && brush.getDoodleToolCode() == toolCode
                    && brush.getDoodleToolAlpha() == alpha && brush.getIsPalette() == isPalette
                    && brush.getIsColorMode() == isColorMode && brush.getCustomColor() == color
                    && brush.getSelectedColorIndex() == index)
            {
                isExist = true;
                break;
            }
        }

        if(isExist)
            return false;
        else
        {
            mBrushList.add(brushInfo);
            isDataChanged = true;
            return true;
        }
    }

    public void deleteBrush(BrushInfo brushInfo)
    {
        isDataChanged = true;
        mBrushList.remove(brushInfo);
    }

    public void deleteBrush(int index)
    {
        isDataChanged = true;
        mBrushList.remove(index);
    }

    public boolean isBrushFull()
    {
        if(mBrushList.size() >= BRUSH_MAX_NUM)
            return true;
        else
            return false;
    }
}
