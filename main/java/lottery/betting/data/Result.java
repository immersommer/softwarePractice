package lottery.betting.data;

import java.io.Serializable;

public interface Result<T> extends Comparable<T>, Serializable {
//	static final long serialVersionUID = -7565813772046251748L;
	int compareTo(T o);
	String toString();
}
