/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.xmltool;

import org.junit.Ignore;
import org.junit.Test;

import com.mycila.xmltool.AbstractTest;
import com.vsc.util.FileUtil;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class XMLDocValidatorTest extends AbstractTest {
	String xsd = "C:\\xmldata\\xsd\\";
	String xml = "C:\\xmldata\\checkout\\testTonghop.xml";
    @Test
    public void validate_URL() throws Exception {
//        XMLTag doc = XMLDoc.from(readString(xml), false);
    	XMLTag doc = XMLDoc.from(FileUtil.readFile(new File(xml)), false);
        ValidationResult res = Utils.validate(doc.toDocument(), getClass().getResource(xsd));
        assertFalse(res.hasError());
        assertFalse(res.hasWarning());
    }

    @Test
    public void validate_URL_invalid() throws Exception {
        XMLTag doc = XMLDoc.from(readString("doc3.xhtml"), false);
        ValidationResult res = Utils.validate(doc.toDocument(), getClass().getResource(xsd));
        assertTrue(res.hasError());
        assertFalse(res.hasWarning());
        System.out.println(Arrays.deepToString(res.getErrorMessages()));
    }

    @Test
    @Ignore
    //when we ignore namespace, validation becomes unpredictable on diffrent jdk versions
    public void validate_URL_ignoringNS() throws Exception {
        XMLTag doc = XMLDoc.from(readString("doc2.xhtml"), true);
        ValidationResult res = Utils.validate(doc.toDocument(), getClass().getResource(xsd));
        System.out.println(Arrays.deepToString(res.getErrorMessages()));
        assertFalse(res.hasError());
        assertFalse(res.hasWarning());
    }

    @Test
    @Ignore
    //when we ignore namespace, validation becomes unpredictable on diffrent jdk versions
    public void validate_URL_invalid_ignoringNS() throws Exception {
        XMLTag doc = XMLDoc.from(readString("doc3.xhtml"), true);
        ValidationResult res = Utils.validate(doc.toDocument(), getClass().getResource(xsd));
        assertTrue(res.hasError());
        assertFalse(res.hasWarning());
        System.out.println(Arrays.deepToString(res.getErrorMessages()));
    }
}
