package com.hourlyweather.yrno;

import org.xmlpull.v1.XmlPullParser;

/**
 * Utilities for parsing the yrno xml
 * @author dhgonsalves
 *
 */
public class XmlParserUtil {

    /**
     * Returns the XML attribute by name, not as fast as using attribute indexes
     * but much more durable in case of future api changes
     * 
     * @param xpp
     * @param name
     * @return
     */
    public static String getAttributeByName(XmlPullParser xpp, String name) {
	for (int i = 0; i < xpp.getAttributeCount(); i++)
	    if (name.equalsIgnoreCase(xpp.getAttributeName(i)))
		return xpp.getAttributeValue(i);

	return null;
    }
}
