/**
 * Copyright (C) 2007 Google Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tornaco.apps.shortx.services.audio.cast.safesax;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * Parsing utility methods.
 */
public class Parsers {

    /**
     * Parses XML from the given reader with namespace support enabled.
     */
    public static void parse(Reader in, ContentHandler contentHandler)
            throws SAXException, IOException {
        try {
            XMLReader reader
                    = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setContentHandler(contentHandler);
            reader.parse(new InputSource(in));
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
