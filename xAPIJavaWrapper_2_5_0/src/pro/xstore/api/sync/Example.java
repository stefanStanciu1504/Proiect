package pro.xstore.api.sync;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Clock;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.*;
import pro.xstore.api.message.response.*;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.ServerData.ServerEnum;

public class Example {
	public void runExample(ServerEnum server, Credentials credentials) throws Exception {
		try {
			SyncAPIConnector connector = new SyncAPIConnector(server);
			LoginResponse loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
			System.out.println(loginResponse);
			if (loginResponse != null && loginResponse.getStatus())
			{
				StreamingListener sl = new StreamingListener() {
					@Override
					public void receiveTradeRecord(STradeRecord tradeRecord) {
						System.out.println("Stream trade record: " + tradeRecord);
					}
	
					@Override
					public void receiveTickRecord(STickRecord tickRecord) {
						System.out.print("Stream tick record: " + tickRecord);
					}

					@Override
					public void receiveBalanceRecord(SBalanceRecord balanceRecord) {
						System.out.println("CEVA: " + balanceRecord);
					}
				};
//				try {
//					File myObj = new File("/home/stefan/Desktop/file.txt");
//					myObj.createNewFile();
//				} catch (IOException e) {
//						System.out.println("An error occurred.");
//						e.printStackTrace();
//				}
//
//				AllSymbolsResponse allSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
//				FileWriter myFile = new FileWriter("/home/stefan/Desktop/file.txt");
//
//				for (SymbolRecord idx : allSymbols.getSymbolRecords()) {
//					int aux = idx.getSymbol().indexOf("_");
//					String symbol;
//					if (aux != -1) {
//						symbol = idx.getSymbol().substring(0, aux);
//					} else {
//						symbol = idx.getSymbol();
//					}
//
//					if (idx.getDescription().contains("CFD")) {
//						String category = idx.getCategoryName().concat(" CFD");
//						myFile.write(symbol + " " + category + " " + idx.getDescription() +"\n");
//					} else {
//						myFile.write(symbol + " " + idx.getCategoryName() + " " + idx.getDescription() +"\n");
//					}
//				}
//
//				LinkedList<String> list = new LinkedList<String>();
//				list.add("EURUSD");
//				list.add("AGK.UK_9");
//
//				TickPricesResponse resp = APICommandFactory.executeTickPricesCommand(connector, 0L, list, 0L);
//				for (TickRecord tr : resp.getTicks()) {
//					System.out.println("TickPrices result: "+tr.getSymbol() + " - ask: " + tr.getAsk());
//				}
//
//				CurrentUserDataResponse user = APICommandFactory.executeCurrentUserDataCommand(connector);
//				System.out.println(user);
//
//				connector.connectStream(sl);
//				System.out.println("Stream connected.");
//
//				connector.subscribeBalance();
//
//				for (String idx : list) {
//					System.out.println(idx);
//					connector.subscribePrice(idx);
//					SymbolResponse symResponse = APICommandFactory.executeSymbolCommand(connector, idx);
//					System.out.println(symResponse.toString());
//				}
//				connector.subscribeTrades();
//
//				Thread.sleep(10000);
//
//				for (String idx : list) {
//					connector.unsubscribePrice(idx);
//				}
//				connector.unsubscribeTrades();
//				connector.unsubscribeBalance();
//				connector.disconnectStream();
//
//				System.out.println("Stream disconnected.");
//				myFile.close();
//				Thread.sleep(5000);
//
//				connector.connectStream(sl);
//				System.out.println("Stream connected again.");
//				connector.disconnectStream();
//				System.out.println("Stream disconnected again.");
//				System.exit(0);
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	protected Map<String,Server> getAvailableServers() {
		return ServerData.getProductionServers();
	}
}