/*
 * ModuleParserRSS2.java
 *
 * Created on November 20, 2005, 6:15 PM
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License or the Apache
 * License at your discretion.
 *
 *  Copyright (C) 2005  Robert Cooper, Temple of the Screaming Penguin
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rometools.feed.module.cc.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.rometools.feed.module.cc.CreativeCommonsImpl;
import org.rometools.feed.module.cc.types.License;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;

/**
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class ModuleParserRSS2 implements ModuleParser {

    private static final Namespace NS = Namespace.getNamespace(CreativeCommonsImpl.RSS2_URI);

    /** Creates a new instance of ModuleParserRSS2 */
    public ModuleParserRSS2() {
    }

    @Override
    public Module parse(final Element element, final Locale locale) {
        final CreativeCommonsImpl module = new CreativeCommonsImpl();
        // Do channel global
        {
            Element root = element;
            while (!root.getName().equals("channel") && !root.getName().equals("feed")) {
                root = root.getParentElement();
            }
            final ArrayList<License> licenses = new ArrayList<License>();
            List<Element> items = null;
            if (root.getName().equals("channel")) {
                items = root.getChildren("item");
            } else {
                items = root.getChildren("entry");
            }

            final Iterator<Element> iit = items.iterator();
            while (iit.hasNext()) {
                final Element item = iit.next();
                final List<Element> licenseTags = item.getChildren("license", NS);
                final Iterator<Element> lit = licenseTags.iterator();
                while (lit.hasNext()) {
                    final Element licenseTag = lit.next();
                    final License license = License.findByValue(licenseTag.getTextTrim());
                    if (!licenses.contains(license)) {
                        ;
                    }
                    licenses.add(license);
                }
            }
            if (licenses.size() > 0) {
                module.setAllLicenses(licenses.toArray(new License[0]));
            }
        }
        // do element local
        final ArrayList<License> licenses = new ArrayList<License>();
        final List<Element> licenseTags = element.getChildren("license", NS);
        final Iterator<Element> it = licenseTags.iterator();
        while (it.hasNext()) {
            final Element licenseTag = it.next();
            licenses.add(License.findByValue(licenseTag.getTextTrim()));
        }
        if (licenses.size() > 0) {
            module.setLicenses(licenses.toArray(new License[0]));
        }

        if (module.getLicenses() != null && module.getAllLicenses() != null) {
            return module;
        } else {
            return null;
        }
    }

    @Override
    public String getNamespaceUri() {
        return CreativeCommonsImpl.RSS2_URI;
    }
}
