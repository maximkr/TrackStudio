/**
 * Copyright 2005 Darren L. Spurgeon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ajaxtags.tags;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: Document type OptionsBuilder.
 * 
 * @author Darren Spurgeon
 * @version $Revision$ $Date$
 */
public class OptionsBuilder {

  private Map parameters = new HashMap();

  private Map parameterQuotes = new HashMap();

  public OptionsBuilder add(String parameter, String value, boolean quoted) {
    this.parameters.put(parameter, value);
    this.parameterQuotes.put(parameter, Boolean.valueOf(quoted));
    return this;
  }

  public OptionsBuilder remove(String parameter) {
    this.parameters.remove(parameter);
    this.parameterQuotes.remove(parameter);
    return this;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (Iterator iter = this.parameters.keySet().iterator(); iter.hasNext();) {
      String key = (String) iter.next();
      String value = (String) this.parameters.get(key);
      boolean quoted = ((Boolean) this.parameterQuotes.get(key)).booleanValue();
      sb.append(key).append(": ");
      if (quoted) {
        sb.append('\"').append(value).append('\"');
      } else {
        sb.append(value);
      }
      if (iter.hasNext()) {
        sb.append(',');
      }
      sb.append('\n');
    }
    return sb.toString();
  }

}
