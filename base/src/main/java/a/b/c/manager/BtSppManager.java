package a.b.c.manager;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

import a.b.c.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BtSppManager implements BluetoothAdapter.LeScanCallback {

	private static final String NAME_SECURE = "BluetoothChatSecure";

	private static final UUID UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECT_FAILURE = 3;
	public static final int STATE_CONNECTED = 4;
	public static final int STATE_DISCONNECTED = 5;

	private BluetoothAdapter bluetoothAdapter;
	private AcceptThread acceptThread;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	private StateCallback stateCallback;
	private DataCallback dataCallback;
	private DiscoveryCallback discoveryCallback;
	private int state;
	private boolean isStart, isDiscovery;

	private static BtSppManager sManager;

	private Context context;

	public interface StateCallback {
		void state(int state);
	}

	public interface DataCallback {
		void data(int len, byte[] bytes);
	}

	public interface DiscoveryCallback {
		void device(BluetoothDevice device);

		void finished();
	}

	public void setStateCallback(StateCallback stateCallback) {
		if (this.stateCallback == null || this.stateCallback != stateCallback) {
			this.stateCallback = stateCallback;
		}
	}

	public void setDataCallback(DataCallback dataCallback) {
		if (this.dataCallback == null || this.dataCallback != dataCallback) {
			this.dataCallback = dataCallback;
		}
	}

	public void setDiscoveryCallback(DiscoveryCallback discoveryCallback) {
		if (this.discoveryCallback == null || this.discoveryCallback != discoveryCallback) {
			this.discoveryCallback = discoveryCallback;
		}
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		if (discoveryCallback != null) discoveryCallback.device(device);
	}

	private BtSppManager(Context context) {
		this.context = context;
	}

	public static BtSppManager getInstance(Context context) {
		if (sManager == null) {
			sManager = new BtSppManager(context);
		}
		return sManager;
	}

	public void onCreate(Bundle savedInstanceState) {
		openBluetooth();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						if (discoveryCallback != null) discoveryCallback.device(device);
					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					if (discoveryCallback != null) {
						if (isDiscovery) {
							stopDiscovery();
							discoveryCallback.finished();
						}
					}
				}
			}
		}, filter);
	}

	public void onDestroy() {
		stopDiscovery();
		bluetoothAdapter = null;
		stateCallback = null;
		dataCallback = null;
		discoveryCallback = null;
	}

	public BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;
	}

	public boolean openBluetooth() {
		if (bluetoothAdapter == null) {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		if (bluetoothAdapter == null) {
			LogManager.tS(context, context.getString(R.string.no_bluetooth));
			return false;
		}
		if (!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();
		return true;
	}

	public void see300s() {
		if (bluetoothAdapter != null && bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			context.startActivity(discoverableIntent);
		}
	}

	private synchronized void setState(int state) {
		this.state = state;
	}

	public synchronized int getState() {
		return state;
	}

	public void startDiscovery() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (openBluetooth()) {
					while (!bluetoothAdapter.isEnabled()) {
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (discoveryCallback != null) {
						Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
						if (pairedDevices.size() > 0) {
							for (BluetoothDevice device : pairedDevices) {
								discoveryCallback.device(device);
							}
						}
						bluetoothAdapter.startDiscovery();
						if (Build.VERSION.SDK_INT > 17) {
							bluetoothAdapter.startLeScan(sManager);
						}
					}
					if (stateCallback != null) stateCallback.state(STATE_NONE);
				}
			}
		}).start();
		isDiscovery = true;
	}

	public void stopDiscovery() {
		if (bluetoothAdapter != null && discoveryCallback != null) {
			bluetoothAdapter.cancelDiscovery();
			discoveryCallback.finished();
			if (Build.VERSION.SDK_INT > 17) {
				bluetoothAdapter.stopLeScan(sManager);
			}
		}
		if (stateCallback != null) setState(STATE_NONE);
		isDiscovery = false;
	}

	public synchronized void start() {

		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		setState(STATE_LISTEN);
		if (stateCallback != null) stateCallback.state(STATE_LISTEN);

		if (acceptThread == null) {
			acceptThread = new AcceptThread();
			acceptThread.start();
		}

		isStart = true;
	}

	public synchronized void connect(final BluetoothDevice device) {

		if (state == STATE_CONNECTING) {
			if (connectThread != null) {
				connectThread.cancel();
				connectThread = null;
			}
		}

		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		connectThread = new ConnectThread(device);
		connectThread.start();

		setState(STATE_CONNECTING);
		if (stateCallback != null) stateCallback.state(STATE_CONNECTING);
	}

	public synchronized void connected(BluetoothSocket socket) {

		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}

		connectedThread = new ConnectedThread(socket);
		connectedThread.start();

		setState(STATE_CONNECTED);
		if (stateCallback != null) stateCallback.state(STATE_CONNECTED);
	}

	public synchronized void stop() {

		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}

		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}

		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}

		setState(STATE_NONE);
		if (stateCallback != null) stateCallback.state(STATE_NONE);

		isStart = false;
	}

	public void shutdown() {
		long time = 10;
		try {
			connectedThread.write(String.format("AA%02x%02xFF", (byte) (time / 256), (byte) (time % 256)).toUpperCase().getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private class AcceptThread extends Thread {
		private BluetoothServerSocket bluetoothServerSocket;

		public AcceptThread() {
			if (openBluetooth()) {
				while (!bluetoothAdapter.isEnabled()) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_SECURE);
				} catch (IOException ignored) {
				}
			}
		}

		public void run() {

			setName("AcceptThreadSecure");

			while (state != STATE_CONNECTED) {
				BluetoothSocket socket;
				try {
					socket = bluetoothServerSocket.accept();
				} catch (IOException e) {
					return;
				}

				if (socket != null) {
					synchronized (BtSppManager.this) {
						switch (state) {
							case STATE_LISTEN:
							case STATE_CONNECTING:
								connected(socket);
								break;
							case STATE_NONE:
							case STATE_CONNECTED:
								try {
									socket.close();
								} catch (IOException ignored) {
								}
								break;
						}
					}
				}
			}
		}

		public void cancel() {
			try {
				bluetoothServerSocket.close();
			} catch (IOException ignored) {
			}
		}
	}

	private class ConnectThread extends Thread {

		private BluetoothSocket bluetoothSocket;

		public ConnectThread(BluetoothDevice device) {
			try {
				bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID_SECURE);
			} catch (IOException ignored) {
			}
		}

		public void run() {

			setName("ConnectThreadSecure");

			if (openBluetooth()) {
				while (!bluetoothAdapter.isEnabled()) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				bluetoothAdapter.cancelDiscovery();
			}

			try {
				bluetoothSocket.connect();
			} catch (IOException e) {
				try {
					bluetoothSocket.close();
				} catch (IOException ignored) {
				}
				setState(STATE_CONNECT_FAILURE);
				if (stateCallback != null) stateCallback.state(STATE_CONNECT_FAILURE);
				return;
			}

			synchronized (BtSppManager.this) {
				connectThread = null;
			}

			connected(bluetoothSocket);
		}

		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException ignored) {
			}
		}
	}

	private class ConnectedThread extends Thread {
		private BluetoothSocket bluetoothSocket;
		private InputStream inputStream;
		private OutputStream outputStream;

		public ConnectedThread(BluetoothSocket socket) {

			bluetoothSocket = socket;

			try {
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
			} catch (IOException ignored) {
			}
		}


		public void run() {

			byte[] bytes = new byte[1024];
			int len;

			while (true) {
				try {
					len = inputStream.read(bytes);
					if (dataCallback != null) {
						dataCallback.data(len, bytes);
					}
				} catch (IOException e) {
					setState(STATE_DISCONNECTED);
					if (stateCallback != null) stateCallback.state(STATE_DISCONNECTED);
					return;
				}
			}
		}

		public void write(byte[] buffer) {
			try {
				outputStream.write(buffer);
			} catch (IOException ignored) {
			}
		}

		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException ignored) {
			}
		}
	}
}
