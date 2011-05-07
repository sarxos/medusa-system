package org.xinotes.iniparser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 * There is a open source project named [ini4j] for processing Windows .ini
 * configuration files. However, I found it an overkill for my purposes. So here
 * is my simple implementation of a .ini parser. It mimics the standard
 * java.util.Properties class with enhancements to get and set properties by
 * section name.
 * 
 * There are only a few simple rules:
 * 
 * <ul>
 * <li>Leading and trailing spaces are trimmed from section names, property
 * names and property values.</li>
 * <li>Section names are enclosed between [ and ].</li>
 * <li>Properties following a section header belong to that section</li>
 * <li>Properties defined before the appearance of any section headers are
 * considered global properties and should be set and get with no section names.
 * </li>
 * <li>You can use either equal sign (=) or colon (:) to assign property values</li>
 * <li>Comments begin with either a semicolon (;), or a sharp sign (#) and
 * extend to the end of line. It doesn't have to be the first character.</li>
 * <li>A backslash (\) escapes the next character (e.g., \# is a literal #, \\
 * is a literal \)</li>
 * <li>If the last character of a line is backslash (\), the value is continued
 * on the next line with new line character included.</li>
 * </ul>
 * 
 * @see http://www.xinotes.org/notes/note/407/
 * @author Dr. Xi
 */
public class INIProperties {

	private Properties globalProperties;

	private Map<String, Properties> properties;

	enum ParseState {
		NORMAL,
		ESCAPE,
		ESC_CRNL,
		COMMENT
	}

	public INIProperties() {
		globalProperties = new Properties();
		properties = new HashMap<String, Properties>();
	}

	/**
	 * Load ini as properties from input stream.
	 */
	public void load(InputStream in) throws IOException {
		int bufSize = 4096;
		byte[] buffer = new byte[bufSize];
		int n = in.read(buffer, 0, bufSize);

		ParseState state = ParseState.NORMAL;
		boolean section_open = false;
		String current_section = null;
		String key = null, value = null;
		StringBuilder sb = new StringBuilder();
		while (n >= 0) {
			for (int i = 0; i < n; i++) {
				char c = (char) buffer[i];

				if (state == ParseState.COMMENT) { // comment, skip to end of
													// line
					if ((c == '\r') || (c == '\n')) {
						state = ParseState.NORMAL;
					} else {
						continue;
					}
				}

				if (state == ParseState.ESCAPE) {
					sb.append(c);
					if (c == '\r') {
						// if the EOL is \r\n, \ escapes both chars
						state = ParseState.ESC_CRNL;
					} else {
						state = ParseState.NORMAL;
					}
					continue;
				}

				switch (c) {
					case '[': // start section
						sb = new StringBuilder();
						section_open = true;
						break;

					case ']': // end section
						if (section_open) {
							current_section = sb.toString().trim();
							sb = new StringBuilder();
							properties.put(current_section, new Properties());
							section_open = false;
						} else {
							sb.append(c);
						}
						break;

					case '\\': // escape char, take the next char as is
						state = ParseState.ESCAPE;
						break;

					case '#':
					case ';':
						state = ParseState.COMMENT;
						break;

					case '=': // assignment operator
					case ':':
						if (key == null) {
							key = sb.toString().trim();
							sb = new StringBuilder();
						} else {
							sb.append(c);
						}
						break;

					case '\r':
					case '\n':
						if ((state == ParseState.ESC_CRNL) && (c == '\n')) {
							sb.append(c);
							state = ParseState.NORMAL;
						} else {
							if (sb.length() > 0) {
								value = sb.toString().trim();
								sb = new StringBuilder();

								if (key != null) {
									if (current_section == null) {
										this.setProperty(key, value);
									} else {
										this.setProperty(current_section, key, value);
									}
								}
							}
							key = null;
							value = null;
						}
						break;

					default:
						sb.append(c);
				}
			}
			n = in.read(buffer, 0, bufSize);
		}
	}

	/**
	 * Get global property by name.
	 */
	public String getProperty(String name) {
		return globalProperties.getProperty(name);
	}

	/**
	 * Set global property.
	 */
	public void setProperty(String name, String value) {
		globalProperties.setProperty(name, value);
	}

	/**
	 * Return iterator of global properties.
	 */
	@SuppressWarnings("unchecked")
	public Iterator<String> properties() {
		return new IteratorFromEnumeration<String>(
					(Enumeration<String>) globalProperties.propertyNames());
	}

	/**
	 * Get property value for specified section and name. Returns null if
	 * section or property does not exist.
	 */
	public String getProperty(String section, String name) {
		Properties p = properties.get(section);
		return p == null ? null : p.getProperty(name);
	}

	/**
	 * Set property value for specified section and name. Creates section if not
	 * existing.
	 */
	public void setProperty(String section, String name, String value) {
		Properties p = properties.get(section);
		if (p == null) {
			p = new Properties();
			properties.put(section, p);
		}
		p.setProperty(name, value);
	}

	/**
	 * Return property iterator for specified section. Returns null if specified
	 * section does not exist.
	 */
	@SuppressWarnings("unchecked")
	public Iterator<String> properties(String section) {
		Properties p = properties.get(section);
		if (p == null) {
			return null;
		}
		return new IteratorFromEnumeration<String>(
					(Enumeration<String>) p.propertyNames());
	}

	/**
	 * Return iterator of names of section.
	 */
	public Iterator<String> sections() {
		return properties.keySet().iterator();
	}

	/**
	 * Dumps properties to output stream.
	 */
	public void dump(PrintStream out) throws IOException {
		// Global properties
		Iterator<String> props = this.properties();
		while (props.hasNext()) {
			String name = props.next();
			out.printf("%s = %s\n", name, dumpEscape(getProperty(name)));
		}

		// sections
		Iterator<String> sections = this.sections();
		while (sections.hasNext()) {
			String section = sections.next();
			out.printf("\n[%s]\n", section);
			props = this.properties(section);
			while (props.hasNext()) {
				String name = props.next();
				out.printf("%s = %s\n", name, dumpEscape(getProperty(section, name)));
			}
		}
	}

	private static String dumpEscape(String s) {
		return s.replaceAll("\\\\", "\\\\\\\\")
				.replaceAll(";", "\\\\;")
				.replaceAll("#", "\\\\#")
				.replaceAll("(\r?\n|\r)", "\\\\$1");
	}

	// private class used to coerce Enumerator to Iterator.
	private static class IteratorFromEnumeration<E> implements Iterator<E> {

		private Enumeration<E> e;

		public IteratorFromEnumeration(Enumeration<E> e) {
			this.e = e;
		}

		@Override
		public boolean hasNext() {
			return e.hasMoreElements();
		}

		@Override
		public E next() {
			return e.nextElement();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Can't change underlying enumeration");
		}
	}

	public static void main(String[] args) throws IOException {
		INIProperties props = new INIProperties();
		InputStream in = new BufferedInputStream(new FileInputStream("test.ini"));
		props.load(in);
		in.close();

		props.dump(System.out);
	}
}

// FOR TEST PUROPOSE

// ; Global properties
// story = Snow White and the Seven Dwarfs
// year = 1937
// url = www.imdb.com/title/tt0029583/
// ; A backslash at the end of the line escapes the new line
// ; character, the property value continues to the next line.
// ; Since the ; character (after Dwarfs) starts a comment
// ; (either at the beginning or middle of the line, we have
// ; to escape it with a backslash.
// plot = Snow White, pursued by a jealous queen, hides with the Dwarfs\; \
// the queen feeds her a poison apple, but Prince Charming \
// awakens her with a kiss. # this is a comment
//
// # This is also a comment line
// # The first : can also be used as assignment operator
// Tagline: Walt Disney's New characters in his first full-length production!
// file = C:\\local\\snowwhite.mpg
//
// ; Bashful
// [bashful]
// weight = 45.7
// height = 98.8
// age = 67
// homePage = http://snowwhite.tale/~bashful
//
// ; Doc
// [doc]
// weight = 49.5
// height = 87.7
// age = 63
// homePage = http://doc.dwarfs
