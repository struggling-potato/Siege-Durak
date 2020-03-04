package durak.communication;

import java.io.Serializable;
import java.util.Objects;

public class ServerInfo implements Serializable {
	private String serverName;
	private int    port;

	public ServerInfo(String serverName, int port) {
		this.serverName = serverName;
		this.port = port;
	}

	@Override
	public int hashCode() {
		return Objects.hash(serverName, port);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ServerInfo that = (ServerInfo) o;
		return port == that.port &&
		       serverName.equals(that.serverName);
	}

	@Override
	public String toString() {
		return serverName + ":" + port;
	}

	String getServerName() {
		return serverName;
	}

	int getPort() {
		return port;
	}
}
