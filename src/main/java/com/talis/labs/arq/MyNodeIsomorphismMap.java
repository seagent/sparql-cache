/*
 * Copyright © 2011 Talis Systems Ltd.
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

package com.talis.labs.arq;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.util.NodeIsomorphismMap;

public class MyNodeIsomorphismMap extends NodeIsomorphismMap {

	private Map<Node, Node> map = new HashMap<Node, Node>();

	private Node get(Node key) {
		return map.get(key);
	}

	private void put(Node key, Node value) {
		map.put(key, value);
	}

	public boolean makeIsomorhpic(Node n1, Node n2) {
		if (n1.isBlank() && n2.isBlank()) {
			Node other = get(n1);
			if (other == null) {
				put(n1, n2);
				return true;
			}
			return other.equals(n2);
		} else if (n1.isVariable() && n2.isVariable()) {
			Node other = get(n1);
			if (other == null) {
				put(n1, n2);
				return true;
			}
			return other.equals(n2);
		}
		return n1.equals(n2);
	}

}