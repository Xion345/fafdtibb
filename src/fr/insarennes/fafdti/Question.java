package fr.insarennes.fafdti;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.Text;

public class Question implements WritableComparable<Question> {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		// Only use textValue or doubleValue according to
		// type to generate hash code
		if (this.type == AttrType.DISCRETE || this.type == AttrType.TEXT) {
		result = prime * result
				+ ((doubleValue == null) ? 0 : doubleValue.hashCode());
		} else {
			result = prime * result
				+ ((textValue == null) ? 0 : textValue.hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Question other = (Question) obj;
		if (col != other.col)
			return false;
		if (type != other.type)
			return false;
		// Only compare textValue or doubleValue according to
		// type
		if (this.type == AttrType.DISCRETE || this.type == AttrType.TEXT) {
			if (textValue == null) {
				if (other.textValue != null)
					return false;
			} else if (!textValue.equals(other.textValue))
				return false;
		} else {
			if (doubleValue == null) {
				if (other.doubleValue != null)
					return false;
			} else if (!doubleValue.equals(other.doubleValue))
				// FIXME Maybe which should do an approximate comparison here
				// as double values can change a little
				return false;
		}
		return true;
	}

	int col;
	AttrType type;
	DoubleWritable doubleValue;
	// !!! NOTE IMPORTANTE :
	// Chaine représentant :
	// - valeur d'un attribut discret
	// - valeur d'un attribut texte
	Text textValue;

	public static final String DELIMITER = ":";

	public int getCol() {
		return col;
	}

	public Question() {
		this.doubleValue = new DoubleWritable();
		this.textValue = new Text();
	}

	public Question(int col, AttrType type, double value) {
		this();
		this.col = col;
		if (type != AttrType.CONTINUOUS) {
			throw new IllegalArgumentException(
					"Constructor with double value can only be used if"
							+ " attr type is continuous");
		}
		this.type = type;
		this.doubleValue.set(value);
	}

	public Question(int col, AttrType type, String value) {
		this();
		this.col = col;
		if (type == AttrType.CONTINUOUS) {
			throw new IllegalArgumentException(
					"Constructor with string value can only be used if"
							+ " attr type is discrete or text");
		}
		this.type = type;
		this.textValue.set(value);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// Char representing type of AttrValue :
		// D : Discret
		// C : Continuous
		// T : Text
		char typeChar = in.readChar();
		switch (typeChar) {
		case 'C':
			this.type = AttrType.CONTINUOUS;
			this.textValue.readFields(in);
			break;
		case 'T':
			this.type = AttrType.TEXT;
			this.textValue.readFields(in);
			break;
		case 'D':
			this.type = AttrType.DISCRETE;
			this.doubleValue.readFields(in);
			break;
		default:
			throw new IllegalStateException("Bad type description character "
					+ typeChar);
		}
	}

	public static char typeToChar(AttrType type) {
		char typeChar = 'C';
		switch (type) {
		case CONTINUOUS:
			typeChar = 'C';
			break;
		case DISCRETE:
			typeChar = 'D';
			break;
		case TEXT:
			typeChar = 'T';
			break;
		}
		return typeChar;
	}

	public static AttrType charToType(char typeChar) {
		switch (typeChar) {
		case 'C':
			return AttrType.CONTINUOUS;
		case 'T':
			return AttrType.TEXT;
		case 'D':
			return AttrType.DISCRETE;
		default:
			throw new IllegalArgumentException(
					"Bad type description character " + typeChar);
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		switch (this.type) {
		case CONTINUOUS:
			out.writeChar('C');
			this.doubleValue.write(out);
		case DISCRETE:
			out.writeChar('D');
			this.textValue.write(out);
		case TEXT:
			out.writeChar('T');
			this.textValue.write(out);
		}
	}

	@Override
	public int compareTo(Question other) {
		if(this.col != other.col)
			return -1;
		if (this.type != other.type)
			return -1;
		if (this.type == AttrType.DISCRETE || this.type == AttrType.TEXT)
			return this.textValue.compareTo(other.textValue);
		else
			return 0;
	}

	public String toString() {
		String outStr = "";
		outStr += Question.typeToChar(this.type);
		outStr += Question.DELIMITER;
		if (this.type == AttrType.DISCRETE || this.type == AttrType.TEXT) {
			outStr += this.getTextValue();
		} else {
			outStr += this.getDoubleValue();
		}
		return outStr;
	}

	public void fromString(String strRepr) {
		String[] fields = strRepr.split(Question.DELIMITER);
		this.type = Question.charToType(fields[0].charAt(0));
		if (this.type == AttrType.DISCRETE 
				|| this.type == AttrType.TEXT) {
			this.textValue.set(fields[1]);
		} else {
			this.doubleValue.set(Double.parseDouble(fields[1]));
		}
	}

	public double getDoubleValue() {
		return this.doubleValue.get();
	}

	public String getTextValue() {
		return this.textValue.toString();
	}

	public AttrType getType() {
		return type;
	}
}