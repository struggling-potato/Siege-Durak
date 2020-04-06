package durak.communication;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

class MessageInfo {
	private SocketChannel     receiver;
	private DatagramChannel   datagramChannel;
	private InetSocketAddress remoteAddress;
	private Message           message;

	MessageInfo(SocketChannel receiver, Message message) {
		this.receiver = receiver;
		this.message = message;
	}

	MessageInfo(DatagramChannel datagramChannel, InetSocketAddress remoteAddress, Message message) {
		this.datagramChannel = datagramChannel;
		this.remoteAddress = remoteAddress;
		this.message = message;
	}

	SocketChannel getReceiver() {
		return receiver;
	}

	Message getMessage() {
		return message;
	}

	DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}

	InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
}
