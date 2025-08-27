package com.trackstudio.tools;

import java.util.List;

/**
 * TODO: comment
 * @author parsentev
 * @since 13.03.2015
 */
public class AutocompleteItem {
	private String query;
	private List<Item> suggestions;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<Item> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(List<Item> suggestions) {
		this.suggestions = suggestions;
	}

	public static final class Item {
		private String value;
		private String label;

		public Item(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}
}
