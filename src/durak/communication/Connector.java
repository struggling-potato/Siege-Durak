package durak.communication;

import durak.game.IGame;
import durak.game.IPlayer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Connector {
	private static final int INT_SIZE = Integer.SIZE >> 3;

	private Integer lastDummyId            = 0;
	private int     udpBufferMinSize       = 512;
	private int     scannerPort            = 7439;
	private boolean isInfoSenderRegistered = false;

	private Thread             provider;
	private Thread             executor;
	private Queue<MessageInfo> providerQueue    = new ArrayDeque<>();
	private Queue<ExecuteInfo> executorQueue    = new ArrayDeque<>();
	private Selector           providerSelector = Selector.open();
	private ByteBuffer         providerBuffer   = ByteBuffer.allocate(INT_SIZE);

	private ArrayList<InetSocketAddress>                              serverAddresses          = new ArrayList<>();
	private HashMap<InetSocketAddress, ServerSocketChannel>           serverAddressToSocket    = new HashMap<>();
	private HashMap<ServerInfo, IGame>                                establishedConnections   = new HashMap<>();
	private HashMap<SocketChannel, IGame>                             socketToGame             = new HashMap<>();
	private HashMap<Integer, IPlayer>                                 dummyIdToPlayer          = new HashMap<>();
	private HashMap<IPlayer, Integer>                                 playerToDummyId          = new HashMap<>();
	private HashMap<PlayerDummy, Integer>                             playerDummyToDummyId     = new HashMap<>();
	private HashMap<Integer, PlayerDummy>                             dummyIdToPlayerDummy     = new HashMap<>();
	private HashMap<SocketChannel, ArrayList<PlayerDummy>>            socketToPlayerDummy      = new HashMap<>();
	private HashMap<GameDummy, SocketChannel>                         gameDummyToGameSocket    = new HashMap<>();
	private HashMap<PlayerDummy, SocketChannel>                       playerSockets            = new HashMap<>();
	private HashMap<GameDummy, ArrayList<Integer>>                    gameDummyToPlayerDummyId = new HashMap<>();
	private HashMap<SocketChannel, GameDummy>                         gameSocketToGameDummy    = new HashMap<>();
	private HashMap<Consumer<ArrayList<ServerInfo>>, DatagramChannel> listenerToScanner        = new HashMap<>();

	{
		provider = new Thread(() -> {
			while (!provider.isInterrupted()) {
				synchronized (providerQueue) {
					int retries = 2;
					if (providerQueue.isEmpty()) {
						try {
							while (0 != retries && providerQueue.isEmpty()) {
								providerQueue.wait(5);
								retries--;
							}
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					while (!providerQueue.isEmpty()) {
						MessageInfo message = providerQueue.remove();
						if (null != message.getDatagramChannel()) {
							sendMessage(message.getDatagramChannel(), message.getRemoteAddress(), message.getMessage());
						}
						else if (message.getReceiver().isConnected()) {
							sendMessage(message.getReceiver(), message.getMessage());
						}
						else
							providerQueue.add(message);
					}
				}

				synchronized (providerSelector) {
					try {
						providerSelector.selectNow((key) -> {
							Optional<SocketChannel> lastClient = Optional.empty();
							if (key.channel() instanceof SocketChannel) {
								lastClient = Optional.ofNullable((SocketChannel) key.channel());
							}
							try {
								if (key.isAcceptable()) {
									accept(key);
								}
								else if (key.isConnectable()) {
									connect(key);
								}
								else if (key.isReadable()) {
									read(key);
								}
							}
							catch (IOException e) {
								System.err.println(e.getMessage());
								lastClient.ifPresent(client -> cleanUpClient(client, key));
							}
						});
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		provider.setDaemon(true);
		provider.start();
		executor = new Thread(() -> {
			while (!executor.isInterrupted()) {
				Optional<ExecuteInfo> info = Optional.empty();
				synchronized (executorQueue) {
					try {
						while (executorQueue.isEmpty()) {
							executorQueue.wait();
						}
						info = Optional.ofNullable(executorQueue.remove());
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				info.ifPresent(i -> i.getMessage().process(i));
			}
		});
		executor.setDaemon(true);
		executor.start();
	}

	public Connector() throws IOException {}

	public Consumer<ArrayList<ServerInfo>> registerBroadcastServerScanner(Consumer<ArrayList<ServerInfo>> infoListener) throws
			IOException {
		return registerBroadcastServerScanner(infoListener, 2);
	}

	public Consumer<ArrayList<ServerInfo>> registerBroadcastServerScanner(Consumer<ArrayList<ServerInfo>> infoListener,
	                                                                      int secondsPeriod) throws
			IOException {
		DatagramChannel scannerChannel = DatagramChannel.open();
		scannerChannel.configureBlocking(false);
		scannerChannel.socket().setBroadcast(true);

		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Message message = (info) -> {
					InetSocketAddress scannerAddress  = info.getRemoteAddress();
					DatagramChannel   datagramChannel = info.getDatagramChannel();

					ArrayList<InetSocketAddress> serverInfos = info.getConnector().getServerAddresses();

					Message sendServers = (inf) -> {
						InetSocketAddress serverAddress = inf.getRemoteAddress();
						((Consumer<ArrayList<ServerInfo>>) inf.getAttachment()).accept(serverInfos.stream()
						                                                                          .map(inetSocketAddress -> new ServerInfo(
								                                                                          serverAddress.getHostString(),
								                                                                          inetSocketAddress
										                                                                          .getPort()))
						                                                                          .collect(Collectors.toCollection(
								                                                                          ArrayList::new)));
					};

					info.getConnector().enqueueMessage(new MessageInfo(datagramChannel, scannerAddress, sendServers));

				};

				enqueueMessage(new MessageInfo(scannerChannel,
				                               new InetSocketAddress("localhost", scannerPort),
				                               message));

				enqueueMessage(new MessageInfo(scannerChannel,
				                               new InetSocketAddress("255.255.255.255", scannerPort),
				                               message));

			}
		}, 0, secondsPeriod * 1000);

		synchronized (providerSelector) {
			scannerChannel.register(providerSelector, SelectionKey.OP_READ, infoListener);
			listenerToScanner.put(infoListener, scannerChannel);
		}

		return infoListener;
	}

	ArrayList<InetSocketAddress> getServerAddresses() {
		return serverAddresses;
	}

	private void enqueueMessage(MessageInfo messageInfo) {
		synchronized (providerQueue) {
			providerQueue.add(messageInfo);
		}
	}

	public void unregisterBroadcastServerScanner(Consumer<ArrayList<ServerInfo>> infoListener) throws
			NullPointerException {
		synchronized (providerSelector) {
			listenerToScanner.remove(infoListener).keyFor(providerSelector).cancel();
		}
	}

	public Optional<IGame> connectToServer(ServerInfo serverInfo) throws IOException, InterruptedException {
		System.out.println("Connecting to server " + serverInfo + "...");
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		SocketAddress serverAddress = new InetSocketAddress(serverInfo.getServerName(), serverInfo.getPort());
		synchronized (providerSelector) {
			if (establishedConnections.containsKey(serverInfo))
				return Optional.ofNullable(establishedConnections.get(serverInfo));
			socketChannel.connect(serverAddress);
			socketChannel.register(providerSelector, SelectionKey.OP_CONNECT, serverAddress);
			try {
				while (0 != (socketChannel.keyFor(providerSelector).interestOps() & SelectionKey.OP_CONNECT)) {
					providerSelector.wait();
				}
			}
			catch (Exception e) {
				System.err.println(e);
				return null;
			}
		}

		GameDummy gameDummy = new GameDummy(this);
		gameDummyToGameSocket.put(gameDummy, socketChannel);
		gameSocketToGameDummy.put(socketChannel, gameDummy);
		establishedConnections.put(serverInfo, gameDummy);
		return Optional.of(gameDummy);
	}

	public void registerServer(IGame server, int port) throws IOException, InterruptedException {
		System.out.println("Registering server on port " + port + "...");
		InetSocketAddress   serverAddress       = new InetSocketAddress("localhost", port);
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		synchronized (providerSelector) {
			serverAddresses.add(serverAddress);
			serverAddressToSocket.put(serverAddress, serverSocketChannel);
			serverSocketChannel.bind(serverAddress);
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(providerSelector, serverSocketChannel.validOps(), server);
			registerServerInfoSender();
		}
	}

	private void registerServerInfoSender() throws IOException {
		if (isInfoSenderRegistered)
			return;

		DatagramChannel datagramChannel = DatagramChannel.open();
		datagramChannel.configureBlocking(false);
		datagramChannel.socket().setReuseAddress(true);
		datagramChannel.bind(new InetSocketAddress("localhost", scannerPort));

		synchronized (providerSelector) {
			datagramChannel.register(providerSelector, SelectionKey.OP_READ);
			isInfoSenderRegistered = true;
		}
	}

	public void unregisterServer(int port) {
		// TODO: make it work
		System.out.println("Unregistering server on port " + port + "...");

		Optional<InetSocketAddress> serverAddress =
				serverAddresses.stream().filter(address -> address.getPort() == port).findFirst();
		serverAddress.ifPresent(address -> {
			synchronized (serverAddresses) {
				serverAddresses.remove(address);
			}
			synchronized (providerSelector) {
				serverAddressToSocket.remove(address).keyFor(providerSelector).cancel();
			}
		});
	}

	int getPlayerDummyId(IPlayer player) {
		synchronized (lastDummyId) {
			int dummyId = playerToDummyId.getOrDefault(player, -1);
			if (-1 != dummyId)
				return dummyId;
			int lDI;
			synchronized (dummyIdToPlayerDummy) {
				while (null != dummyIdToPlayerDummy.get(lastDummyId))
					++lastDummyId;
				lDI = ++lastDummyId;
			}
			playerToDummyId.put(player, lDI);
			return lDI;
		}
	}

	void cleanUpDummiesOnGameDummyCleanUp(ArrayList<IPlayer> players) {
		synchronized (lastDummyId) {
			synchronized (dummyIdToPlayerDummy) {
				synchronized (playerToDummyId) {
					players.forEach(player -> {
						dummyIdToPlayerDummy.remove(playerDummyToDummyId.remove(player));
					});
				}
			}
		}
	}

	int getPlayerDummyId(PlayerDummy playerDummy) {
		synchronized (lastDummyId) {
			int dummyId = playerDummyToDummyId.getOrDefault(playerDummy, -1);
			if (-1 != dummyId)
				return dummyId;
			synchronized (dummyIdToPlayerDummy) {
				while (null != dummyIdToPlayerDummy.get(lastDummyId))
					++lastDummyId;
				dummyId = lastDummyId++;
			}
			return playerDummyToDummyId.put(playerDummy, dummyId);
		}
	}

	PlayerDummy playerDummyById(IGame game, int playerDummyId, ExecuteInfo info) {
		synchronized (dummyIdToPlayerDummy) {
			PlayerDummy playerDummy = dummyIdToPlayerDummy.get(playerDummyId);
			if (null == playerDummy) {
				playerDummy = new PlayerDummy(this);
				dummyIdToPlayerDummy.put(playerDummyId, playerDummy);
				playerDummyToDummyId.put(playerDummy, playerDummyId);
				playerSockets.put(playerDummy, info.getSocketChannel());
				ArrayList<PlayerDummy> playerDummyArrayList =
						socketToPlayerDummy.getOrDefault(info.getSocketChannel(), new ArrayList<>());
				playerDummyArrayList.add(playerDummy);
				socketToPlayerDummy.put(info.getSocketChannel(), playerDummyArrayList);
			}
			return playerDummy;
		}
	}

	void tirePlayerWithDummy(GameDummy gameDummy, IPlayer player, int dummyId) {
		synchronized (dummyIdToPlayer) {
			if (null != playerByDummyId(dummyId))
				System.err.println("dummyId " + dummyId + " collision; replacing player on " + player);
			dummyIdToPlayer.put(dummyId, player);
			ArrayList<Integer> dummyIds = gameDummyToPlayerDummyId.getOrDefault(gameDummy, new ArrayList<>());
			dummyIds.add(dummyId);
			gameDummyToPlayerDummyId.put(gameDummy, dummyIds);
		}
	}

	IPlayer playerByDummyId(int dummyId) {
		return dummyIdToPlayer.get(dummyId);
	}

	void cleanUpDummyOnPlayerDisconnected(int dummyId) {
		synchronized (lastDummyId) {
			synchronized (dummyIdToPlayer) {
				synchronized (playerToDummyId) {
					playerToDummyId.remove(dummyIdToPlayer.remove(dummyId));
				}
			}
		}
	}

	void sendMessageToGame(GameDummy gameDummy, Message message) {
		SocketChannel toGameSocket = gameDummyToGameSocket.get(gameDummy);
		if (null == toGameSocket)
			System.err.println("Socket for gameDummy " + gameDummy + " not found");
		else
			enqueueMessage(new MessageInfo(toGameSocket, message));
	}

	void sendMessageToPlayer(PlayerDummy playerDummy, Message message) {
		SocketChannel toPlayerSocket = playerSockets.get(playerDummy);
		if (null == toPlayerSocket)
			System.err.println("Socket for playerDummy " + playerDummy + " not found");
		else
			enqueueMessage(new MessageInfo(toPlayerSocket, message));
	}

	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
		SocketChannel       client       = (serverSocket).accept();
		client.configureBlocking(false);
		client.register(providerSelector, SelectionKey.OP_READ);
		socketToGame.put(client, (IGame) key.attachment());
	}

	private void connect(SelectionKey key) {
		Optional<SocketChannel> lastClient = Optional.ofNullable((SocketChannel) key.channel());
		try {
			SocketChannel client = (SocketChannel) key.channel();
			if (client.finishConnect()) {
				synchronized (providerSelector) {
					InetSocketAddress serverAddress = (InetSocketAddress) client.getRemoteAddress();
					client.register(providerSelector,
					                SelectionKey.OP_READ,
					                new ServerInfo(serverAddress.getHostString(), serverAddress.getPort()));
					providerSelector.notifyAll();
				}
			}
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
			lastClient.ifPresent(client -> {
				synchronized (providerSelector) {
					providerSelector.notify();
				}
			});
		}
	}

	private void read(SelectionKey key) throws IOException {
		Optional<SocketChannel> lastClient = Optional.empty();
		if (key.channel() instanceof DatagramChannel) {
			DatagramChannel datagramChannel = ((DatagramChannel) key.channel());
			DatagramInfo    datagramInfo    = receiveMessage(datagramChannel);
			datagramInfo.getMessage().ifPresent(msg -> {
				synchronized (executorQueue) {
					executorQueue.add(new ExecuteInfo(this,
					                                  datagramChannel,
					                                  datagramInfo.getRemoteAddress(),
					                                  key.attachment(),
					                                  msg));
					executorQueue.notify();
				}
			});
		}
		else {
			SocketChannel client = (SocketChannel) key.channel();
			lastClient = Optional.ofNullable(client);
			Optional<Message> message = receiveMessage(client);
			message.ifPresent(msg -> {
				synchronized (executorQueue) {
					executorQueue.add(new ExecuteInfo(this, client, msg));
					executorQueue.notify();
				}
			});
		}
	}

	private DatagramInfo receiveMessage(DatagramChannel client) throws IOException {
		Message           message = null;
		InetSocketAddress remoteAddress;
		synchronized (providerBuffer) {
			providerBuffer = ByteBuffer.allocate(udpBufferMinSize);
			remoteAddress = (InetSocketAddress) client.receive(providerBuffer);
			providerBuffer.flip();
			int        messageSize   = providerBuffer.getInt();
			ByteBuffer messageBuffer = ByteBuffer.allocate(messageSize);
			try {
				providerBuffer.get(messageBuffer.array());
				ObjectInputStream inputStream =
						new ObjectInputStream(new ByteArrayInputStream(messageBuffer.array()));
				message = (Message) inputStream.readObject();
			}
			catch (BufferUnderflowException e) {
				System.err.println("udpBufferMinSize(" + udpBufferMinSize + ") not enough to receive datagram");
				udpBufferMinSize *= 2;
				System.err.println("New udpBufferMinSize: " + udpBufferMinSize);
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			providerBuffer.limit(providerBuffer.capacity());
		}
		DatagramInfo datagramInfo = new DatagramInfo(message, remoteAddress);
		return datagramInfo;
	}

	private Optional<Message> receiveMessage(SocketChannel client) throws IOException {
		Message message = null;
		synchronized (providerBuffer) {
			loadBytesIntoBuffer(client, providerBuffer, INT_SIZE);
			int messageSize = providerBuffer.getInt();
			providerBuffer = ByteBuffer.allocate(messageSize);
			loadBytesIntoBuffer(client, providerBuffer, messageSize);
			ObjectInputStream inputStream =
					new ObjectInputStream(new ByteArrayInputStream(providerBuffer.array()));
			try {
				message = (Message) inputStream.readObject();
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return Optional.of(message);
	}

	private void loadBytesIntoBuffer(ByteChannel channel, ByteBuffer buffer, int count) throws IOException {
		setBufferOffset(buffer, count);
		channel.read(buffer);
		setBufferOffset(buffer, count);
	}

	private void setBufferOffset(ByteBuffer buffer, int count) {
		buffer.position(buffer.capacity() - count);
	}

	private void sendMessage(SocketChannel client, Message message) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream    objectOutputStream    = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(message);
			byte[] byteMessage = byteArrayOutputStream.toByteArray();
			int    messageSize = byteMessage.length;
			setBufferOffset(providerBuffer, INT_SIZE);
			providerBuffer.putInt(messageSize);
			setBufferOffset(providerBuffer, INT_SIZE);
			client.write(providerBuffer);
			providerBuffer = ByteBuffer.wrap(byteMessage);
			client.write(providerBuffer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void cleanUpClient(SocketChannel client, SelectionKey key) {
		try {
			System.err.println("Disconnecting " + client.getRemoteAddress() + "...");
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		synchronized (providerSelector) {
			client.keyFor(providerSelector).cancel();
			IGame game = gameBySocketChannel(client);
			if (null != game) {
				socketToGame.remove(client);
				ArrayList<PlayerDummy> clientDummyArrayList = socketToPlayerDummy.remove(client);
				clientDummyArrayList.forEach(clientDummy -> {
					clientDummy.onCleanUp(game);
					playerSockets.remove(clientDummy);
					dummyIdToPlayerDummy.remove(playerDummyToDummyId.remove(clientDummy));
				});
			}
			else {
				ServerInfo serverInfo = (ServerInfo) key.attachment();
				establishedConnections.remove(serverInfo);
				GameDummy          gameDummy      = gameSocketToGameDummy.remove(client);
				gameDummyToGameSocket.remove(gameDummy);
				ArrayList<Integer> playerDummyIds = gameDummyToPlayerDummyId.remove(gameDummy);
				ArrayList<IPlayer> players =
						playerDummyIds.stream().map(dummyId -> dummyIdToPlayer.remove(dummyId)).collect(
								Collectors.toCollection(ArrayList::new));
				gameDummy.onCleanUp(players);
			}
			providerQueue.removeIf(messageInfo -> {
				SocketChannel receiver = messageInfo.getReceiver();
				if (null == receiver)
					return false;
				return receiver.equals(client);
			});
		}
	}

	IGame gameBySocketChannel(SocketChannel socketChannel) {
		return socketToGame.get(socketChannel);
	}

	private void sendMessage(DatagramChannel client, InetSocketAddress remoteAddress, Message message) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream    objectOutputStream    = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(message);
			byte[] byteMessage = byteArrayOutputStream.toByteArray();
			int    messageSize = byteMessage.length;
			int    bytesLeft   = INT_SIZE + messageSize;
			synchronized (providerBuffer) {
				providerBuffer = ByteBuffer.allocate(bytesLeft);
				providerBuffer.putInt(messageSize);
				providerBuffer.put(byteMessage);
				providerBuffer.flip();
				do {
					int sent = client.send(providerBuffer, remoteAddress);
					bytesLeft -= sent;
				} while (0 != bytesLeft);
				providerBuffer.limit(providerBuffer.capacity());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class DatagramInfo {
		private Message           message;
		private InetSocketAddress remoteAddress;

		private DatagramInfo(Message message, InetSocketAddress remoteAddress) {
			this.message = message;
			this.remoteAddress = remoteAddress;
		}

		Optional<Message> getMessage() {
			return Optional.ofNullable(message);
		}

		InetSocketAddress getRemoteAddress() {
			return remoteAddress;
		}
	}
}
