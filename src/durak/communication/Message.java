package durak.communication;

import java.io.Serializable;

interface Message extends Serializable {
	void process(ExecuteInfo info);
}
