package utils;

public interface Actor {
	void processMessage(Message msg);

	void setMessageHandler(ProcessMessage handler);
}
