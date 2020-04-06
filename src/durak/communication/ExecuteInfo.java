package durak.communication;

import durak.game.IGame;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;

class ExecuteInfo {
	private Message           message;
	private SocketChannel     socketChannel;
	private InetSocketAddress remoteAddress;
	private DatagramChannel   datagramChannel;
	private Connector         connector;
	private Object            attachment;

	ExecuteInfo(Connector connector, SocketChannel socketChannel, Message message) {
		this.connector = connector;
		this.socketChannel = socketChannel;
		this.message = message;
	}

	ExecuteInfo(Connector connector,
	            DatagramChannel datagramChannel,
	            InetSocketAddress remoteAddress,
	            Object attachment,
	            Message message) {
		this.connector = connector;
		this.datagramChannel = datagramChannel;
		this.remoteAddress = remoteAddress;
		this.attachment = attachment;
		this.message = message;
	}

	Optional<IGame> getGame() {
		IGame game = connector.gameBySocketChannel(socketChannel);
		return Optional.ofNullable(game);
	}

	Message getMessage() {
		return message;
	}

	SocketChannel getSocketChannel() {
		return socketChannel;
	}

	Connector getConnector() {
		return connector;
	}

	InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}

	Object getAttachment() {
		return attachment;
	}
}
