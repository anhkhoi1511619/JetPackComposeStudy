package com.example.jetpackcomposeexample.controller.tcp_ip_v2;

import android.util.Log;

import com.example.jetpackcomposeexample.utils.DataTypeConverter;
import com.example.jetpackcomposeexample.utils.TLog_Sync;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;


/**
 * 概要：TCP送信マネージャー
 */
public class SendManager {
	
	// Raise this up may increase the risk of memory error
	// reduce this down may affect the communication speed
	static final int THREAD_POOL_SIZE_FOR_EACH_IP = 50;

	static final HashMap<String, ScheduledThreadPoolExecutor> commExecutor = new HashMap<>();
	private static final Map<String, Thread> threadMap = new ConcurrentHashMap<>();
	public static Future<Boolean> sendAsync(CommPackageDTO lcpComSrv, String address, int port, boolean critical) {
		if(!commExecutor.containsKey(address)) {
			addNewThreadPool(address);
		}
		ScheduledThreadPoolExecutor executor = commExecutor.get(address);
		if(executor.getActiveCount() > executor.getCorePoolSize()/2 && !critical) {
			TLog_Sync.d( "SendManager", "[TX]About to send to "+address+", connection pool is half way full "+executor.getActiveCount()+", drop this package");
			//if(executor.getActiveCount() >= 25)checkThreadStatus();
			return null;
		}
		if(executor.getQueue().size() > 20) {
			TLog_Sync.d( "SendManager", "[TX]About to send to "+address+", connection queue is full "+executor.getQueue().size());
		}
		if(executor.getActiveCount() >= executor.getCorePoolSize() && critical) {
			TLog_Sync.d( "SendManager","[TX]About to send to "+address+", connection pool is full "+executor.getActiveCount()+", this package may be stuck");
		}
		return executor.submit(() -> send(lcpComSrv, address, port, critical));
	}
	/**
	 * 概要：アプリ起動時、それぞれのIP Addressによって、最大の５０スレッドを作成する
	 * */
	static void addNewThreadPool (String address)
	{
		commExecutor.put(address,
				new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE_FOR_EACH_IP, new ThreadFactory() {
					private int count = 0;
					@Override
					public Thread newThread(Runnable r) {
						count++;
						String threadName = "Thread-" + count;
						Thread thread = new Thread(r, threadName);
						threadMap.put(threadName, thread);  // 監視リストを保存する
						return thread;
					}})
		);
	}
	/***
	 * 概要：TCPでデータ送信実施
	 *
	 * @param lcpComSrv	送信コマンド
	 * @param address	IPアドレス送信先
	 * @param port		Port送信先
	 * @return			送信状況：成功　OR　失敗
	 */
	public static boolean send(CommPackageDTO lcpComSrv, String address, int port, boolean critical) throws InterruptedException{
		String log = "[TX]Communicating to " + address + ":" + port +
				" command "+lcpComSrv.getTxCmd()+
				" data "+lcpComSrv.getTxData()+
				" in Thread : " + Thread.currentThread().getName();
		if(critical) {
			TLog_Sync.d( "SendManager",log);
		} else {
			//Log.v(TAG_TCP_2, log);
		}
		Thread currentThread = Thread.currentThread();
		threadMap.put(currentThread.getName(), currentThread);  // 実装されているスレッドをthreadMapリストにアップデート
		//　TCP接続設定確立
		ConnectionManager conn = ConnectionManager.getInstance();
		//　TCP接続状況確認　→　失敗なら何もしない
		if (!conn.createIfNotExist(address, port)) {
			TLog_Sync.d("SendManager", "Client of Reader-Controller Comm not connected to " +address+":"+port);
			return false;
		}
		ConnectionManager.Config connection = ConnectionManager.getInstance().get(address);
		synchronized(connection) {
			//　データ送信実施
			if (!txTransmitData(lcpComSrv, address)) {
				TLog_Sync.d( "SendManager", "[TX]Transmit command failed, " + lcpComSrv.getTxCmd() + " -> " + address);
				ConnectionManager.getInstance().close(address);
				return false;
			}
			//　データ取得実施
			if (!rxTransmitData(address)) {
				TLog_Sync.d( "SendManager","[TX-RES]Receive command error, " + lcpComSrv.getRxCmd() + " <- " + address);
				ConnectionManager.getInstance().close(address);
				return false;
			}
		}
		log = "[TX]successfully communicated with " + connection.getSocket().getRemoteSocketAddress().toString();
		if(critical) {
			TLog_Sync.d( "SendManager",log);
		} else {
			//Log.v(TAG_TCP_2, log);
		}
		return true;
	}

	/***
	 * 概要：データ取得実施
	 *
	 * @param address	送信先
	 * @return			取得済みデータ長さ
	 */
	static boolean rxTransmitData(String address) {
		byte[] tRxByteData = new byte[2048];
		byte[] rxByteData = new byte[2048];
		int rxSize;
		int rxIndex = 0;

		// TCP接続設定取り出す
		ConnectionManager conn = ConnectionManager.getInstance();
		ConnectionManager.Config tcpConn = conn.get(address);
		//　データ取得行う
		int rxDataSize = 0;
		try {
			rxDataSize = tcpConn.getInBuf().read(tRxByteData);
		} catch (SocketTimeoutException e) {
			Log.d( "SendManager", "[TX]Socket timed out "+
					tcpConn.getSocket().getRemoteSocketAddress().toString()+" : "+e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			TLog_Sync.d( "SendManager", "[TX]Error while reading data from socket "+
					tcpConn.getSocket().getRemoteSocketAddress().toString()+" : "+e.getMessage());
			e.printStackTrace();
			return false;
		}
        // 有無交データ確認行う
//		if (rxDataSize < 8) {
//			return false;
//		}
		//　データ抽出行う
		System.arraycopy(tRxByteData, 0, rxByteData, rxIndex, rxDataSize);
		rxIndex += rxDataSize;
		//　データサイズ取り出す　→　10進数に交換
		//rxSize = DataTypeConverter.castInt(Arrays.copyOfRange(rxByteData, 1, 3)) +
		//		EX_DEV_FIX_PART_LENGTH;
		rxSize = 1;
		if (rxIndex >= rxSize) {
			byte[] rxBuf = new byte[2048];
			// バッファの初期化
			Arrays.fill(rxBuf, (byte) 0);
			// ブッファから電文を抽出する
			rxBuf = Arrays.copyOfRange(rxByteData, 0, rxDataSize);
			// データ処理行い
			final int DUMMY_PORT = 0;
			Log.d( "SendManager", "[TX-RES]received " +
					DataTypeConverter.format(rxBuf) +
					" bytes from " +
					tcpConn.getSocket().getRemoteSocketAddress().toString() );
			// ログ用文字を交換する
		}
		return true;
	}

	/***
	 * 概要：データ送信実施
	 *
	 * @param lcpComSrv	送信コマンド
	 * @param address	送信先
	 */
	static boolean txTransmitData(CommPackageDTO lcpComSrv, String address) {
		//　送信用データ準備
		byte[] data = lcpComSrv.makeTxData();
		// TCP接続設定取り出す
		ConnectionManager.Config config = ConnectionManager.getInstance().get(address);
		//　データ送信
		try {
			Log.d("SendManager","[TX]Sending: " + DataTypeConverter.format(data) + " length : " +
					data.length + " to : " + config.getSocket().getRemoteSocketAddress().toString());
			config.getOutBuf().write(data);
			config.getOutBuf().flush();
			Log.d( "SendManager", "[TX]Done sending data to " + config.getSocket().getRemoteSocketAddress().toString());
		} catch (IOException e) {
			TLog_Sync.d( "SendManager", "[TX]Error while writing data to "+
					config.getSocket().getRemoteSocketAddress().toString()+": "+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
