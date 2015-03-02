package co.gi9.ilsaul.games.pa.ObjectCompare.model.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Element {
	private Map<String, List<String>> properties;
	private List<Element> objects;
	private Element parent;
	private String name;

	public Element(Element parent, String name) {
		properties = new HashMap<String, List<String>>();
		objects = new ArrayList<Element>();

		this.parent = parent;
		this.name = name;
	}

	public void addProperty(String key, String value) {
		List<String> values = null;

		if (properties.containsKey(key)) {
			values = properties.get(key);
		} else {
			values = new ArrayList<String>();
			properties.put(key, values);
		}
		values.add(value);
		//properties.put(key, value);
	}

	public void addObect(Element o) {
		objects.add(o);
	}

	public Element getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "Element [name=" + name + ", properties=" + properties + "]";
	}

	public List<Element> getObjects() {
		return objects;
	}

	public String getName() {
		return name;
	}

}
